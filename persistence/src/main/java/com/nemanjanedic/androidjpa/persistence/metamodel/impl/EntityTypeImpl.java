/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.EntityType;

import java.util.Set;

public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {

    protected final String name;

    protected final String tableName;

    public EntityTypeImpl(final Class<X> javaType,
            final Set<AttributeImpl<X, ?>> declaredAttributes,
            final String name, final String tableName) {
        super(PersistenceType.ENTITY, javaType, declaredAttributes);
        this.name = name;
        this.tableName = tableName;
    }

    @Override
    public BindableType getBindableType() {
        return BindableType.ENTITY_TYPE;
    }

    @Override
    public Class<X> getBindableJavaType() {
        return getJavaType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
