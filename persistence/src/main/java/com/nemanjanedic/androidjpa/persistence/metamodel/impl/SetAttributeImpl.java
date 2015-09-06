/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.SetAttribute;

import java.util.Set;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type;

public class SetAttributeImpl<X, E> extends PluralAttributeImpl<X, Set<E>, E> implements
        SetAttribute<X, E> {

    public SetAttributeImpl(final String name,
            final PersistentAttributeType persistentAttributeType,
            final ManagedTypeImpl<X> declaringType, final Class<Set<E>> javaType,
            final boolean association,
            final boolean collection, final FetchType fetchType, final Class<E> elementJavaType,
            final Type<E> elementType, final SingularAttributeImpl<E, X> mappedByAttribute) {
        super(name, persistentAttributeType, declaringType, javaType, association, collection,
                fetchType,
                elementJavaType, elementType, mappedByAttribute);
    }

    @Override
    public CollectionType getCollectionType() {
        return CollectionType.SET;
    }

}
