package ru.vsu.csf.asashina.paymentprocessing.config

import com.zaxxer.hikari.HikariDataSource
import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatasourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun dataSourceProperties() = DataSourceProperties()

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun dataSource(properties: DataSourceProperties): HikariDataSource =
        properties.initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()

    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase")
    fun liquibaseProperties() = LiquibaseProperties()

    @Bean
    fun liquibase(): SpringLiquibase {
        val liquibase = SpringLiquibase().apply {
            dataSource = dataSource(dataSourceProperties())
            changeLog = liquibaseProperties().changeLog
            contexts = liquibaseProperties().contexts
            defaultSchema = liquibaseProperties().defaultSchema
            isDropFirst = liquibaseProperties().isDropFirst
        }
        liquibase.setShouldRun(liquibaseProperties().isEnabled)
        liquibase.setChangeLogParameters(liquibaseProperties().parameters)
        liquibase.setRollbackFile(liquibaseProperties().rollbackFile)
        return liquibase
    }

}