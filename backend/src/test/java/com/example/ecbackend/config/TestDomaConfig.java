package com.example.ecbackend.config;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.H2Dialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * テスト用のDomaの設定クラス
 * H2データベース環境でPostgreSQLの機能をエミュレート
 */
@Configuration
@Primary
@Profile("test")
public class TestDomaConfig implements Config {

    private final DataSource dataSource;
    private final Dialect dialect;

    @Autowired
    public TestDomaConfig(DataSource dataSource) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
        this.dialect = new H2SequenceDialect();
        setupH2Functions();
    }
    
    /**
     * H2データベースにPostgreSQL互換の関数を追加
     */
    private void setupH2Functions() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // PostgreSQLのスキーマを作成
            stmt.execute("CREATE SCHEMA IF NOT EXISTS pg_catalog");
            
            // PostgreSQLのpg_get_serial_sequenceをエミュレート
            stmt.execute("CREATE ALIAS IF NOT EXISTS pg_catalog.pg_get_serial_sequence AS " +
                         "'String pgGetSerialSequence(String tableName, String columnName) { " +
                         "return tableName + \"_\" + columnName + \"_seq\"; }'");
            
            // H2には標準でSELECT NEXTVAL関数が存在するため、カスタム関数は削除
            // H2にはSELECT CURRVAL関数が存在するため、カスタム関数は削除
            
        } catch (SQLException e) {
            throw new RuntimeException("H2関数のセットアップに失敗しました: " + e.getMessage(), e);
        }
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
    @Primary
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(this.dataSource);
    }
    
    /**
     * H2データベース用カスタムDialect
     * PostgreSQL固有の機能をH2でも使えるようにするための拡張
     */
    public static class H2SequenceDialect extends H2Dialect {
        public H2SequenceDialect() {
            super();
        }
    }
} 