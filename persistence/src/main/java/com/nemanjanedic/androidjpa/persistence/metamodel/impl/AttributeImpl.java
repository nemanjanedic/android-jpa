/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.Attribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.ManagedType;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

public class AttributeImpl<X, Y> implements Attribute<X, Y> {

    private final String name;

    private final PersistentAttributeType persistentAttributeType;

    protected final ManagedTypeImpl<X> declaringType;

    private final Class<Y> javaType;

    private final boolean association;

    private final boolean collection;

    private final FetchType fetchType;

    public AttributeImpl(final String name,
            final PersistentAttributeType persistentAttributeType,
            final ManagedTypeImpl<X> declaringType, final Class<Y> javaType,
            final boolean association,
            final boolean collection, final FetchType fetchType) {
        this.name = name;
        this.persistentAttributeType = persistentAttributeType;
        this.declaringType = declaringType;
        this.javaType = javaType;
        this.association = association;
        this.collection = collection;
        this.fetchType = fetchType;
        if (this.declaringType != null) {
            this.declaringType.addDeclaredAttribute(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PersistentAttributeType getPersistentAttributeType() {
        return persistentAttributeType;
    }

    @Override
    public ManagedType<X> getDeclaringType() {
        return declaringType;
    }

    @Override
    public Class<Y> getJavaType() {
        return javaType;
    }

    @Override
    public boolean isAssociation() {
        return association;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }

}
