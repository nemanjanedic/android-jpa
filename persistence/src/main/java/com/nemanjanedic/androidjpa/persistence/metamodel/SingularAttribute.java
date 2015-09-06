package com.nemanjanedic.androidjpa.persistence.metamodel;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Type;

/**
 * Instances of the type <code>SingularAttribute</code> represents persistent single-valued
 * properties or fields.
 *
 * @param <X> The type containing the represented attribute
 * @param <T> The type of the represented attribute
 */
public interface SingularAttribute<X, T> extends Attribute<X, T>, Bindable<T> {

    /**
     * Is the attribute an id attribute. This method will return true if the attribute is an
     * attribute that corresponds
     * to a simple id, an embedded id, or an attribute of an id class.
     *
     * @return boolean indicating whether the attribute is an id
     */
    boolean isId();

    /**
     * Is the attribute a version attribute.
     *
     * @return boolean indicating whether the attribute is a version attribute
     */
    boolean isVersion();

    /**
     * Can the attribute be null.
     *
     * @return boolean indicating whether the attribute can be null
     */
    boolean isOptional();

    /**
     * Return the type that represents the type of the attribute.
     *
     * @return type of attribute
     */
    Type<T> getType();

    /**
     * Returns the relational table column name.
     *
     * @return column name
     */
    String getColumnName();
}
