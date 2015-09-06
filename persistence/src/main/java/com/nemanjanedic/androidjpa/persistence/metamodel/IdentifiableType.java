package com.nemanjanedic.androidjpa.persistence.metamodel;


import java.util.Set;

import javax.persistence.metamodel.Type;

/**
 * Instances of the type <code>IdentifiableType</code> represent entity or mapped superclass types.
 *
 * @param <X> The represented entity or mapped superclass type.
 * @since Java Persistence 2.0
 */
public interface IdentifiableType<X> extends ManagedType<X> {

    /**
     * Return the attribute that corresponds to the id attribute of the entity or mapped
     * superclass.
     *
     * @return id attribute
     * @throws IllegalArgumentException if id attribute of the given type is not present in the
     *                                  identifiable type or if the identifiable type
     *                                  has an id class
     */
    SingularAttribute<? super X, Long> getId();

    /**
     * Return the attribute that corresponds to the id attribute declared by the entity or mapped
     * superclass.
     *
     * @return declared id attribute
     * @throws IllegalArgumentException if id attribute of the given type is not declared in the
     *                                  identifiable type or if the identifiable
     *                                  type has an id class
     */
    SingularAttribute<X, Long> getDeclaredId();

    /**
     * Return the attribute that corresponds to the version attribute of the entity or mapped
     * superclass.
     *
     * @return version attribute
     * @throws IllegalArgumentException if version attribute of the given type is not present in
     *                                  the
     *                                  identifiable type
     */
    SingularAttribute<? super X, Long> getVersion();

    /**
     * Return the attribute that corresponds to the version attribute declared by the entity or
     * mapped superclass.
     *
     * @return declared version attribute
     * @throws IllegalArgumentException if version attribute of the type is not declared in the
     *                                  identifiable type
     */
    SingularAttribute<X, Long> getDeclaredVersion();

    /**
     * Return the identifiable type that corresponds to the most specific mapped superclass or
     * entity extended by the
     * entity or mapped superclass.
     *
     * @return supertype of identifiable type or null if no such supertype
     */
    IdentifiableType<? super X> getSupertype();

    /**
     * Whether the identifiable type has a single id attribute. Returns true for a simple id or
     * embedded id; returns
     * false for an idclass.
     *
     * @return boolean indicating whether the identifiable type has a single id attribute
     */
    boolean hasSingleIdAttribute();

    /**
     * Whether the identifiable type has a version attribute.
     *
     * @return boolean indicating whether the identifiable type has a version attribute
     */
    boolean hasVersionAttribute();

    /**
     * Return the attributes corresponding to the id class of the identifiable type.
     *
     * @return id attributes
     * @throws IllegalArgumentException if the identifiable type does not have an id class
     */
    Set<SingularAttribute<? super X, ?>> getIdClassAttributes();

    /**
     * Return the type that represents the type of the id.
     *
     * @return type of id
     */
    Type<?> getIdType();
}
