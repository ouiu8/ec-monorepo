package com.example.ecbackend.dao;

/** */
@org.springframework.stereotype.Repository()
@javax.annotation.processing.Generated(value = { "Doma", "2.54.0" }, date = "2025-04-06T21:48:15.061+0900")
@org.seasar.doma.DaoImplementation
public class ProductDaoImpl implements com.example.ecbackend.dao.ProductDao, org.seasar.doma.jdbc.ConfigProvider {

    static {
        org.seasar.doma.internal.Artifact.validateVersion("2.54.0");
    }

    private static final java.lang.reflect.Method __method0 = org.seasar.doma.internal.jdbc.dao.DaoImplSupport.getDeclaredMethod(com.example.ecbackend.dao.ProductDao.class, "selectAll");

    private static final java.lang.reflect.Method __method1 = org.seasar.doma.internal.jdbc.dao.DaoImplSupport.getDeclaredMethod(com.example.ecbackend.dao.ProductDao.class, "selectById", java.lang.Long.class);

    private final org.seasar.doma.internal.jdbc.dao.DaoImplSupport __support;

    /**
     * @param config the config
     */
    @org.springframework.beans.factory.annotation.Autowired()
    public ProductDaoImpl(org.seasar.doma.jdbc.Config config) {
        __support = new org.seasar.doma.internal.jdbc.dao.DaoImplSupport(config);
    }

    @Override
    public org.seasar.doma.jdbc.Config getConfig() {
        return __support.getConfig();
    }

    @Override
    public java.util.List<com.example.ecbackend.entity.Product> selectAll() {
        __support.entering("com.example.ecbackend.dao.ProductDaoImpl", "selectAll");
        try {
            org.seasar.doma.jdbc.query.SqlFileSelectQuery __query = __support.getQueryImplementors().createSqlFileSelectQuery(__method0);
            __query.setMethod(__method0);
            __query.setConfig(__support.getConfig());
            __query.setSqlFilePath("META-INF/com/example/ecbackend/dao/ProductDao/selectAll.sql");
            __query.setEntityType(com.example.ecbackend.entity._Product.getSingletonInternal());
            __query.setCallerClassName("com.example.ecbackend.dao.ProductDaoImpl");
            __query.setCallerMethodName("selectAll");
            __query.setResultEnsured(false);
            __query.setResultMappingEnsured(false);
            __query.setFetchType(org.seasar.doma.FetchType.LAZY);
            __query.setQueryTimeout(-1);
            __query.setMaxRows(-1);
            __query.setFetchSize(-1);
            __query.setSqlLogType(org.seasar.doma.jdbc.SqlLogType.FORMATTED);
            __query.prepare();
            org.seasar.doma.jdbc.command.SelectCommand<java.util.List<com.example.ecbackend.entity.Product>> __command = __support.getCommandImplementors().createSelectCommand(__method0, __query, new org.seasar.doma.internal.jdbc.command.EntityResultListHandler<com.example.ecbackend.entity.Product>(com.example.ecbackend.entity._Product.getSingletonInternal()));
            java.util.List<com.example.ecbackend.entity.Product> __result = __command.execute();
            __query.complete();
            __support.exiting("com.example.ecbackend.dao.ProductDaoImpl", "selectAll", __result);
            return __result;
        } catch (java.lang.RuntimeException __e) {
            __support.throwing("com.example.ecbackend.dao.ProductDaoImpl", "selectAll", __e);
            throw __e;
        }
    }

    @Override
    public com.example.ecbackend.entity.Product selectById(java.lang.Long id) {
        __support.entering("com.example.ecbackend.dao.ProductDaoImpl", "selectById", id);
        try {
            org.seasar.doma.jdbc.query.SqlFileSelectQuery __query = __support.getQueryImplementors().createSqlFileSelectQuery(__method1);
            __query.setMethod(__method1);
            __query.setConfig(__support.getConfig());
            __query.setSqlFilePath("META-INF/com/example/ecbackend/dao/ProductDao/selectById.sql");
            __query.setEntityType(com.example.ecbackend.entity._Product.getSingletonInternal());
            __query.addParameter("id", java.lang.Long.class, id);
            __query.setCallerClassName("com.example.ecbackend.dao.ProductDaoImpl");
            __query.setCallerMethodName("selectById");
            __query.setResultEnsured(false);
            __query.setResultMappingEnsured(false);
            __query.setFetchType(org.seasar.doma.FetchType.LAZY);
            __query.setQueryTimeout(-1);
            __query.setMaxRows(-1);
            __query.setFetchSize(-1);
            __query.setSqlLogType(org.seasar.doma.jdbc.SqlLogType.FORMATTED);
            __query.prepare();
            org.seasar.doma.jdbc.command.SelectCommand<com.example.ecbackend.entity.Product> __command = __support.getCommandImplementors().createSelectCommand(__method1, __query, new org.seasar.doma.internal.jdbc.command.EntitySingleResultHandler<com.example.ecbackend.entity.Product>(com.example.ecbackend.entity._Product.getSingletonInternal()));
            com.example.ecbackend.entity.Product __result = __command.execute();
            __query.complete();
            __support.exiting("com.example.ecbackend.dao.ProductDaoImpl", "selectById", __result);
            return __result;
        } catch (java.lang.RuntimeException __e) {
            __support.throwing("com.example.ecbackend.dao.ProductDaoImpl", "selectById", __e);
            throw __e;
        }
    }

}
