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
                classes = com.example.dsms.repository.VenteRepositoryThies.class
        ),
    entityManagerFactoryRef = "thiesEntityManagerFactory",
    transactionManagerRef = "thiesTransactionManager"
)
public class ThiesDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasources.thies")
    public DataSourceProperties thiesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource thiesDataSource() {
        return thiesDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "thiesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean thiesEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(thiesDataSource());
        emf.setPackagesToScan("com.example.dsms.model");
        emf.setPersistenceUnitName("thiesPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.hbm2ddl.auto", "update");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        emf.setJpaProperties(jpaProps);
        return emf;
    }

    @Bean(name = "thiesTransactionManager")
    public PlatformTransactionManager thiesTransactionManager(
            @Autowired @Qualifier("thiesEntityManagerFactory") LocalContainerEntityManagerFactoryBean thiesEntityManagerFactory) {
        return new JpaTransactionManager(thiesEntityManagerFactory.getObject());
    }
}