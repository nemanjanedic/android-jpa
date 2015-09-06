/**
 *
 */
package com.nemanjanedic.androidjpa.persistence.metamodel.impl;

import com.nemanjanedic.androidjpa.persistence.metamodel.IdentifiableType;
import com.nemanjanedic.androidjpa.persistence.metamodel.SingularAttribute;

import java.util.Set;

import javax.persistence.metamodel.Type;

public class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

    protected SingularAttributeImpl<X, Long> idAttribute = null;

    protected SingularAttributeImpl<X, Long> versionAttribute = null;

    @SuppressWarnings("unchecked")
    public IdentifiableTypeImpl(final PersistenceType persistenceType, final Class<X> javaType,
            final Set<AttributeImpl<X, ?>> declaredAttributes) {
        super(persistenceType, javaType, declaredAttributes);
        if (declaredAttributes != null) {
            for (AttributeImpl<X, ?> attribute : declaredAttributes) {
                if (attribute instanceof SingularAttributeImpl && attribute.getJavaType()
                        .equals(Long.class)) {
                    SingularAttributeImpl<X, Long> singularAttribute
                            = (SingularAttributeImpl<X, Long>) attribute;
                    if (singularAttribute.isId()) {
                        idAttribute = singularAttribute;
                    }
                    if (singularAttribute.isVersion()) {
                        versionAttribute = singularAttribute;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    void addDeclaredAttribute(final AttributeImpl<X, ?> attribute) {
        super.addDeclaredAttribute(attribute);
        if (attribute != null && attribute instanceof SingularAttributeImpl
                && attribute.getJavaType().equals(Long.class)) {
            SingularAttributeImpl<X, Long> singularAttribute
                    = (SingularAttributeImpl<X, Long>) attribute;
            if (singularAttribute.isId()) {
                idAttribute = singularAttribute;
            }
            if (singularAttribute.isVersion()) {
                versionAttribute = singularAttribute;
            }
        }
    }

    @Override
    public SingularAttribute<? super X, Long> getId() {
        return getDeclaredId();
    }

    @Override
    public SingularAttribute<X, Long> getDeclaredId() {
        return idAttribute;
    }

    @Override
    public SingularAttribute<? super X, Long> getVersion() {
        return getDeclaredVersion();
    }

    @Override
    public SingularAttribute<X, Long> getDeclaredVersion() {
        return versionAttribute;
    }

    @Override
    public IdentifiableType<? super X> getSupertype() {
        return null;
    }

    @Override
    public boolean hasSingleIdAttribute() {
        return idAttribute != null;
    }

    @Override
    public boolean hasVersionAttribute() {
        return versionAttribute != null;
    }

    @Override
    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
        throw new IllegalArgumentException("No id class declared.");
    }

    @Override
    public Type<?> getIdType() {
        return new TypeImpl<Long>(PersistenceType.BASIC, Long.class);
    }

}
