package com.nemanjanedic.androidjpa.persistence.dao;

import com.nemanjanedic.androidjpa.persistence.metamodel.EntityType;
import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;

public abstract class AbstractDao<X> {

    public abstract EntityType<X> getMetamodel();

    public abstract <T> T getAttributeValue(X entity,
            SingularAttribute<? super X, T> attributeMetamodel);

    public abstract <T> void setAttributeValue(X entity,
            SingularAttribute<? super X, T> attributeMetamodel, T value);

    protected abstract X getNewEntitiyInstance();

    protected static <X> void registerDao(final AbstractDao<X> instance) {
        DaoManager.getInstance().registerDao(instance.getMetamodel().getJavaType(), instance);
    }

    public X save(final X entity, final SQLiteDatabase database) {
        String tableName = getMetamodel().getTableName();
        ContentValues values = getContentValues(entity);
        Long id = getIdValue(entity);

        if (id == null) {
            id = database.insert(tableName, null, values);
            setIdValue(entity, id);
        } else {
            String whereClause = getMetamodel().getId().getColumnName() + " = ?";
            database.update(tableName, values, whereClause, new String[]{id.toString()});
        }

        return entity;
    }

    public void delete(final X entity, final SQLiteDatabase database) {
        String whereClause = getMetamodel().getId().getColumnName() + " = ?";
        database.delete(getMetamodel().getTableName(), whereClause,
                new String[]{getIdValue(entity).toString()});
    }

    public X findOne(final Long id, final SQLiteDatabase database) {
        String selection = getMetamodel().getId().getColumnName() + " = ?";
        Cursor cursor = database
                .query(false, getMetamodel().getTableName(), getAllColumns(), selection,
                        new String[]{id.toString()}, null, null, null, "LIMIT 1");
        if (cursor.moveToFirst()) {
            return getEntityFromCursor(cursor, database);
        }
        return null;
    }

