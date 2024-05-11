package ru.vsu.csf.asashina.paymentanalyzing.config

import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.RoundRobinAssignor
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.kafka.support.serializer.JsonDeserializer
import ru.vsu.csf.asashina.paymentanalyzing.model.kafka.PaymentChangeLogMessage

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.consumer")
class KafkaConsumerConfig(
    private val bootstrapServers: String,
    private val consumerGroupId: String,
    private val maxPollRecords: String
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
                BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                GROUP_ID_CONFIG to consumerGroupId,
                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::javaClass,
                VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer<PaymentChangeLogMessage>::javaClass,
                MAX_POLL_RECORDS_CONFIG to maxPollRecords,
                ENABLE_AUTO_COMMIT_CONFIG to false,
                PARTITION_ASSIGNMENT_STRATEGY_CONFIG to RoundRobinAssignor()
            )
        )

}
