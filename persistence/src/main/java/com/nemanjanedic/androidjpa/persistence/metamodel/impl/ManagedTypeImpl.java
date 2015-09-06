/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.Attribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.CollectionAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.ListAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.ManagedType;
import com.nemanjanedic.androidjpa.persistence.metamodel.MapAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.PluralAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.SetAttribute;
import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ManagedTypeImpl<X> extends TypeImpl<X> implements ManagedType<X> {

    protected final Map<String, AttributeImpl<X, ?>> declaredAttributeMap
            = new HashMap<String, AttributeImpl<X, ?>>();

    public ManagedTypeImpl(final PersistenceType persistenceType, final Class<X> javaType,
            final Set<AttributeImpl<X, ?>> declaredAttributes) {
        super(persistenceType, javaType);
        if (declaredAttributes != null) {
            for (AttributeImpl<X, ?> attribute : declaredAttributes) {
                declaredAttributeMap.put(attribute.getName(), attribute);
            }
        }
    }

    void addDeclaredAttribute(final AttributeImpl<X, ?> attribute) {
        if (attribute != null) {
            declaredAttributeMap.put(attribute.getName(), attribute);
        }
    }

    @Override
    public Set<Attribute<? super X, ?>> getAttributes() {
        return new HashSet<Attribute<? super X, ?>>(declaredAttributeMap.values());
    }

    @Override
    public Set<Attribute<X, ?>> getDeclaredAttributes() {
        return new HashSet<Attribute<X, ?>>(declaredAttributeMap.values());
    }

    @Override
    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(final String name,
            final Class<Y> type) {
        return getDeclaredSingularAttribute(name, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(final String name,
            final Class<Y> type) {
        SingularAttribute<X, ?> attribute = getDeclaredSingularAttribute(name);
        if (attribute != null && attribute.getJavaType().equals(type)) {
            return (SingularAttribute<X, Y>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
        HashSet<SingularAttribute<? super X, ?>> res
                = new HashSet<SingularAttribute<? super X, ?>>();
        Set<SingularAttribute<X, ?>> declaredSingularAttributes = getDeclaredSingularAttributes();
        for (SingularAttribute<X, ?> attribute : declaredSingularAttributes) {
            res.add(attribute);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
        HashSet<SingularAttribute<X, ?>> res = new HashSet<SingularAttribute<X, ?>>();
        for (AttributeImpl<X, ?> attribute : declaredAttributeMap.values()) {
            if (attribute instanceof SingularAttributeImpl) {
                res.add((SingularAttribute<X, ?>) attribute);
            }
        }
        return res;
    }

    @Override
    public <E> CollectionAttribute<? super X, E> getCollection(final String name,
            final Class<E> elementType) {
        return getDeclaredCollection(name, elementType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> CollectionAttribute<X, E> getDeclaredCollection(final String name,
            final Class<E> elementType) {
        CollectionAttribute<X, ?> attribute = getDeclaredCollection(name);
        if (attribute != null && attribute.getJavaType().equals(elementType)) {
            return (CollectionAttribute<X, E>) attribute;
        }
        return null;
    }

    @Override
    public <E> SetAttribute<? super X, E> getSet(final String name, final Class<E> elementType) {
        return getDeclaredSet(name, elementType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> SetAttribute<X, E> getDeclaredSet(final String name, final Class<E> elementType) {
        SetAttribute<X, ?> attribute = getDeclaredSet(name);
        if (attribute != null && attribute.getJavaType().equals(elementType)) {
            return (SetAttribute<X, E>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public <E> ListAttribute<? super X, E> getList(final String name, final Class<E> elementType) {
        return getDeclaredList(name, elementType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> ListAttribute<X, E> getDeclaredList(final String name, final Class<E> elementType) {
        ListAttribute<X, ?> attribute = getDeclaredList(name);
        if (attribute != null && attribute.getJavaType().equals(elementType)) {
            return (ListAttribute<X, E>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public <K, V> MapAttribute<? super X, K, V> getMap(final String name, final Class<K> keyType,
            final Class<V> valueType) {
        return getDeclaredMap(name, keyType, valueType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> MapAttribute<X, K, V> getDeclaredMap(final String name, final Class<K> keyType,
            final Class<V> valueType) {
        MapAttribute<X, ?, ?> attribute = getDeclaredMap(name);
        if (attribute != null && attribute.getKeyJavaType().equals(keyType)
                && attribute.getElementType().getJavaType().equals(valueType)) {
            return (MapAttribute<X, K, V>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
        HashSet<PluralAttribute<? super X, ?, ?>> res
                = new HashSet<PluralAttribute<? super X, ?, ?>>();
        Set<PluralAttribute<X, ?, ?>> declaredAttributes = getDeclaredPluralAttributes();
        for (PluralAttribute<X, ?, ?> attribute : declaredAttributes) {
            res.add(attribute);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
        HashSet<PluralAttribute<X, ?, ?>> res = new HashSet<PluralAttribute<X, ?, ?>>();
        for (AttributeImpl<X, ?> attribute : declaredAttributeMap.values()) {
            if (attribute instanceof PluralAttributeImpl) {
                res.add((PluralAttribute<X, ?, ?>) attribute);
            }
        }
        return res;
    }

    @Override
    public Attribute<? super X, ?> getAttribute(final String name) {
        return getDeclaredAttribute(name);
    }

    @Override
    public Attribute<X, ?> getDeclaredAttribute(final String name) {
        AttributeImpl<X, ?> attribute = declaredAttributeMap.get(name);
        if (attribute != null) {
            return attribute;
        } else {
            throw new IllegalArgumentException(
                    "There is no attribute with the name [" + name + "] declared.");
        }
    }

    @Override
    public SingularAttribute<? super X, ?> getSingularAttribute(final String name) {
        return getDeclaredSingularAttribute(name);
    }

    @Override
    public SingularAttribute<X, ?> getDeclaredSingularAttribute(final String name) {
        AttributeImpl<X, ?> attribute = declaredAttributeMap.get(name);
        if (attribute != null && attribute instanceof SingularAttributeImpl) {
            return (SingularAttributeImpl<X, ?>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public CollectionAttribute<? super X, ?> getCollection(final String name) {
        return getDeclaredCollection(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CollectionAttribute<X, ?> getDeclaredCollection(final String name) {
        AttributeImpl<X, ?> attribute = declaredAttributeMap.get(name);
        if (attribute != null && attribute instanceof CollectionAttributeImpl) {
            return (CollectionAttributeImpl<X, ?>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public SetAttribute<? super X, ?> getSet(final String name) {
        return getDeclaredSet(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetAttribute<X, ?> getDeclaredSet(final String name) {
        AttributeImpl<X, ?> attribute = declaredAttributeMap.get(name);
        if (attribute != null && attribute instanceof SetAttributeImpl) {
            return (SetAttributeImpl<X, ?>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public ListAttribute<? super X, ?> getList(final String name) {
        return getDeclaredList(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListAttribute<X, ?> getDeclaredList(final String name) {
        AttributeImpl<X, ?> attribute = declaredAttributeMap.get(name);
        if (attribute != null && attribute instanceof ListAttributeImpl) {
            return (ListAttributeImpl<X, ?>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

    @Override
    public MapAttribute<? super X, ?, ?> getMap(final String name) {
        return getDeclaredMap(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapAttribute<X, ?, ?> getDeclaredMap(final String name) {
        AttributeImpl<X, ?> attribute = declaredAttributeMap.get(name);
        if (attribute != null && attribute instanceof MapAttributeImpl) {
            return (MapAttributeImpl<X, ?, ?>) attribute;
        }
        throw new IllegalArgumentException(
                "There is no attribute with the name [" + name + "] declared.");
    }

}
