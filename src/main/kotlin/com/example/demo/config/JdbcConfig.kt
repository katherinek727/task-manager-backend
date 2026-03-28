package com.example.demo.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DataSourceProperties::class)
class JdbcConfig {

    @Bean
    fun dataSource(props: DataSourceProperties): DataSource =
        DriverManagerDataSource(props.url, props.username, props.password).apply {
            setDriverClassName(props.driverClassName)
        }

    @Bean
    fun jdbcClient(dataSource: DataSource): JdbcClient =
        JdbcClient.create(dataSource)
}
