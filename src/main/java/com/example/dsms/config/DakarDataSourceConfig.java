// java
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
    basePackages = "com.example.dsms.repository",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.example.dsms.repository.VenteRepositoryDakar.class
        ),
    entityManagerFactoryRef = "dakarEntityManagerFactory",
    transactionManagerRef = "dakarTransactionManager"
)
public class DakarDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasources.dakar")
    public DataSourceProperties dakarDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dakarDataSource() {
        return dakarDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "dakarEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dakarEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dakarDataSource());
        emf.setPackagesToScan("com.example.dsms.model");
        emf.setPersistenceUnitName("dakarPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.hbm2ddl.auto", "update");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        emf.setJpaProperties(jpaProps);
        return emf;
    }

    @Bean(name = "dakarTransactionManager")
    public PlatformTransactionManager dakarTransactionManager(
            @Autowired @Qualifier("dakarEntityManagerFactory") LocalContainerEntityManagerFactoryBean dakarEntityManagerFactory) {
        return new JpaTransactionManager(dakarEntityManagerFactory.getObject());
    }
}