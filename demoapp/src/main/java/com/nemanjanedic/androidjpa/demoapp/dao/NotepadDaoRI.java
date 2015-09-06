package com.nemanjanedic.androidjpa.demoapp.dao;

import com.nemanjanedic.androidjpa.persistence.dao.AbstractDao;
import com.nemanjanedic.androidjpa.persistence.metamodel.EntityType;
import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.BasicTypeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.EntityTypeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.SingularAttributeImpl;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;

public class NotepadDaoRI extends AbstractDao<Notepad> {

    static EntityTypeImpl<Notepad> metamodel;

    public static SingularAttribute<Notepad, Long> _id;

    public static SingularAttribute<Notepad, String> _name;

    public static SingularAttribute<Notepad, Long> _dateCreated;

    public static SingularAttribute<Notepad, Long> _dateModified;

    static {
        metamodel = new EntityTypeImpl<Notepad>(Notepad.class, null, "Notepad", "notepad");
        _id = new SingularAttributeImpl<>("id", PersistentAttributeType.BASIC, metamodel,
                Long.class,
                false, false, null, true, false, false, BasicTypeImpl.createInstance(
                Long.class), "id");
        _name = new SingularAttributeImpl<Notepad, String>("name", PersistentAttributeType.BASIC,
                metamodel,
                String.class, false, false, null, false, false, false,
                BasicTypeImpl.createInstance(String.class),
                "name");
        _dateCreated = new SingularAttributeImpl<Notepad, Long>("dateCreated",
                PersistentAttributeType.BASIC,
                metamodel, Long.class, false, false, null, false, false, false,
                BasicTypeImpl.createInstance(Long.class), "created");
        _dateModified = new SingularAttributeImpl<Notepad, Long>("dateModified",
                PersistentAttributeType.BASIC,
                metamodel, Long.class, false, false, null, false, false, false,
                BasicTypeImpl.createInstance(Long.class), "modified");
        AbstractDao.registerDao(new NotepadDaoRI());
    }

    @Override
    public EntityType<Notepad> getMetamodel() {
        return metamodel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttributeValue(final Notepad entity,
            final SingularAttribute<? super Notepad, T> attributeMetamodel) {
        if (_id.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getId();
        }
        if (_name.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getName();
        }
        if (_dateCreated.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getDateCreated();
        }
        if (_dateModified.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getDateModified();
        }
        return null;
    }

    @Override
    public <T> void setAttributeValue(final Notepad entity,
            final SingularAttribute<? super Notepad, T> attributeMetamodel, final T value) {
        if (_id.getName().equals(attributeMetamodel.getName())) {
            entity.setId((Long) value);
            return;
        }
        if (_name.getName().equals(attributeMetamodel.getName())) {
            entity.setName((String) value);
            return;
        }
        if (_dateCreated.getName().equals(attributeMetamodel.getName())) {
            entity.setDateCreated((Long) value);
            return;
        }
        if (_dateModified.getName().equals(attributeMetamodel.getName())) {
            entity.setDateModified((Long) value);
            return;
        }
    }

    @Override
    protected Notepad getNewEntitiyInstance() {
        return new Notepad();
    }

}
