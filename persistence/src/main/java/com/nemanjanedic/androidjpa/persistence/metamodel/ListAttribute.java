package com.nemanjanedic.androidjpa.persistence.metamodel;

/**
 * Instances of the type <code>ListAttribute</code> represent persistent
 * <code>javax.util.List</code>-valued attributes.
 *
 * @param <X> The type the represented List belongs to
 * @param <E> The element type of the represented List
 */
public interface ListAttribute<X, E> extends PluralAttribute<X, java.util.List<E>, E> {

}
