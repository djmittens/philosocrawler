package me.ngrid.philosocrawler.config

import javax.sql.DataSource

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration, Primary}

/**
  *
  */
@Configuration
class DatabaseConfig {
  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  def dataSource: DataSource = DataSourceBuilder.create().build()
}
