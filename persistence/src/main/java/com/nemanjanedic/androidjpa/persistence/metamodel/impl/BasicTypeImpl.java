/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import javax.persistence.metamodel.BasicType;
import javax.persistence.metamodel.Type;

public class BasicTypeImpl<X> extends TypeImpl<X> implements BasicType<X> {

    public BasicTypeImpl(final Class<X> javaType) {
        super(PersistenceType.BASIC, javaType);
    }

    public static <X> Type<X> createInstance(final Class<X> javaType) {
        return new BasicTypeImpl<X>(javaType);
    }

}
