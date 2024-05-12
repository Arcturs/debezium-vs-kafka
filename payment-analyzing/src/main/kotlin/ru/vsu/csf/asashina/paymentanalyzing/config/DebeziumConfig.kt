package ru.vsu.csf.asashina.paymentanalyzing.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "debezium")
class DebeziumConfig(
    private val offsetUrl: String,
    private val offsetUsername: String,
    private val offsetPassword: String,
    private val targetHostname: String,
    private val targetPort: String,
    private val targetDbname: String,
    private val targetTable: String,
    private val targetUsername: String,
    private val targetPassword: String
) {

    @Bean
    fun debeziumConfiguration(): io.debezium.config.Configuration =
        io.debezium.config.Configuration.from(
            mapOf(
                NAME_PROPERTY to CONNECTOR_NAME,
                CONNECTOR_CLASS_PROPERTY to CONNECTOR_CLASS,
                OFFSET_STORAGE_PROPERTY to CONNECTOR_OFFSET_STORAGE,
                OFFSET_STORAGE_JDBC_URL_PROPERTY to offsetUrl,
                OFFSET_STORAGE_JDBC_USER_PROPERTY to offsetUsername,
                OFFSET_STORAGE_JDBC_PASSWORD_PROPERTY to offsetPassword,
                DATABASE_HOSTNAME_PROPERTY to targetHostname,
                DATABASE_PORT_PROPERTY to targetPort,
                DATABASE_USER_PROPERTY to targetUsername,
                DATABASE_PASSWORD_PROPERTY to targetPassword,
                DATABASE_DBNAME_PROPERTY to targetDbname,
                TABLE_INCLUDE_LIST_PROPERTY to targetTable
            )
        )

    private companion object {
        const val CONNECTOR_NAME = "payment-analyzing-connector"
        const val CONNECTOR_CLASS = "io.debezium.connector.postgresql.PostgresConnector"
        const val CONNECTOR_OFFSET_STORAGE = "io.debezium.storage.jdbc.offset.JdbcOffsetBackingStore"

        const val NAME_PROPERTY = "name"
        const val CONNECTOR_CLASS_PROPERTY = "connector.class"
        const val OFFSET_STORAGE_PROPERTY = "offset.storage"
        const val OFFSET_STORAGE_JDBC_URL_PROPERTY = "offset.storage.jdbc.url"
        const val OFFSET_STORAGE_JDBC_USER_PROPERTY = "offset.storage.jdbc.user"
        const val OFFSET_STORAGE_JDBC_PASSWORD_PROPERTY = "offset.storage.jdbc.password"
        const val DATABASE_HOSTNAME_PROPERTY = "database.hostname"
        const val DATABASE_PORT_PROPERTY = "database.port"
        const val DATABASE_USER_PROPERTY = "database.user"
        const val DATABASE_PASSWORD_PROPERTY = "database.password"
        const val DATABASE_DBNAME_PROPERTY = "database.dbname"
        const val TABLE_INCLUDE_LIST_PROPERTY = "table.include.list"
    }

}
