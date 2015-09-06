/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Type;

public class SingularAttributeImpl<X, Y> extends AttributeImpl<X, Y> implements
        SingularAttribute<X, Y> {

    private final boolean id;

    private final boolean version;

    private final boolean optional;

    private final Type<Y> type;

    private final String columnName;

    public SingularAttributeImpl(final String name,
            final PersistentAttributeType persistentAttributeType,
            final ManagedTypeImpl<X> declaringType, final Class<Y> javaType,
            final boolean association,
            final boolean collection, final FetchType fetchType, final boolean id,
            final boolean version,
            final boolean optional, final Type<Y> type, final String columnName) {
        super(name, persistentAttributeType, declaringType, javaType, association, collection,
                fetchType);
        this.id = id;
        this.version = version;
        this.optional = optional;
        this.type = type;
        this.columnName = columnName;
        if (id || version) {
            if (super.declaringType != null) {
                super.declaringType.addDeclaredAttribute(this);
            }
        }
    }

    @Override
    public BindableType getBindableType() {
        return BindableType.SINGULAR_ATTRIBUTE;
    }

    @Override
    public Class<Y> getBindableJavaType() {
        return getJavaType();
    }

    @Override
    public boolean isId() {
        return id;
    }

    @Override
    public boolean isVersion() {
        return version;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public Type<Y> getType() {
        return type;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

}
