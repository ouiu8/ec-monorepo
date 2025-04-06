package com.example.ecbackend.entity;

/** */
@javax.annotation.processing.Generated(value = { "Doma", "2.54.0" }, date = "2025-04-06T21:48:14.943+0900")
@org.seasar.doma.EntityTypeImplementation
public final class _Product extends org.seasar.doma.jdbc.entity.AbstractEntityType<com.example.ecbackend.entity.Product> {

    static {
        org.seasar.doma.internal.Artifact.validateVersion("2.54.0");
    }

    private static final _Product __singleton = new _Product();

    private final org.seasar.doma.jdbc.entity.NamingType __namingType = null;

    private final java.util.function.Supplier<org.seasar.doma.jdbc.entity.NullEntityListener<com.example.ecbackend.entity.Product>> __listenerSupplier;

    private final boolean __immutable;

    private final String __catalogName;

    private final String __schemaName;

    private final String __tableName;

    private final boolean __isQuoteRequired;

    private final String __name;

    private final java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __idPropertyTypes;

    private final java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __entityPropertyTypes;

    private final java.util.Map<String, org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __entityPropertyTypeMap;

    @SuppressWarnings("unused")
    private final java.util.Map<String, org.seasar.doma.jdbc.entity.EmbeddedPropertyType<com.example.ecbackend.entity.Product, ?>> __embeddedPropertyTypeMap;

    private _Product() {
        __listenerSupplier = org.seasar.doma.internal.jdbc.entity.NullEntityListenerSuppliers.of();
        __immutable = false;
        __name = "Product";
        __catalogName = "";
        __schemaName = "";
        __tableName = "products";
        __isQuoteRequired = false;
        java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __idList = new java.util.ArrayList<>();
        java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __list = new java.util.ArrayList<>(4);
        java.util.Map<String, org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __map = new java.util.LinkedHashMap<>(4);
        java.util.Map<String, org.seasar.doma.jdbc.entity.EmbeddedPropertyType<com.example.ecbackend.entity.Product, ?>> __embeddedMap = new java.util.LinkedHashMap<>(4);
        initializeMaps(__map, __embeddedMap);
        initializeIdList(__map, __idList);
        initializeList(__map, __list);
        __idPropertyTypes = java.util.Collections.unmodifiableList(__idList);
        __entityPropertyTypes = java.util.Collections.unmodifiableList(__list);
        __entityPropertyTypeMap = java.util.Collections.unmodifiableMap(__map);
        __embeddedPropertyTypeMap = java.util.Collections.unmodifiableMap(__embeddedMap);
    }

    private void initializeMaps(java.util.Map<String, org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __map, java.util.Map<String, org.seasar.doma.jdbc.entity.EmbeddedPropertyType<com.example.ecbackend.entity.Product, ?>> __embeddedMap) {
        __map.put("id", new org.seasar.doma.jdbc.entity.AssignedIdPropertyType<com.example.ecbackend.entity.Product, java.lang.Long, java.lang.Long>(com.example.ecbackend.entity.Product.class, org.seasar.doma.internal.jdbc.scalar.BasicScalarSuppliers.ofLong(), "id", "", __namingType, false));
        __map.put("name", new org.seasar.doma.jdbc.entity.DefaultPropertyType<com.example.ecbackend.entity.Product, java.lang.String, java.lang.String>(com.example.ecbackend.entity.Product.class, org.seasar.doma.internal.jdbc.scalar.BasicScalarSuppliers.ofString(), "name", "", __namingType, true, true, false));
        __map.put("description", new org.seasar.doma.jdbc.entity.DefaultPropertyType<com.example.ecbackend.entity.Product, java.lang.String, java.lang.String>(com.example.ecbackend.entity.Product.class, org.seasar.doma.internal.jdbc.scalar.BasicScalarSuppliers.ofString(), "description", "", __namingType, true, true, false));
        __map.put("price", new org.seasar.doma.jdbc.entity.DefaultPropertyType<com.example.ecbackend.entity.Product, java.lang.Integer, java.lang.Integer>(com.example.ecbackend.entity.Product.class, org.seasar.doma.internal.jdbc.scalar.BasicScalarSuppliers.ofInteger(), "price", "", __namingType, true, true, false));
    }

    private void initializeIdList(java.util.Map<String, org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __map, java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __idList) {
        __idList.add(__map.get("id"));
    }

    private void initializeList(java.util.Map<String, org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __map, java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> __list) {
        __list.addAll(__map.values());
    }

    @Override
    public org.seasar.doma.jdbc.entity.NamingType getNamingType() {
        return __namingType;
    }

    @Override
    public boolean isImmutable() {
        return __immutable;
    }

    @Override
    public String getName() {
        return __name;
    }

    @Override
    public String getCatalogName() {
        return __catalogName;
    }

    @Override
    public String getSchemaName() {
        return __schemaName;
    }

    @Override
    @Deprecated
    public String getTableName() {
        return getTableName(org.seasar.doma.internal.jdbc.entity.TableNames.namingFunction);
    }

    @Override
    public String getTableName(java.util.function.BiFunction<org.seasar.doma.jdbc.entity.NamingType, String, String> namingFunction) {
        if (__tableName.isEmpty()) {
            return namingFunction.apply(__namingType, __name);
        }
        return __tableName;
    }

