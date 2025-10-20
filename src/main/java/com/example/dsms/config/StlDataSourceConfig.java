package com.example.dsms.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.dsms.stl.repository",
    entityManagerFactoryRef = "stlEntityManagerFactory",
    transactionManagerRef = "stlTransactionManager"
)
public class StlDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasources.stl")
    public DataSourceProperties stlDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource stlDataSource() {
        return stlDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean stlEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(stlDataSource());
        emf.setPackagesToScan("com.example.dsms.model");
        emf.setPersistenceUnitName("stlPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.hbm2ddl.auto", "update");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        emf.setJpaProperties(jpaProps);
        return emf;
    }

    @Bean
    public PlatformTransactionManager sltTransactionManager(
            @Autowired @Qualifier("stlEntityManagerFactory") LocalContainerEntityManagerFactoryBean stlEntityManagerFactory) {
        return new JpaTransactionManager(stlEntityManagerFactory.getObject());
    }
}
