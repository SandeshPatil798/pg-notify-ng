package com.example.pg_notify_ng.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

  /**
   * Defines the primary DataSourceProperties bean, which reads properties
   * prefixed with "spring.datasource".
   */
  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  /**
   * Creates the primary DataSource bean for the application, used by Liquibase,
   * Spring Data, etc. It uses a connection pool (HikariCP by default in Spring Boot 3).
   *
   * @param properties The configured DataSourceProperties.
   * @return A configured DataSource instance.
   */
  @Bean
  @Primary
  public DataSource dataSource(DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().build();
  }
}