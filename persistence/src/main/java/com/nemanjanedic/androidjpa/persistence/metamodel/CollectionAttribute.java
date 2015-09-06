package com.nemanjanedic.androidjpa.persistence.metamodel;

/**
 * Instances of the type <code>CollectionAttribute</code> represent persistent
 * <code>java.util.Collection</code>-valued
 * attributes.
 *
 * @param <X> The type the represented Collection belongs to
 * @param <E> The element type of the represented Collection
 */
public interface CollectionAttribute<X, E> extends PluralAttribute<X, java.util.Collection<E>, E> {

}
