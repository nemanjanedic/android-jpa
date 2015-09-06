/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package com.nemanjanedic.androidjpa.compiler.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * A utility to help writing Java Source code dynamically.
 *
 * Provides basic elements of Java Source Code e.g. Package, Class, Field, Method, Import,
 * Annotation, Argument.
 *
 * Mutator methods return the operating element for easy chaining.
 *
 * @author Pinaki Poddar
 * @since 2.0.0
 */
public class SourceCode {

    /**
     * List of Java Keywords and primitive types. Populated statically.
     */
    private static final ArrayList<String> reserved = new ArrayList<String>();

    private static final ArrayList<String> knownTypes = new ArrayList<String>();

    private static int TABSIZE = 4;

    private static final String SPACE = " ";

    private static final String BLANK = "";

    private static final String SEMICOLON = ";";

    public static final String COMMA = ",";

    public static final String DOT = ".";

    public static final String EQUAL = "=";

    public static final String QUOTE = "\"";

    private static final Delimiter BLOCK_DELIMITER = new Delimiter("{}");

    private static final Delimiter ARGS_DELIMITER = new Delimiter("()");

    private static final Delimiter PARAMS_DELIMITER = new Delimiter("<>");

    private List<Comment> comments;

    private final Package pkg;

    private final Class cls;

    private final Set<Import> imports = new TreeSet<Import>();

    /**
     * Create source code for a top-level class with given fully-qualified class name.
     */
    public SourceCode(final String c) {
        ClassName name = getOrCreateImport(c);
        cls = new Class(c);
        pkg = new Package(name.getPackageName());
    }

    /**
     * Gets the top level class represented by this receiver.
     */
    public Class getTopLevelClass() {
        return cls;
    }

    public Package getPackage() {
        return pkg;
    }

    /**
     * Sets the tab size. Tabs are always written as spaces.
     */
    public SourceCode setTabSize(final int t) {
        if (t > 0) {
            TABSIZE = Math.max(t, 8);
        }
        return this;
    }

    /**
     * Adds import to this source code. Adding an import may force the given class name to use its
     * full name if it is
     * hidden by other imports.
     *
     * @param name a ClassName instance
     * @return true if the import is added. ClassName starting with <code>java.lang.</code> is not
     * added.
     */
    private boolean addImport(final ClassName name) {
        name.getPackageName();
        for (Import i : imports) {
            if (i.getClassName().hides(name)) {
                i.getClassName().useFullName();
                name.useFullName();
            }
        }
        return imports.add(new Import(name));
    }

    /**
     * Get the class name instance for the given fully-qualified class name. If the given class name
     * is already
     * imported, then use the existing instance. Otherwise, creates a new instance and adds it to
     * list of imports.
     *
     * @param name fully-qualified name of a class
     * @return an existing class name instance or a new one.
     * @see #addImport(ClassName)
     * @see ClassName
     */
    public ClassName getOrCreateImport(final String name) {
        for (Import i : imports) {
            if (i.name.getFullName().equals(name)) {
                return i.name;
            }
        }
        ClassName imp = new ClassName(name);
        for (Import i : imports) {
            if (i.name.fullName.equals(imp.fullName)) {
                // skip adding import
                return imp;
            }
        }
        addImport(imp);
        return imp;
    }

    public SourceCode addComment(final boolean inline, final String... lines) {
        if (lines == null) {
            return this;
        }
        if (comments == null) {
            comments = new ArrayList<Comment>();
        }
        Comment comment = new Comment();
        comments.add(comment);
        comment.makeInline(inline);
        for (String line : lines) {
            // Handle long header comment lines...
            if (line.length() > 120 - 4) {
                String[] wrappedLines = wrap(line, 120 - 4);
                for (String w : wrappedLines) {
                    comment.append(w);
                }
            } else {
                comment.append(line);
            }
        }
        return this;
    }

    /**
     * Prints the class to the given Writer.
     */
    public void write(final PrintWriter out) {
        if (comments != null) {
            for (Comment comment : comments) {
                comment.write(out, 0);
                out.println();
            }
        }
        if (pkg != null) {
            pkg.write(out, 0);
            out.println();
        }
        for (Import imp : imports) {
            imp.write(out, 0);
        }
        out.println();
        cls.write(out, 0);
        out.flush();
    }

