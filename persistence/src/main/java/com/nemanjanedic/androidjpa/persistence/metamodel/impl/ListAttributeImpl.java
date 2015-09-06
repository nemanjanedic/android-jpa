/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.ListAttribute;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type;

public class ListAttributeImpl<X, E> extends PluralAttributeImpl<X, List<E>, E> implements
        ListAttribute<X, E> {

    public ListAttributeImpl(final String name,
            final PersistentAttributeType persistentAttributeType,
            final ManagedTypeImpl<X> declaringType, final Class<List<E>> javaType,
            final boolean association,
            final boolean collection, final FetchType fetchType, final Class<E> elementJavaType,
            final Type<E> elementType, final SingularAttributeImpl<E, X> mappedByAttribute) {
        super(name, persistentAttributeType, declaringType, javaType, association, collection,
                fetchType,
                elementJavaType, elementType, mappedByAttribute);
    }

    @Override
    public CollectionType getCollectionType() {
        return CollectionType.LIST;
    }

}
