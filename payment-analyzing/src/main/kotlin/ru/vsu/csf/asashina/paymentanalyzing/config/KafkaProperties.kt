package ru.vsu.csf.asashina.paymentanalyzing.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.kafka.consumer")
data class KafkaProperties(
    val bootstrapServers: String,
    val groupId: String,
    val maxPollRecords: String
)
