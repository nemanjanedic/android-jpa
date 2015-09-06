package com.nemanjanedic.androidjpa.persistence.dao;

import java.util.HashMap;

public class DaoManager {

    private static DaoManager instance;

    private final HashMap<Class<?>, AbstractDao<?>> daoInstances;

    private DaoManager() {
        daoInstances = new HashMap<Class<?>, AbstractDao<?>>();
    }

    public static DaoManager getInstance() {
        if (instance == null) {
            instance = new DaoManager();
        }
        return instance;
    }

    <X> void registerDao(final Class<X> entitiyJavaType, final AbstractDao<X> daoInstance) {
        daoInstances.put(entitiyJavaType, daoInstance);
    }

    @SuppressWarnings("unchecked")
    public <X> AbstractDao<X> getDao(final Class<X> entitiyJavaType) {
        return (AbstractDao<X>) daoInstances.get(entitiyJavaType);
    }

}