    public List<X> findAll(final SQLiteDatabase database) {
        ArrayList<X> result = new ArrayList<X>();
        Cursor cursor = database
                .query(getMetamodel().getTableName(), getAllColumns(), null, null, null, null,
                        null);
        if (cursor.moveToFirst()) {
            do {
                result.add(getEntityFromCursor(cursor, database));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public Long getIdValue(final X entity) {
        return getAttributeValue(entity, getMetamodel().getId());
    }

    public void setIdValue(final X entity, final Long value) {
        setAttributeValue(entity, getMetamodel().getId(), value);
    }

    protected String[] getAllColumns() {
        ArrayList<String> columns = new ArrayList<String>();
        for (SingularAttribute<? super X, ?> attribute : getMetamodel().getSingularAttributes()) {
            switch (attribute.getPersistentAttributeType()) {
                case BASIC:
                case MANY_TO_ONE:
                case ONE_TO_ONE:
                    columns.add(attribute.getColumnName());
                    break;
                default:
                    break;
            }
        }
        return columns.toArray(new String[columns.size()]);
    }

    protected ContentValues getContentValues(final X entity) {
        ContentValues values = new ContentValues();
        for (SingularAttribute<? super X, ?> attribute : getMetamodel().getSingularAttributes()) {
            switch (attribute.getPersistentAttributeType()) {
                case BASIC:
                    insertContentValueItem(values, entity, attribute);
                    break;
                case MANY_TO_ONE:
                case ONE_TO_ONE:
                    insertReferenceContentValueItem(values, entity, attribute);
                    break;
                default:
                    break;
            }
        }
        return values;
    }

    private void insertContentValueItem(final ContentValues values, final X entity,
            final SingularAttribute<? super X, ?> attribute) {
        String columnName = attribute.getColumnName();
        if (attribute.getJavaType().equals(String.class)) {
            values.put(columnName, (String) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Byte.class)) {
            values.put(columnName, (Byte) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Short.class)) {
            values.put(columnName, (Short) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Integer.class)) {
            values.put(columnName, (Integer) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Long.class)) {
            values.put(columnName, (Long) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Float.class)) {
            values.put(columnName, (Float) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Double.class)) {
            values.put(columnName, (Double) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Boolean.class)) {
            values.put(columnName, (Boolean) getAttributeValue(entity, attribute));
        } else if (attribute.getJavaType().equals(Byte[].class)) {
            values.put(columnName, (byte[]) getAttributeValue(entity, attribute));
        } else {
            Object value = getAttributeValue(entity, attribute);
            if (value != null) {
                values.put(columnName, value.toString());
            }
        }
    }

    private <T> void insertReferenceContentValueItem(final ContentValues values, final X entity,
            final SingularAttribute<? super X, T> attribute) {
        AbstractDao<T> dao = DaoManager.getInstance().getDao(attribute.getJavaType());
        T referencedEntity = getAttributeValue(entity, attribute);
        if (referencedEntity != null) {
            Long referencedId = dao.getIdValue(referencedEntity);
            values.put(attribute.getColumnName(), referencedId);
        } else {
            values.putNull(attribute.getColumnName());
        }
    }

    protected X getEntityFromCursor(final Cursor cursor, final SQLiteDatabase database) {
        X entity = getNewEntitiyInstance();
        for (SingularAttribute<? super X, ?> attribute : getMetamodel().getSingularAttributes()) {
            switch (attribute.getPersistentAttributeType()) {
                case BASIC:
                    updateEntityAttribute(cursor, entity, attribute);
                    break;
                case MANY_TO_ONE:
                case ONE_TO_ONE:
                    if (!FetchType.LAZY.equals(attribute.getFetchType())) {
                        updateReferenceEntityAttribute(cursor, entity, attribute, database);
                    }
                    break;
                default:
                    break;
            }
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    private void updateEntityAttribute(final Cursor cursor, final X entity,
            final SingularAttribute<? super X, ?> attribute) {
        int columnIndex = cursor.getColumnIndex(attribute.getColumnName());
        if (attribute.getJavaType().equals(String.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, String>) attribute,
                    cursor.getString(columnIndex));
        } else if (attribute.getJavaType().equals(Byte.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, Byte>) attribute,
                    Byte.valueOf(cursor.getString(columnIndex)));
        } else if (attribute.getJavaType().equals(Short.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, Short>) attribute,
                    cursor.getShort(columnIndex));
        } else if (attribute.getJavaType().equals(Integer.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, Integer>) attribute,
                    cursor.getInt(columnIndex));
        } else if (attribute.getJavaType().equals(Long.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, Long>) attribute,
                    cursor.getLong(columnIndex));
        } else if (attribute.getJavaType().equals(Float.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, Float>) attribute,
                    cursor.getFloat(columnIndex));
        } else if (attribute.getJavaType().equals(Double.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, Double>) attribute,
                    cursor.getDouble(columnIndex));
        } else if (attribute.getJavaType().equals(Boolean.class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, Boolean>) attribute,
                    Boolean.valueOf(cursor.getString(columnIndex)));
        } else if (attribute.getJavaType().equals(Byte[].class)) {
            setAttributeValue(entity, (SingularAttribute<? super X, byte[]>) attribute,
                    cursor.getBlob(columnIndex));
        }
    }

    private <T> void updateReferenceEntityAttribute(final Cursor cursor, final X entity,
            final SingularAttribute<? super X, T> attribute, final SQLiteDatabase database) {
        int columnIndex = cursor.getColumnIndex(attribute.getColumnName());
        Long referenceId = cursor.getLong(columnIndex);
        if (referenceId != null) {
            AbstractDao<T> dao = DaoManager.getInstance().getDao(attribute.getBindableJavaType());
            T referencedEntity = dao.findOne(referenceId, database);
            if (referencedEntity != null) {
                setAttributeValue(entity, attribute, referencedEntity);
            }
        }
    }

}