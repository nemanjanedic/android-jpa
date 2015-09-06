/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import javax.persistence.metamodel.Type;

public class TypeImpl<X> implements Type<X> {

    private final PersistenceType persistenceType;

    private final Class<X> javaType;

    public TypeImpl(final PersistenceType persistenceType, final Class<X> javaType) {
        this.persistenceType = persistenceType;
        this.javaType = javaType;
    }

    @Override
    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    @Override
    public Class<X> getJavaType() {
        return javaType;
    }

}