    /**
     * Outputs <code>tab</code> number of spaces.
     */
    static void tab(final PrintWriter out, final int tab) {
        for (int i = 0; i < tab * TABSIZE; i++) {
            out.print(SPACE);
        }
    }

    /**
     * Wraps the given string into lines of max length width at word boundaries
     */
    public static String[] wrap(final String longLine, final int width) {
        String[] words = longLine.split("\\ ");
        List<String> lines = new ArrayList<String>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (line.length() + w.length() < width) {
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(w);
            } else {
                lines.add(line.toString());
                line.setLength(0);
                line.append(w);
            }
        }
        lines.add(line.toString());
        return lines.toArray(new String[lines.size()]);
    }

    static void writeList(final PrintWriter out, final String header, final List<?> list) {
        writeList(out, header, list, new Delimiter(), false);
    }

    static void writeList(final PrintWriter out, final String header, final List<?> list,
            final Delimiter bracket,
            final boolean writeEmpty) {
        if (list == null || list.isEmpty()) {
            if (writeEmpty) {
                out.append(bracket.start).append(bracket.end);
            }
            return;
        }
        out.append(header);
        out.append(bracket.start);
        for (int i = 0; i < list.size(); i++) {
            out.append(list.get(i).toString());
            if (i != list.size() - 1) {
                out.append(COMMA);
            }
        }
        out.append(bracket.end);
    }

    public static String capitalize(final String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static boolean isValidToken(final String s) {
        return s != null && s.length() > 0 && !reserved.contains(s) && isJavaIdentifier(s);
    }

    public static boolean isKnownType(final String s) {
        return knownTypes.contains(s);
    }

    static boolean isEmpty(final String s) {
        return s == null || s.length() == 0;
    }

    static LinkedList<String> tokenize(final String s, final String delim) {
        StringTokenizer tokenizer = new StringTokenizer(s, delim, false);
        LinkedList<String> tokens = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    public static boolean isJavaIdentifier(final String s) {
        if (s == null || s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public enum ACCESS {
        PUBLIC, PROTECTED, PRIVATE
    }

    /**
     * Abstract element has a name, optional list of modifiers, annotations and arguments.
     */
    public abstract class Element<T> implements Comparable<Element<T>> {

        protected String name;

        protected ClassName type;

        protected ACCESS access;

        protected boolean isStatic;

        protected boolean isFinal;

        protected Comment comment;

        protected List<ClassName> params = new ArrayList<ClassName>();

        protected List<Annotation> annos = new ArrayList<Annotation>();

        protected Element(final String name, final ClassName type) {
            this.name = name;
            this.type = type;
        }

        public ClassName getType() {
            return type;
        }

        public Annotation addAnnotation(final String a) {
            Annotation an = new Annotation(a);
            annos.add(an);
            return an;
        }

        public Element<T> addParameter(final String param) {
            params.add(getOrCreateImport(param));
            return this;
        }

        @Override
        public int compareTo(final Element<T> other) {
            return name.compareTo(other.name);
        }

        @SuppressWarnings("unchecked")
        public T addComment(final boolean inline, final String... lines) {
            if (comment == null) {
                comment = new Comment();
            }
            comment.makeInline(inline);
            for (String line : lines) {
                comment.append(line);
            }
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T makePublic() {
            access = ACCESS.PUBLIC;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T makeProtected() {
            access = ACCESS.PROTECTED;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T makePrivate() {
            access = ACCESS.PRIVATE;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T makeStatic() {
            isStatic = true;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T makeFinal() {
            isFinal = true;
            return (T) this;
        }

        public void write(final PrintWriter out, final int tab) {
            if (comment != null) {
                comment.write(out, tab);
            }
            for (Annotation a : annos) {
                a.write(out, tab);
            }
            tab(out, tab);
            if (access != null) {
                out.append(access.toString().toLowerCase() + SPACE);
            }
            if (isStatic) {
                out.append("static" + SPACE);
            }
            if (isFinal) {
                out.append("final" + SPACE);
            }
        }
    }

    /**
     * Represent <code>class</code> declaration.
     */
    public class Class extends Element<Class> {

        private boolean isAbstract;

        private boolean isFinal;

        private ClassName superCls;

        private final List<ClassName> interfaces = new ArrayList<ClassName>();

        private final Set<Field> fields = new TreeSet<Field>();

        private final Set<Method> methods = new TreeSet<Method>();

        private final Set<Constructor> constructors = new TreeSet<Constructor>();

        private final List<StaticBlock> staticBlocks = new ArrayList<StaticBlock>();

        public Class(final String name) {
            super(name, getOrCreateImport(name));
            makePublic();
        }

        public Class setSuper(final String s) {
            superCls = getOrCreateImport(s);
            return this;
        }

        public Class addInterface(final String s) {
            interfaces.add(getOrCreateImport(s));
            return this;
        }

        public Class makeAbstract() {
            if (isFinal) {
                throw new IllegalArgumentException("Invalid source modifier");
            }

            isAbstract = true;
            return this;
        }

        @Override
        public Class makeFinal() {
            if (isAbstract) {
                throw new IllegalArgumentException("Invalid source modifier");
            }
            isFinal = true;
            return this;
        }

        /**
         * Adds getters and setters to every non-public field.
         */
        public Class markAsBean() {
            for (Field f : fields) {
                f.markAsBean();
            }
            return this;
        }

        public String getName() {
            return getType().getSimpleName();
        }

        public String getPackageName() {
            return getType().getPackageName();
        }

        public Field addField(final String name, final String type) {
            return addField(name, getOrCreateImport(type));
        }

        public Field addField(final String f, final ClassName type) {
            if (!isValidToken(f)) {
                throw new IllegalArgumentException("Invalid field token: " + f);
            }
            Field field = new Field(this, f, type);

            if (!fields.add(field)) {
                throw new IllegalArgumentException("Duplicate field: " + f);
            }
            return field;
        }

        public Method addMethod(final String m, final String retType) {
            return addMethod(m, getOrCreateImport(retType));
        }

        protected Method addMethod(final String m, final ClassName retType) {
            if (isEmpty(m) || !isValidToken(m)) {
                throw new IllegalArgumentException("Invalid method token: " + m);
            }
            Method method = new Method(m, retType);
            if (!methods.add(method)) {
                throw new IllegalArgumentException("Duplicate method token: " + m);
            }
            return method;
        }

        public Constructor addConstructor() {
            Constructor c = new Constructor(type.simpleName);
            if (!constructors.add(c)) {
                throw new IllegalArgumentException("Duplicate contractor.");
            }
            return c;
        }

        public StaticBlock addStaticBlock() {
            StaticBlock sb = new StaticBlock();
            staticBlocks.add(sb);
            return sb;
        }

        @Override
        public void write(final PrintWriter out, final int tab) {
            super.write(out, tab);
            if (isAbstract) {
                out.append("abstract ");
            }
            if (isFinal) {
                out.append("final ");
            }
            out.print("class ");
            out.print(type.simpleName);
            writeList(out, BLANK, params, PARAMS_DELIMITER, false);
            if (superCls != null) {
                out.print(" extends " + superCls + SPACE);
            }
            writeList(out, "implements ", interfaces);
            out.println(SPACE + BLOCK_DELIMITER.start);
            for (Field field : fields) {
                field.write(out, 1);
            }
            for (StaticBlock staticBlock : staticBlocks) {
                staticBlock.write(out, 1);
            }
            for (Constructor ctor : constructors) {
                ctor.write(out, 1);
            }
            for (Method method : methods) {
                method.write(out, 1);
            }
            out.println(BLOCK_DELIMITER.end);
        }

        @Override
        public String toString() {
            return getType().fullName;
        }
    }

    /**
     * Represents field declaration.
     */
    public class Field extends Element<Field> {

        private final Class owner;

        protected boolean isTransient;

        protected boolean isVolatile;

        Field(final Class owner, final String name, final ClassName type) {
            super(name, type);
            this.owner = owner;
        }

        /**
         * Adds bean-style getter setter method.
         */
        public Field markAsBean() {
            addGetter();
            addSetter();
            return this;
        }

        public Field addGetter() {
            owner.addMethod("get" + capitalize(name), type).makePublic()
                    .addCodeLine("return " + name);
            return this;
        }

        public Field addSetter() {
            owner.addMethod("set" + capitalize(name), "void").makePublic()
                    .addArgument(new Argument<ClassName, String>(type, name, SPACE))
                    .addCodeLine("this." + name + " = " + name);
            return this;
        }

        public void makeVolatile() {
            isVolatile = true;
        }

        public void makeTransient() {
            isTransient = true;
        }

        @Override
        public String toString() {
            return type + SPACE + name;
        }

        @Override
        public void write(final PrintWriter out, final int tab) {
            super.write(out, tab);
            if (isVolatile) {
                out.print("volatile ");
            }
            if (isTransient) {
                out.print("transient ");
            }
            out.print(type);
            writeList(out, BLANK, params, PARAMS_DELIMITER, false);
            out.println(SPACE + name + SEMICOLON);
        }

        @Override
        public boolean equals(final Object other) {
            if (other instanceof Field) {
                Field that = (Field) other;
                return name.equals(that.name);
            }
            return false;
        }
    }

    /**
     * Represents Method declaration.
     */
    public class Method extends Element<Method> {

        private boolean isAbstract;

        private final List<Argument<ClassName, String>> args
                = new ArrayList<Argument<ClassName, String>>();

        private final List<String> codeLines = new ArrayList<String>();

        int tabCount = 0;

        String tab = "";

        Method(final String n, final String t) {
            this(n, getOrCreateImport(t));
        }

        public Method(final String name, final ClassName returnType) {
            super(name, returnType);
            makePublic();
        }

        public Method addArgument(final Argument<ClassName, String> arg) {
            args.add(arg);
            return this;
        }

        public Method addArgument(final String className, final String argName) {
            ClassName cn = getOrCreateImport(className);
            args.add(new Argument<ClassName, String>(cn, argName, " "));
            return this;
        }

        public void setTab(final boolean inc) {
            if (inc) {
                tabCount++;
            } else {
                tabCount--;
            }
            tab = "";
            for (int i = 0; i < tabCount * TABSIZE; i++) {
                tab += SPACE;
            }
        }

        public Method addCodeLine(String line) {
            if (isAbstract) {
                throw new IllegalStateException("abstract method " + name + " can not have body");
            }
            // This doesn't handle try{ ... catch(){ if{
            if (line.endsWith("{") || line.endsWith("}")) {

            }
            if (!line.endsWith(SEMICOLON)
                    && !(line.isEmpty() || line.endsWith("{") || line.endsWith("}") || line
                    .startsWith("if"))) {
                line = line + SEMICOLON;
            }
            codeLines.add(tab + line);
            return this;
        }

        /**
         * if tabInc = true, the current line, and all following lines will be tabbed. If false, a
         * tab will be removed.
         */
        public Method addCodeLine(final String line, final boolean tabInc) {
            setTab(tabInc);
            return addCodeLine(line);
        }

        public Method makeAbstract() {
            if (codeLines.isEmpty()) {
                isAbstract = true;
            } else {
                throw new IllegalStateException(
                        "method " + name + " can not be abstract. It has a body");
            }
            return this;
        }

        @Override
        public String toString() {
            return type + SPACE + name;
        }

        @Override
        public void write(final PrintWriter out, final int tab) {
            out.println(BLANK);
            super.write(out, tab);
            if (isAbstract) {
                out.append("abstract ");
            }
            writeList(out, BLANK, params, PARAMS_DELIMITER, false);
            out.print(type + SPACE + name);
            writeList(out, BLANK, args, ARGS_DELIMITER, true);
            if (isAbstract) {
                out.println(SEMICOLON);
                return;
            }
            out.println(SPACE + BLOCK_DELIMITER.start);
            for (String line : codeLines) {
                tab(out, tab + 1);
                out.println(line);
            }
            tab(out, tab);
            out.println(BLOCK_DELIMITER.end);
        }

        @Override
        public boolean equals(final Object other) {
            if (other instanceof Method) {
                Method that = (Method) other;
                return name.equals(that.name) && args.equals(that.args);
            }
            return false;
        }
    }

    public class Constructor extends Element<Constructor> {

        private final List<Argument<ClassName, String>> args
                = new ArrayList<Argument<ClassName, String>>();

        private final List<String> codeLines = new ArrayList<String>();

        int tabCount = 0;

        String tab = "";

        public Constructor(final String name) {
            super(name, null);
            makePublic();
        }

        public Constructor addArgument(final Argument<ClassName, String> arg) {
            args.add(arg);
            return this;
        }

        public Constructor addArgument(final String className, final String argName) {
            ClassName cn = getOrCreateImport(className);
            args.add(new Argument<ClassName, String>(cn, argName, " "));
            return this;
        }

        public Constructor addCodeLine(String line) {
            // This doesn't handle try{ ... catch(){ if{
            if (line.endsWith("{") || line.endsWith("}")) {

            }
            if (!line.endsWith(SEMICOLON)
                    && !(line.isEmpty() || line.endsWith("{") || line.endsWith("}") || line
                    .startsWith("if"))) {
                line = line + SEMICOLON;
            }
            codeLines.add(tab + line);
            return this;
        }

        /**
         * if tabInc = true, the current line, and all following lines will be tabbed. If false, a
         * tab will be removed.
         */
        public Constructor addCodeLine(final String line, final boolean tabInc) {
            setTab(tabInc);
            return addCodeLine(line);
        }

        public void setTab(final boolean inc) {
            if (inc) {
                tabCount++;
            } else {
                tabCount--;
            }
            tab = "";
            for (int i = 0; i < tabCount * TABSIZE; i++) {
                tab += SPACE;
            }
        }

        @Override
        public void write(final PrintWriter out, final int tab) {
            out.println(BLANK);
            super.write(out, tab);
            out.print(name);
            writeList(out, BLANK, args, ARGS_DELIMITER, true);

            out.println(SPACE + BLOCK_DELIMITER.start);
            for (String line : codeLines) {
                tab(out, tab + 1);
                out.println(line);
            }
            tab(out, tab);
            out.println(BLOCK_DELIMITER.end);
        }

    }

    public class StaticBlock extends Element<StaticBlock> {

        private final List<String> codeLines = new ArrayList<String>();

        int tabCount = 0;

        String tab = "";

        public StaticBlock() {
            super(null, null);
            makeStatic();
        }

        public StaticBlock addCodeLine(String line) {
            // This doesn't handle try{ ... catch(){ if{
            if (line.endsWith("{") || line.endsWith("}")) {

            }
            if (!line.endsWith(SEMICOLON)
                    && !(line.isEmpty() || line.endsWith("{") || line.endsWith("}") || line
                    .startsWith("if"))) {
                line = line + SEMICOLON;
            }
            codeLines.add(tab + line);
            return this;
        }

        /**
         * if tabInc = true, the current line, and all following lines will be tabbed. If false, a
         * tab will be removed.
         */
        public StaticBlock addCodeLine(final String line, final boolean tabInc) {
            setTab(tabInc);
            return addCodeLine(line);
        }

        public void setTab(final boolean inc) {
            if (inc) {
                tabCount++;
            } else {
                tabCount--;
            }
            tab = "";
            for (int i = 0; i < tabCount * TABSIZE; i++) {
                tab += SPACE;
            }
        }

        @Override
        public void write(final PrintWriter out, final int tab) {
            out.println(BLANK);
            super.write(out, tab);

            out.println(SPACE + BLOCK_DELIMITER.start);
            for (String line : codeLines) {
                tab(out, tab + 1);
                out.println(line);
            }
            tab(out, tab);
            out.println(BLOCK_DELIMITER.end);
        }

    }

    /**
     * Represents <code>import</code> statement.
     */
    class Import implements Comparable<Import> {

        private final ClassName name;

        public Import(final ClassName name) {
            this.name = name;
        }

        @Override
        public int compareTo(final Import other) {
            return name.compareTo(other.name);
        }

        public void write(final PrintWriter out, final int tab) {
            if (name.usingFullName()) {
                return;
            }
            String pkg = name.getPackageName();
            if (pkg.length() == 0 || pkg.equals(getPackage().name)) {
                return;
            }
            out.println("import " + name.fullName + SEMICOLON);
        }

        @Override
        public boolean equals(final Object other) {
            if (other instanceof Import) {
                Import that = (Import) other;
                return name.fullName.equals(that.name.fullName);
            }
            return false;
        }

        ClassName getClassName() {
            return name;
        }
    }

    /**
     * Represents method argument.
     */
    public class Argument<K, V> {

        final private K key;

        final private V value;

        final private String connector;

        Argument(final K key, final V value, final String connector) {
            this.key = key;
            this.value = value;
            this.connector = connector;
        }

        @Override
        public String toString() {
            return key + connector + value;
        }
    }

    /**
     * Represents annotation.
     */
    public class Annotation {

        private final String name;

        private final List<Argument<?, ?>> args = new ArrayList<Argument<?, ?>>();

        Annotation(final String n) {
            name = n;
        }

        public Annotation addArgument(final String key, final String v, final boolean quote) {
            return addArgument(new Argument<String, String>(key, quote ? quote(v) : v, EQUAL));
        }

        public Annotation addArgument(final String key, final String v) {
            return addArgument(key, v, true);
        }

        public Annotation addArgument(final String key, final String[] vs) {
            StringBuilder tmp = new StringBuilder(BLOCK_DELIMITER.start);
            for (int i = 0; i < vs.length; i++) {
                tmp.append(quote(vs[i]));
                tmp.append(i != vs.length - 1 ? COMMA : BLANK);
            }
            tmp.append(BLOCK_DELIMITER.end);
            return addArgument(key, tmp.toString(), false);
        }

        public <K, V> Annotation addArgument(final Argument<K, V> arg) {
            args.add(arg);
            return this;
        }

        public void write(final PrintWriter out, final int tab) {
            tab(out, tab);
            out.println("@" + name);
            writeList(out, BLANK, args, ARGS_DELIMITER, false);
            out.println();
        }

        String quote(final String s) {
            return QUOTE + s + QUOTE;
        }
    }

    static class Package {

        private final String name;

        Package(final String p) {
            name = p;
        }

        public void write(final PrintWriter out, final int tab) {
            if (name != null && !name.isEmpty()) {
                out.println("package " + name + SEMICOLON);
            }
        }
    }

    class Comment {

        List<String> lines = new ArrayList<String>();

        private boolean inline = false;

        public void append(final String line) {
            lines.add(line);
        }

        boolean isEmpty() {
            return lines.isEmpty();
        }

        void makeInline(final boolean flag) {
            inline = flag;
        }

        public void write(final PrintWriter out, final int tab) {
            if (inline) {
                for (String l : lines) {
                    tab(out, tab);
                    out.println("// " + l);
                }
            } else {
                int i = 0;
                for (String l : lines) {
                    tab(out, tab);
                    if (i == 0) {
                        out.println("/** ");
                        tab(out, tab);
                    }
                    out.println(" *  " + l);
                    i++;
                }
                tab(out, tab);
                out.println("**/");
            }
        }
    }

    /**
     * Represents fully-qualified name of a Java type.
     *
     * NOTE: Do not construct directly unless necessary.
     *
     * @see SourceCode#getOrCreateImport(String)
     */
    public class ClassName implements Comparable<ClassName> {

        public final String fullName;

        public final String simpleName;

        public final String pkgName;

        private String arrayMarker = BLANK;

        private boolean useFullName = false;

        private final String paramName;

        ClassName(String name) {
            while (isArray(name)) {
                arrayMarker = arrayMarker + "[]";
                name = getComponentName(name);
            }
            int start = name.indexOf("<");
            int stop = name.lastIndexOf(">");
            if (start != -1 && stop != -1) {
                paramName = name.substring(start + 1, stop);
                name = name.substring(0, start) + name.substring(stop + 1);
            } else {
                paramName = null;
            }
            fullName = name;
            int dot = fullName.lastIndexOf(DOT);
            simpleName = (dot == -1) ? fullName : fullName.substring(dot + 1);
            pkgName = (dot == -1) ? BLANK : fullName.substring(0, dot);
            if (!isValidTypeName(name)) {
                throw new IllegalArgumentException("Invalid type name: " + name);
            }
        }

        /**
         * Gets fully qualified name of this receiver.
         */
        public String getFullName() {
            return fullName + (paramName != null ? "<" + paramName + ">" : "") + arrayMarker;
        }

        /**
         * Gets simple name of this receiver.
         */
        public String getSimpleName() {
            return simpleName + (paramName != null ? "<" + paramName + ">" : "") + arrayMarker;
        }

        /**
         * Gets the package name of this receiver. Default package name is represented as empty
         * string.
         */
        public String getPackageName() {
            return pkgName;
        }

        /**
         * Gets the full or simple name of this receiver based on useFullName flag.
         */
        @Override
        public String toString() {
            return (useFullName ? fullName : simpleName) + (paramName != null ? "<" + paramName
                    + ">" : "")
                    + arrayMarker;
        }

        /**
         * Compares by fully-qualified name.
         */
        @Override
        public int compareTo(final ClassName other) {
            return getFullName().compareTo(other.getFullName());
        }

        public boolean isValidTypeName(final String s) {
            return isValidPackageName(pkgName) && (isKnownType(s) || isValidToken(simpleName));
        }

        boolean isValidPackageName(final String s) {
            if (isEmpty(s)) {
                return true;
            }
            LinkedList<String> tokens = tokenize(s, DOT);
            for (String token : tokens) {
                if (!isValidToken(token)) {
                    return false;
                }
            }
            return !s.endsWith(DOT);
        }

        boolean isArray(final String name) {
            return name.endsWith("[]");
        }

        String getComponentName(final String name) {
            return (!isArray(name)) ? name : name.substring(0, name.length() - "[]".length());
        }

        boolean hides(final ClassName other) {
            return this.getSimpleName().equals(other.getSimpleName()) && !fullName
                    .equals(other.fullName);
        }

        void useFullName() {
            useFullName = true;
        }

        boolean usingFullName() {
            return useFullName;
        }

    }

    static class Delimiter {

        final char start;

        final char end;

        public Delimiter() {
            this(' ', ' ');
        }

        public Delimiter(final String pair) {
            this(pair.charAt(0), pair.charAt(1));
        }

        public Delimiter(final char start, final char end) {
            super();
            this.start = start;
            this.end = end;
        }
    }

    static {
        reserved.add("abstract");
        reserved.add("continue");
        reserved.add("for");
        reserved.add("new");
        reserved.add("switch");
        reserved.add("assert");
        reserved.add("default");
        reserved.add("goto");
        reserved.add("package");
        reserved.add("synchronized");
        reserved.add("boolean");
        reserved.add("do");
        reserved.add("if");
        reserved.add("private");
        reserved.add("this");
        reserved.add("break");
        reserved.add("double");
        reserved.add("implements");
        reserved.add("protected");
        reserved.add("throw");
        reserved.add("byte");
        reserved.add("else");
        reserved.add("import");
        reserved.add("public");
        reserved.add("throws");
        reserved.add("case");
        reserved.add("enum");
        reserved.add("instanceof");
        reserved.add("return");
        reserved.add("transient");
        reserved.add("catch");
        reserved.add("extends");
        reserved.add("int");
        reserved.add("short");
        reserved.add("try");
        reserved.add("char");
        reserved.add("final");
        reserved.add("interface");
        reserved.add("static");
        reserved.add("void");
        reserved.add("class");
        reserved.add("finally");
        reserved.add("long");
        reserved.add("strictfp");
        reserved.add("volatile");
        reserved.add("const");
        reserved.add("float");
        reserved.add("native");
        reserved.add("super");
        reserved.add("while");

        knownTypes.add("boolean");
        knownTypes.add("byte");
        knownTypes.add("char");
        knownTypes.add("double");
        knownTypes.add("float");
        knownTypes.add("int");
        knownTypes.add("long");
        knownTypes.add("short");
        knownTypes.add("void");
        knownTypes.add("String");
    }
}
