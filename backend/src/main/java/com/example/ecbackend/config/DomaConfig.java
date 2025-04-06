package com.example.ecbackend.config;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;

@Configuration
public class DomaConfig implements Config {

    private DataSource dataSource;

    @Autowired
    public DomaConfig(DataSource dataSource) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    @Bean
    public Dialect getDialect() {
        return new PostgresDialect();
    }
}