package com.example.ecbackend.config;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;
import org.seasar.doma.jdbc.id.BuiltinIdentityIdGenerator;
import org.seasar.doma.jdbc.id.IdentityIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

/**
 * テスト用のDomaの設定クラス
 * H2データベース環境でPostgreSQLの機能をエミュレート
 */
@Configuration
@Primary
@Profile("test")
public class TestDomaConfig implements Config {

    private final DataSource dataSource;
    private final H2PostgreSQLDialect dialect;

    @Autowired
    public TestDomaConfig(DataSource dataSource) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
        this.dialect = new H2PostgreSQLDialect();
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    @Bean
    public Dialect getDialect() {
        return this.dialect;
    }
    
    @Bean
    public IdentityIdGenerator identityIdGenerator() {
        return new H2IdentityIdGenerator();
    }
    
    /**
     * H2データベース用カスタムDialect
     * PostgreSQL固有の機能をH2でも使えるようにするための拡張
     */
    public static class H2PostgreSQLDialect extends H2Dialect {
        public H2PostgreSQLDialect() {
            super();
        }
    }
    
    /**
     * H2データベース用カスタムIdentityIdGenerator
     * PostgreSQL固有のID生成処理をH2データベース用に修正
     */
    public static class H2IdentityIdGenerator extends BuiltinIdentityIdGenerator {
        public String getIdSql() {
            // H2の構文
            return "CALL IDENTITY()";
        }
    }
} 