    @Override
    public boolean isQuoteRequired() {
        return __isQuoteRequired;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void preInsert(com.example.ecbackend.entity.Product entity, org.seasar.doma.jdbc.entity.PreInsertContext<com.example.ecbackend.entity.Product> context) {
        Class __listenerClass = org.seasar.doma.jdbc.entity.NullEntityListener.class;
        org.seasar.doma.jdbc.entity.NullEntityListener __listener = context.getConfig().getEntityListenerProvider().get(__listenerClass, __listenerSupplier);
        __listener.preInsert(entity, context);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void preUpdate(com.example.ecbackend.entity.Product entity, org.seasar.doma.jdbc.entity.PreUpdateContext<com.example.ecbackend.entity.Product> context) {
        Class __listenerClass = org.seasar.doma.jdbc.entity.NullEntityListener.class;
        org.seasar.doma.jdbc.entity.NullEntityListener __listener = context.getConfig().getEntityListenerProvider().get(__listenerClass, __listenerSupplier);
        __listener.preUpdate(entity, context);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void preDelete(com.example.ecbackend.entity.Product entity, org.seasar.doma.jdbc.entity.PreDeleteContext<com.example.ecbackend.entity.Product> context) {
        Class __listenerClass = org.seasar.doma.jdbc.entity.NullEntityListener.class;
        org.seasar.doma.jdbc.entity.NullEntityListener __listener = context.getConfig().getEntityListenerProvider().get(__listenerClass, __listenerSupplier);
        __listener.preDelete(entity, context);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void postInsert(com.example.ecbackend.entity.Product entity, org.seasar.doma.jdbc.entity.PostInsertContext<com.example.ecbackend.entity.Product> context) {
        Class __listenerClass = org.seasar.doma.jdbc.entity.NullEntityListener.class;
        org.seasar.doma.jdbc.entity.NullEntityListener __listener = context.getConfig().getEntityListenerProvider().get(__listenerClass, __listenerSupplier);
        __listener.postInsert(entity, context);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void postUpdate(com.example.ecbackend.entity.Product entity, org.seasar.doma.jdbc.entity.PostUpdateContext<com.example.ecbackend.entity.Product> context) {
        Class __listenerClass = org.seasar.doma.jdbc.entity.NullEntityListener.class;
        org.seasar.doma.jdbc.entity.NullEntityListener __listener = context.getConfig().getEntityListenerProvider().get(__listenerClass, __listenerSupplier);
        __listener.postUpdate(entity, context);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void postDelete(com.example.ecbackend.entity.Product entity, org.seasar.doma.jdbc.entity.PostDeleteContext<com.example.ecbackend.entity.Product> context) {
        Class __listenerClass = org.seasar.doma.jdbc.entity.NullEntityListener.class;
        org.seasar.doma.jdbc.entity.NullEntityListener __listener = context.getConfig().getEntityListenerProvider().get(__listenerClass, __listenerSupplier);
        __listener.postDelete(entity, context);
    }

    @Override
    public java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> getEntityPropertyTypes() {
        return __entityPropertyTypes;
    }

    @Override
    public org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?> getEntityPropertyType(String __name) {
        return __entityPropertyTypeMap.get(__name);
    }

    @Override
    public java.util.List<org.seasar.doma.jdbc.entity.EntityPropertyType<com.example.ecbackend.entity.Product, ?>> getIdPropertyTypes() {
        return __idPropertyTypes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public org.seasar.doma.jdbc.entity.GeneratedIdPropertyType<com.example.ecbackend.entity.Product, ?, ?> getGeneratedIdPropertyType() {
        return (org.seasar.doma.jdbc.entity.GeneratedIdPropertyType<com.example.ecbackend.entity.Product, ?, ?>)__entityPropertyTypeMap.get("null");
    }

    @SuppressWarnings("unchecked")
    @Override
    public org.seasar.doma.jdbc.entity.VersionPropertyType<com.example.ecbackend.entity.Product, ?, ?> getVersionPropertyType() {
        return (org.seasar.doma.jdbc.entity.VersionPropertyType<com.example.ecbackend.entity.Product, ?, ?>)__entityPropertyTypeMap.get("null");
    }

    @SuppressWarnings("unchecked")
    @Override
    public org.seasar.doma.jdbc.entity.TenantIdPropertyType<com.example.ecbackend.entity.Product, ?, ?> getTenantIdPropertyType() {
        return (org.seasar.doma.jdbc.entity.TenantIdPropertyType<com.example.ecbackend.entity.Product, ?, ?>)__entityPropertyTypeMap.get("null");
    }

    @Override
    public com.example.ecbackend.entity.Product newEntity(java.util.Map<String, org.seasar.doma.jdbc.entity.Property<com.example.ecbackend.entity.Product, ?>> __args) {
        com.example.ecbackend.entity.Product entity = new com.example.ecbackend.entity.Product();
        if (__args.get("id") != null) __args.get("id").save(entity);
        if (__args.get("name") != null) __args.get("name").save(entity);
        if (__args.get("description") != null) __args.get("description").save(entity);
        if (__args.get("price") != null) __args.get("price").save(entity);
        return entity;
    }

    @Override
    public Class<com.example.ecbackend.entity.Product> getEntityClass() {
        return com.example.ecbackend.entity.Product.class;
    }

    @Override
    public com.example.ecbackend.entity.Product getOriginalStates(com.example.ecbackend.entity.Product __entity) {
        return null;
    }

    @Override
    public void saveCurrentStates(com.example.ecbackend.entity.Product __entity) {
    }

    /**
     * @return the singleton
     */
    public static _Product getSingletonInternal() {
        return __singleton;
    }

    /**
     * @return the new instance
     */
    public static _Product newInstance() {
        return new _Product();
    }

}
