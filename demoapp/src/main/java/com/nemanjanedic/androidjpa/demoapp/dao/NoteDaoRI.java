package com.nemanjanedic.androidjpa.demoapp.dao;

import com.nemanjanedic.androidjpa.persistence.dao.AbstractDao;
import com.nemanjanedic.androidjpa.persistence.metamodel.EntityType;
import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.BasicTypeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.EntityTypeImpl;
import com.nemanjanedic.androidjpa.persistence.metamodel.impl.SingularAttributeImpl;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;

public class NoteDaoRI extends AbstractDao<Note> {

    static EntityTypeImpl<Note> metamodel;

    public static SingularAttribute<Note, Long> _id;

    public static SingularAttribute<Note, String> _title;

    public static SingularAttribute<Note, String> _content;

    public static SingularAttribute<Note, Long> _dateCreated;

    public static SingularAttribute<Note, Long> _dateModified;

    public static SingularAttribute<Note, Notepad> _notepad;

    static {
        metamodel = new EntityTypeImpl<Note>(Note.class, null, "Notepad", "notepad");
        _id = new SingularAttributeImpl<>("id", PersistentAttributeType.BASIC, metamodel,
                Long.class, false,
                false, null, true, false, false, BasicTypeImpl.createInstance(
                Long.class), "id");
        _title = new SingularAttributeImpl<Note, String>("title", PersistentAttributeType.BASIC,
                metamodel,
                String.class, false, false, null, false, false, false,
                BasicTypeImpl.createInstance(String.class),
                "title");
        _content = new SingularAttributeImpl<Note, String>("content", PersistentAttributeType.BASIC,
                metamodel,
                String.class, false, false, null, false, false, true,
                BasicTypeImpl.createInstance(String.class),
                "content");
        _dateCreated = new SingularAttributeImpl<Note, Long>("dateCreated",
                PersistentAttributeType.BASIC, metamodel,
                Long.class, false, false, null, false, false, false,
                BasicTypeImpl.createInstance(Long.class),
                "created");
        _dateModified = new SingularAttributeImpl<Note, Long>("dateModified",
                PersistentAttributeType.BASIC, metamodel,
                Long.class, false, false, null, false, false, false,
                BasicTypeImpl.createInstance(Long.class),
                "modified");
        _notepad = new SingularAttributeImpl<Note, Notepad>("notepad",
                PersistentAttributeType.MANY_TO_ONE, metamodel,
                Notepad.class, false, false, null, false, false, false, NotepadDaoRI.metamodel,
                "notepad_id");
        AbstractDao.registerDao(new NoteDaoRI());
    }

    @Override
    public EntityType<Note> getMetamodel() {
        return metamodel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttributeValue(final Note entity,
            final SingularAttribute<? super Note, T> attributeMetamodel) {
        if (_id.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getId();
        }
        if (_title.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getTitle();
        }
        if (_content.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getContent();
        }
        if (_dateCreated.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getDateCreated();
        }
        if (_dateModified.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getDateModified();
        }
        if (_notepad.getName().equals(attributeMetamodel.getName())) {
            return (T) entity.getNotepad();
        }
        return null;
    }

    @Override
    public <T> void setAttributeValue(final Note entity,
            final SingularAttribute<? super Note, T> attributeMetamodel,
            final T value) {
        if (_id.getName().equals(attributeMetamodel.getName())) {
            entity.setId((Long) value);
            return;
        }
        if (_title.getName().equals(attributeMetamodel.getName())) {
            entity.setTitle((String) value);
            return;
        }
        if (_content.getName().equals(attributeMetamodel.getName())) {
            entity.setContent((String) value);
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
        if (_notepad.getName().equals(attributeMetamodel.getName())) {
            entity.setNotepad((Notepad) value);
            return;
        }
    }

    @Override
    protected Note getNewEntitiyInstance() {
        return new Note();
    }

}
