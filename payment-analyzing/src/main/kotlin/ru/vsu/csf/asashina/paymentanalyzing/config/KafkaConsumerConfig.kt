package ru.vsu.csf.asashina.paymentanalyzing.config

import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import ru.vsu.csf.asashina.paymentanalyzing.model.kafka.PaymentChangeLogMessage

@Configuration
class KafkaConsumerConfig(
    private val properties: KafkaProperties
) {

    @Bean
    fun kafkaListenerContainerFactory() =
        ConcurrentKafkaListenerContainerFactory<String, PaymentChangeLogMessage>()
            .apply {
                consumerFactory = consumerFactory()
                isBatchListener = true
                setBatchMessageConverter(BatchMessagingMessageConverter(StringJsonMessageConverter()))
            }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, PaymentChangeLogMessage> =
        DefaultKafkaConsumerFactory(
            mapOf<String, Any>(
                BOOTSTRAP_SERVERS_CONFIG to properties.bootstrapServers,
                GROUP_ID_CONFIG to properties.groupId,
                KEY_DESERIALIZER_CLASS_CONFIG to KEY_DESERIALIZER_CLASS_NAME,
                VALUE_DESERIALIZER_CLASS_CONFIG to VALUE_DESERIALIZER_CLASS_NAME,
                MAX_POLL_RECORDS_CONFIG to properties.maxPollRecords,
                ENABLE_AUTO_COMMIT_CONFIG to false,
                PARTITION_ASSIGNMENT_STRATEGY_CONFIG to listOf(PARTITION_ASSIGNMENT_STRATEGY_CLASS_NAME)
            )
        )

    private companion object {
        const val KEY_DESERIALIZER_CLASS_NAME = "org.apache.kafka.common.serialization.StringDeserializer"
        const val VALUE_DESERIALIZER_CLASS_NAME = "org.springframework.kafka.support.serializer.JsonDeserializer"
        const val PARTITION_ASSIGNMENT_STRATEGY_CLASS_NAME = "org.apache.kafka.clients.consumer.RoundRobinAssignor"
    }

}
