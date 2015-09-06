package com.nemanjanedic.androidjpa.persistence.metamodel;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

/**
 * Represents an attribute of a Java type.
 *
 * @param <X> The represented type that contains the attribute
 * @param <Y> The type of the represented attribute
 */
public interface Attribute<X, Y> {

    /**
     * Return the name of the attribute.
     *
     * @return name
     */
    String getName();

    /**
     * Return the persistent attribute type for the attribute.
     *
     * @return persistent attribute type
     */
    PersistentAttributeType getPersistentAttributeType();

    /**
     * Return the managed type representing the type in which the attribute was declared.
     *
     * @return declaring type
     */
    ManagedType<X> getDeclaringType();

    /**
     * Return the Java type of the represented attribute.
     *
     * @return Java type
     */
    Class<Y> getJavaType();

    /**
     * Is the attribute an association.
     *
     * @return boolean indicating whether the attribute corresponds to an association
     */
    boolean isAssociation();

    /**
     * Is the attribute collection-valued (represents a Collection, Set, List, or Map).
     *
     * @return boolean indicating whether the attribute is collection-valued
     */
    boolean isCollection();

    /**
     * Returns the fetch type of an association.
     *
     * @return fetch type
     */
    FetchType getFetchType();

}