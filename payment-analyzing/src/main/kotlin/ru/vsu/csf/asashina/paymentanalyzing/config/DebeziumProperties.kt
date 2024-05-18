package ru.vsu.csf.asashina.paymentanalyzing.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "debezium.offset")
data class DebeziumOffsetProperties(
    val url: String,
    val username: String,
    val password: String
)

@ConfigurationProperties(prefix = "debezium.target")
data class DebeziumTargetProperties(
    val hostname: String,
    val port: String,
    val dbname: String,
    val table: String,
    val username: String,
    val password: String
)
