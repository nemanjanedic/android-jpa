/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.PluralAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Type;

public abstract class PluralAttributeImpl<X, C, E> extends AttributeImpl<X, C> implements
        PluralAttribute<X, C, E> {

    private final Class<E> elementJavaType;

    private final Type<E> elementType;

    private final SingularAttributeImpl<E, X> mappedByAttribute;

    public PluralAttributeImpl(final String name,
            final PersistentAttributeType persistentAttributeType,
            final ManagedTypeImpl<X> declaringType, final Class<C> javaType,
            final boolean association,
            final boolean collection, final FetchType fetchType, final Class<E> elementJavaType,
            final Type<E> elementType, final SingularAttributeImpl<E, X> mappedByAttribute) {
        super(name, persistentAttributeType, declaringType, javaType, association, collection,
                fetchType);
        this.elementJavaType = elementJavaType;
        this.elementType = elementType;
        this.mappedByAttribute = mappedByAttribute;
    }

    @Override
    public BindableType getBindableType() {
        return BindableType.PLURAL_ATTRIBUTE;
    }

    @Override
    public Class<E> getBindableJavaType() {
        return elementJavaType;
    }

    @Override
    public Type<E> getElementType() {
        return elementType;
    }

    @Override
    public SingularAttribute<E, X> getMappedByAttribute() {
        return mappedByAttribute;
    }

}
