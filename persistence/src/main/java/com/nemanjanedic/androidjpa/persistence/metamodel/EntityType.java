package com.nemanjanedic.androidjpa.persistence.metamodel;

import javax.persistence.metamodel.Bindable;

/**
 * Instances of the type <code>EntityType</code> represent entity types.
 *
 * @param <X> The represented entity type.
 */
public interface EntityType<X> extends IdentifiableType<X>, Bindable<X> {

    /**
     * Return the entity name.
     *
     * @return entity name
     */
    String getName();

    /**
     * Return the relational table name.
     *
     * @return table name
     */
    String getTableName();

}
