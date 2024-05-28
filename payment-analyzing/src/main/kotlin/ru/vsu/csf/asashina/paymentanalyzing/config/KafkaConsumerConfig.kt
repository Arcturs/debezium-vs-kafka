package ru.vsu.csf.asashina.paymentanalyzing.config

import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter

@Configuration
class KafkaConsumerConfig(
    private val properties: KafkaProperties
) {

    @Bean
    fun kafkaListenerContainerFactory() =
        ConcurrentKafkaListenerContainerFactory<String, ByteArray>()
            .apply {
                consumerFactory = consumerFactory()
                isBatchListener = true
                setBatchMessageConverter(BatchMessagingMessageConverter(StringJsonMessageConverter()))
                containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
            }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, ByteArray> =
        DefaultKafkaConsumerFactory(
            mapOf<String, Any>(
                BOOTSTRAP_SERVERS_CONFIG to properties.bootstrapServers,
                GROUP_ID_CONFIG to properties.groupId,
                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
                MAX_POLL_RECORDS_CONFIG to properties.maxPollRecords,
                ENABLE_AUTO_COMMIT_CONFIG to false,
                AUTO_OFFSET_RESET_CONFIG to "earliest",
                PARTITION_ASSIGNMENT_STRATEGY_CONFIG to listOf(PARTITION_ASSIGNMENT_STRATEGY_CLASS_NAME)
            )
        )

    private companion object {
        const val PARTITION_ASSIGNMENT_STRATEGY_CLASS_NAME = "org.apache.kafka.clients.consumer.RoundRobinAssignor"
    }

}
