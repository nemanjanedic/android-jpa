/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.MapAttribute;

import java.util.Map;

import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type;

public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, Map<K, V>, V> implements
        MapAttribute<X, K, V> {

    private final Class<K> keyJavaType;

    private final Type<K> keyType;

    public MapAttributeImpl(final String name,
            final PersistentAttributeType persistentAttributeType,
            final ManagedTypeImpl<X> declaringType, final Class<Map<K, V>> javaType,
            final boolean association,
            final boolean collection, final FetchType fetchType, final Class<V> elementJavaType,
            final Type<V> elementType, final SingularAttributeImpl<V, X> mappedByAttribute,
            final Class<K> keyJavaType,
            final Type<K> keyType) {
        super(name, persistentAttributeType, declaringType, javaType, association, collection,
                fetchType,
                elementJavaType, elementType, mappedByAttribute);
        this.keyJavaType = keyJavaType;
        this.keyType = keyType;
    }

    @Override
    public CollectionType getCollectionType() {
        return CollectionType.MAP;
    }

    @Override
    public Class<K> getKeyJavaType() {
        return keyJavaType;
    }

    @Override
    public Type<K> getKeyType() {
        return keyType;
    }

}
