package ru.vsu.csf.asashina.paymentanalyzing.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DebeziumConfig(
    private val debeziumOffsetProperties: DebeziumOffsetProperties,
    private val debeziumTargetProperties: DebeziumTargetProperties
) {

    @Bean
    fun debeziumConfiguration(): io.debezium.config.Configuration =
        io.debezium.config.Configuration.from(
            mapOf(
                NAME_PROPERTY to CONNECTOR_NAME,
                TOPIC_PREFIX_PROPERTY to CONNECTOR_TOPIC,
                CONNECTOR_CLASS_PROPERTY to CONNECTOR_CLASS,
                OFFSET_STORAGE_PROPERTY to CONNECTOR_OFFSET_STORAGE,
                OFFSET_STORAGE_JDBC_URL_PROPERTY to debeziumOffsetProperties.url,
                OFFSET_STORAGE_JDBC_USER_PROPERTY to debeziumOffsetProperties.username,
                OFFSET_STORAGE_JDBC_PASSWORD_PROPERTY to debeziumOffsetProperties.password,
                DATABASE_HOSTNAME_PROPERTY to debeziumTargetProperties.hostname,
                DATABASE_PORT_PROPERTY to debeziumTargetProperties.port,
                DATABASE_USER_PROPERTY to debeziumTargetProperties.username,
                DATABASE_PASSWORD_PROPERTY to debeziumTargetProperties.password,
                DATABASE_DBNAME_PROPERTY to debeziumTargetProperties.dbname,
                TABLE_INCLUDE_LIST_PROPERTY to debeziumTargetProperties.table,
                PLUGIN_NAME_PROPERTY to CONNECTOR_PLUGIN_NAME
            )
        )

    private companion object {
        const val CONNECTOR_NAME = "payment-analyzing-connector"
        const val CONNECTOR_TOPIC = "payment-analyzing-topic"
        const val CONNECTOR_CLASS = "io.debezium.connector.postgresql.PostgresConnector"
        const val CONNECTOR_OFFSET_STORAGE = "io.debezium.storage.jdbc.offset.JdbcOffsetBackingStore"
        const val CONNECTOR_PLUGIN_NAME = "pgoutput"

        const val NAME_PROPERTY = "name"
        const val TOPIC_PREFIX_PROPERTY = "topic.prefix"
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
        const val PLUGIN_NAME_PROPERTY = "plugin.name"
    }

}
