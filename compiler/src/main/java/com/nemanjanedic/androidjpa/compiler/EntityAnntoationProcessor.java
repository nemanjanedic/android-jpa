package com.nemanjanedic.androidjpa.compiler;

import com.google.auto.service.AutoService;

import com.nemanjanedic.androidjpa.compiler.util.AnnotationProcessingLogger;
import com.nemanjanedic.androidjpa.compiler.util.SourceCode;
import com.nemanjanedic.androidjpa.persistence.dao.AbstractDao;
import com.nemanjanedic.androidjpa.persistence.metamodel.CollectionAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.EntityType;
import com.nemanjanedic.androidjpa.persistence.metamodel.ListAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.MapAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.SetAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.BasicTypeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.CollectionAttributeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.EntityTypeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.ListAttributeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.MapAttributeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.SetAttributeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.SingularAttributeImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.tools.JavaFileObject;


@SupportedAnnotationTypes({"javax.persistence.Entity"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(EntityAnntoationProcessor.class)
public class EntityAnntoationProcessor extends AbstractProcessor {

    private static final String DAO_CLASS_NAME_SUFFIX = "Dao";

    private static final String METAMODEL_FIELD_PREFIX = "_";

    private AnnotationProcessingLogger logger;

    private SourceAnnotationHandler handler;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new AnnotationProcessingLogger(processingEnv, "INFO");
        handler = new SourceAnnotationHandler(processingEnv, logger);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            Set<? extends Element> elements = roundEnv.getRootElements();
            for (Element e : elements) {
                if (e instanceof TypeElement) {
                    process((TypeElement) e);
                }
            }
        }
        return false;
    }

    private boolean process(final TypeElement e) {
        if (!isAnnotatedAsEntity(e)) {
            return false;
        }

        Elements eUtils = processingEnv.getElementUtils();
        String originalClass = eUtils.getBinaryName(e).toString();
        String originalSimpleClass = e.getSimpleName().toString();
        String originalAsTypeParam = "<" + originalSimpleClass + ">";

        String daoClass = originalClass
                .replace(originalSimpleClass, originalSimpleClass + DAO_CLASS_NAME_SUFFIX);

        SourceCode source = new SourceCode(daoClass);
        try {
            PrintWriter writer = createSourceFile(originalClass, daoClass, e);

            Set<Element> persistentMembers = handler.getPersistentMembers(e);

            // add inheritance to AbstractDao
            source.getTopLevelClass()
                    .setSuper(AbstractDao.class.getCanonicalName() + originalAsTypeParam);

            // add metamodel field
            source.getTopLevelClass().addField("metamodel", EntityTypeImpl.class.getCanonicalName())
                    .addParameter(originalSimpleClass).makeStatic();

            // add getMetamodel method
            source.getTopLevelClass()
                    .addMethod("getMetamodel",
                            EntityType.class.getCanonicalName() + originalAsTypeParam).makePublic()
                    .addCodeLine("return metamodel;").addAnnotation(Override.class.getSimpleName());

            // add getNewEntitiyInstance method
            source.getTopLevelClass().addMethod("getNewEntitiyInstance", originalSimpleClass)
                    .makePublic()
                    .addCodeLine("return new " + originalSimpleClass + "();")
                    .addAnnotation(Override.class.getSimpleName());

            // add getAttributeValue method
            addGetAttributeValueMethod(source, originalSimpleClass, persistentMembers);

            // add setAttributeValue method
            addSetAttributeValueMethod(source, originalSimpleClass, persistentMembers);

            // add attribute metamodel fields
            for (Element member : persistentMembers) {
                TypeMirror decl = handler.getDeclaredType(member);
                String fieldName = handler.getPersistentMemberName(member);
                String fieldType = handler.getDeclaredTypeName(decl, true);
                JpaTypeCategory jpaTypeCategory = toMetaModelTypeCategory(decl, fieldType);

                switch (jpaTypeCategory) {
                    case ATTRIBUTE:
                        source.getTopLevelClass()
                                .addField(METAMODEL_FIELD_PREFIX + fieldName,
                                        jpaTypeCategory.getMetaModelType())
                                .addParameter(originalSimpleClass).addParameter(fieldType)
                                .makeStatic().makePublic();
                    case LIST:
                    case COLLECTION:
                    case SET:
                    case MAP:
                        // skip collection attributes
                        continue;
                }
            }

            // add static initialization block
            String tableName = originalSimpleClass;
            if (SourceAnnotationHandler.isAnnotatedWith(e, Table.class)) {
                String annotatedTableName = (String) SourceAnnotationHandler
                        .getAnnotationValue(e, Table.class, "name");
                if (annotatedTableName != null && !annotatedTableName.isEmpty()) {
                    tableName = annotatedTableName;
                }
            }
            addStaticBlock(source, originalSimpleClass, tableName, persistentMembers);

            source.write(writer);
            writer.flush();
            writer.close();
        } catch (IOException e1) {
            logger.error("Cannot create source file for input class " + originalClass, e1);
        }

        return true;
    }

    private void addGetAttributeValueMethod(final SourceCode source,
            final String originalSimpleClass,
            final Set<Element> persistentMembers) {
        SourceCode.Method method = source.getTopLevelClass().addMethod("getAttributeValue", "T");
        method.addParameter("T");
        method.addAnnotation(Override.class.getSimpleName());
        method.addAnnotation(SuppressWarnings.class.getSimpleName())
                .addArgument("value", "unchecked");
        method.addArgument(originalSimpleClass, "entity");
        method.addArgument(
                SingularAttribute.class.getCanonicalName() + "<? super " + originalSimpleClass
                        + ", T>",
                "attributeMetamodel");

        for (Element member : persistentMembers) {
            TypeMirror decl = handler.getDeclaredType(member);
            String fieldName = handler.getPersistentMemberName(member);
            String getterName = (decl.getKind() == TypeKind.BOOLEAN ? "is" : "get") + SourceCode
                    .capitalize(fieldName);

            method.addCodeLine("if (" + METAMODEL_FIELD_PREFIX + fieldName
                    + ".getName().equals(attributeMetamodel.getName())) {");
            method.addCodeLine("return (T) entity." + getterName + "();", true);
            method.addCodeLine("}", false);
        }
        method.addCodeLine("return null;");
    }

    private void addSetAttributeValueMethod(final SourceCode source,
            final String originalSimpleClass,
            final Set<Element> persistentMembers) {
        SourceCode.Method method = source.getTopLevelClass().addMethod("setAttributeValue", "void");
        method.addParameter("T");
        method.addAnnotation(Override.class.getSimpleName());
        method.addArgument(originalSimpleClass, "entity");
        method.addArgument(
                SingularAttribute.class.getCanonicalName() + "<? super " + originalSimpleClass
                        + ", T>",
                "attributeMetamodel");
        method.addArgument("T", "value");

        for (Element member : persistentMembers) {
            TypeMirror decl = handler.getDeclaredType(member);
            String fieldName = handler.getPersistentMemberName(member);
            String fieldType = handler.getDeclaredTypeName(decl, true);
            String setterName = "set" + SourceCode.capitalize(fieldName);

            method.addCodeLine("if (" + METAMODEL_FIELD_PREFIX + fieldName
                    + ".getName().equals(attributeMetamodel.getName())) {");
            method.addCodeLine("entity." + setterName + "((" + fieldType + ") value);", true);
            method.addCodeLine("return;");
            method.addCodeLine("}", false);
        }
    }

    private void addStaticBlock(final SourceCode source, final String originalSimpleClass,
            final String tableName,
            final Set<Element> persistentMembers) {
        SourceCode.StaticBlock block = source.getTopLevelClass().addStaticBlock();
        block.addCodeLine(
                "metamodel = new EntityTypeImpl<" + originalSimpleClass + ">(" + originalSimpleClass
                        + ".class, null, \"" + originalSimpleClass + "\", \"" + tableName + "\");");

        for (Element member : persistentMembers) {
            TypeMirror decl = handler.getDeclaredType(member);
            String fieldName = handler.getPersistentMemberName(member);
            String fieldType = handler.getDeclaredTypeName(decl, true);
            JpaTypeCategory jpaTypeCategory = toMetaModelTypeCategory(decl, fieldType);

            insertMetamodelAttributeInitialization(source, originalSimpleClass, block, member,
                    fieldName, fieldType,
                    jpaTypeCategory);
        }

        block.addCodeLine(
                "AbstractDao.registerDao(new " + originalSimpleClass + DAO_CLASS_NAME_SUFFIX
                        + "());");
    }

    private void insertMetamodelAttributeInitialization(final SourceCode source,
            final String originalSimpleClass,
            final SourceCode.StaticBlock block, final Element member, final String fieldName,
            final String fieldType,
            final JpaTypeCategory jpaTypeCategory) {
        // default values
        String metamodelFieldType = "null";
        PersistentAttributeType persistentAttributeType = null;
        String columnName = fieldName;
        boolean association = false;
        boolean collection = false;
        boolean id = SourceAnnotationHandler.isAnnotatedWith(member, Id.class);
        boolean version = SourceAnnotationHandler.isAnnotatedWith(member, Id.class);
        boolean optional = false;
        FetchType fetchType = null;
        String typeString = "null";

        switch (jpaTypeCategory) {
            case ATTRIBUTE:
                metamodelFieldType = source.getOrCreateImport(
                        jpaTypeCategory.getMetaModelImpl() + "<" + originalSimpleClass + ", "
                                + fieldType + ">")
                        .getSimpleName();
                if (SourceAnnotationHandler.isAnnotatedWith(member, Column.class)) {
                    persistentAttributeType = PersistentAttributeType.BASIC;
                    String columnNameDecl = (String) SourceAnnotationHandler
                            .getAnnotationValue(member, Column.class,
                                    "name");
                    if (columnNameDecl != null && !columnNameDecl.isEmpty()) {
                        columnName = columnNameDecl;
                    }
                    Boolean optionalDecl = (Boolean) SourceAnnotationHandler
                            .getAnnotationValue(member, Column.class,
                                    "nullable");
                    if (optionalDecl != null) {
                        optional = optionalDecl;
                    }
                    typeString = "BasicTypeImpl.createInstance(" + fieldType + ".class)";
                    source.getOrCreateImport(BasicTypeImpl.class.getCanonicalName());
                } else if (SourceAnnotationHandler.isAnnotatedWith(member, ManyToOne.class)) {
                    persistentAttributeType = PersistentAttributeType.MANY_TO_ONE;
                    String columnNameDecl = (String) SourceAnnotationHandler
                            .getAnnotationValue(member, JoinColumn.class,
                                    "name");
                    if (columnNameDecl != null && !columnNameDecl.isEmpty()) {
                        columnName = columnNameDecl;
                    }
                    Boolean optionalDecl = (Boolean) SourceAnnotationHandler
                            .getAnnotationValue(member, ManyToOne.class,
                                    "optional");
                    if (optionalDecl != null) {
                        optional = optionalDecl;
                    }
                    fetchType = (FetchType) SourceAnnotationHandler
                            .getAnnotationValue(member, ManyToOne.class, "fetch");
                    if (fetchType == null) {
                        fetchType = FetchType.EAGER;
                    }
                    typeString = fieldType + DAO_CLASS_NAME_SUFFIX + ".metamodel";
                } else if (SourceAnnotationHandler.isAnnotatedWith(member, OneToOne.class)) {
                    persistentAttributeType = PersistentAttributeType.MANY_TO_ONE;
                    String columnNameDecl = (String) SourceAnnotationHandler
                            .getAnnotationValue(member, JoinColumn.class,
                                    "name");
                    if (columnNameDecl != null && !columnNameDecl.isEmpty()) {
                        columnName = columnNameDecl;
                    }
                    Boolean optionalDecl = (Boolean) SourceAnnotationHandler
                            .getAnnotationValue(member, OneToOne.class,
                                    "optional");
                    if (optionalDecl != null) {
                        optional = optionalDecl;
                    }
                    fetchType = (FetchType) SourceAnnotationHandler
                            .getAnnotationValue(member, ManyToOne.class, "fetch");
                    if (fetchType == null) {
                        fetchType = FetchType.EAGER;
                    }
                    typeString = fieldType + DAO_CLASS_NAME_SUFFIX + ".metamodel";
                }
                break;

            case LIST:
            case COLLECTION:
            case SET:
            case MAP:
                // skip collection attributes
                return;
        }

        String persistentAttributeTypeString = (persistentAttributeType == null) ? "null"
                : PersistentAttributeType.class.getCanonicalName() + "." + persistentAttributeType
                        .name();
        String fetchTypeString = (fetchType == null) ? "null"
                : FetchType.class.getCanonicalName() + "."
                        + fetchType.name();

        block.addCodeLine(
                METAMODEL_FIELD_PREFIX + fieldName + " = new " + metamodelFieldType + "(\""
                        + fieldName
                        + "\", " + persistentAttributeTypeString + ", metamodel, " + fieldType
                        + ".class, "
                        + Boolean.toString(association) + ", " + Boolean.toString(collection) + ", "
                        + fetchTypeString + ", "
                        + Boolean.toString(id) + ", " + Boolean.toString(version) + ", " + Boolean
                        .toString(optional) + ", "
                        + typeString + ", \"" + columnName + "\");");
    }

    private boolean isAnnotatedAsEntity(final TypeElement e) {
        return SourceAnnotationHandler.isAnnotatedWith(e, Entity.class);
    }

    private PrintWriter createSourceFile(final String originalClass, final String daoClass,
            final TypeElement e)
            throws IOException {
        JavaFileObject javaFile = processingEnv.getFiler().createSourceFile(daoClass, e);
        return new PrintWriter(javaFile.openWriter());
    }

    private enum JpaTypeCategory {
        ATTRIBUTE(SingularAttribute.class.getCanonicalName(),
                SingularAttributeImpl.class.getCanonicalName()),
        COLLECTION(CollectionAttribute.class.getCanonicalName(),
                CollectionAttributeImpl.class.getCanonicalName()),
        SET(SetAttribute.class.getCanonicalName(),
                SetAttributeImpl.class.getCanonicalName()),
        LIST(ListAttribute.class.getCanonicalName(),
                ListAttributeImpl.class.getCanonicalName()),
        MAP(MapAttribute.class.getCanonicalName(),
                MapAttributeImpl.class.getCanonicalName());

        private final String type;

        private final String impl;

        JpaTypeCategory(final String type, final String impl) {
            this.type = type;
            this.impl = impl;
        }

        public String getMetaModelType() {
            return type;
        }

        public String getMetaModelImpl() {
            return impl;
        }
    }

    private static List<String> CLASSNAMES_LIST = Arrays.asList("java.util.List",
            "java.util.AbstractList", "java.util.AbstractSequentialList", "java.util.ArrayList",
            "java.util.Stack",
            "java.util.Vector");

    private static List<String> CLASSNAMES_SET = Arrays
            .asList("java.util.Set", "java.util.AbstractSet",
                    "java.util.EnumSet", "java.util.HashSet", "java.util.LinkedList",
                    "java.util.LinkedHashSet",
                    "java.util.SortedSet", "java.util.TreeSet");

    private static List<String> CLASSNAMES_MAP = Arrays
            .asList("java.util.Map", "java.util.AbstractMap",
                    "java.util.EnumMap", "java.util.HashMap", "java.util.Hashtable",
                    "java.util.IdentityHashMap",
                    "java.util.LinkedHashMap", "java.util.Properties", "java.util.SortedMap",
                    "java.util.TreeMap");

    private static List<String> CLASSNAMES_COLLECTION = Arrays
            .asList("java.util.Collection",
                    "java.util.AbstractCollection", "java.util.AbstractQueue", "java.util.Queue",
                    "java.util.PriorityQueue");

    private JpaTypeCategory toMetaModelTypeCategory(final TypeMirror mirror,
            final String fieldType) {
        if (CLASSNAMES_COLLECTION.contains(fieldType)) {
            return JpaTypeCategory.COLLECTION;
        }
        if (CLASSNAMES_LIST.contains(fieldType)) {
            return JpaTypeCategory.LIST;
        }
        if (CLASSNAMES_SET.contains(fieldType)) {
            return JpaTypeCategory.SET;
        }
        if (CLASSNAMES_MAP.contains(fieldType)) {
            return JpaTypeCategory.MAP;
        }
        return JpaTypeCategory.ATTRIBUTE;
    }

}
