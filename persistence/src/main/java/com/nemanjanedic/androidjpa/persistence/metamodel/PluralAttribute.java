package com.nemanjanedic.androidjpa.persistence.metamodel;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type;

/**
 * Instances of the type <code>PluralAttribute</code> represent persistent collection-valued
 * attributes.
 *
 * @param <X> The type the represented collection belongs to
 * @param <C> The type of the represented collection
 * @param <E> The element type of the represented collection
 */
public interface PluralAttribute<X, C, E> extends Attribute<X, C>, Bindable<E> {

    /**
     * Return the collection type.
     *
     * @return collection type
     */
    CollectionType getCollectionType();

    /**
     * Return the type representing the element type of the collection.
     *
     * @return element type
     */
    Type<E> getElementType();

    /**
     * Returns the mapped by attribute.
     *
     * @return mapped by attribute
     */
    SingularAttribute<E, X> getMappedByAttribute();

}
