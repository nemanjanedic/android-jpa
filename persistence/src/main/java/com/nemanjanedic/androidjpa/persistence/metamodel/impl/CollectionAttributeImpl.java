/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.CollectionAttribute;

import java.util.Collection;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type;

public class CollectionAttributeImpl<X, E> extends PluralAttributeImpl<X, Collection<E>, E>
        implements
        CollectionAttribute<X, E> {

    public CollectionAttributeImpl(final String name,
            final PersistentAttributeType persistentAttributeType,
            final ManagedTypeImpl<X> declaringType, final Class<Collection<E>> javaType,
            final boolean association,
            final boolean collection, final FetchType fetchType, final Class<E> elementJavaType,
            final Type<E> elementType, final SingularAttributeImpl<E, X> mappedByAttribute) {
        super(name, persistentAttributeType, declaringType, javaType, association, collection,
                fetchType,
                elementJavaType, elementType, mappedByAttribute);
    }

    @Override
    public CollectionType getCollectionType() {
        return CollectionType.COLLECTION;
    }

}
