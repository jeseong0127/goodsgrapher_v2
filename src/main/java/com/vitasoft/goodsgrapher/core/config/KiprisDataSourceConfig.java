package com.vitasoft.goodsgrapher.core.config;

import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "kiprisEntityManagerFactory",
        transactionManagerRef = "kiprisTransactionManager",
        basePackages = {"com.vitasoft.goodsgrapher.domain.model.kipris.repository"}
)
public class KiprisDataSourceConfig {
    @Primary
    @Bean
    @ConfigurationProperties("kipris.datasource")
    public DataSourceProperties kiprisDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("kipris.datasource.configuration")
    public DataSource kiprisDataSource(@Qualifier("kiprisDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean kiprisEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                              @Qualifier("kiprisDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.vitasoft.goodsgrapher.domain.model.kipris.entity")
                .persistenceUnit("kiprisEntityManager")
                .properties(jpaProperties())
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager kiprisTransactionManager(@Qualifier("kiprisEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return props;
    }

}
