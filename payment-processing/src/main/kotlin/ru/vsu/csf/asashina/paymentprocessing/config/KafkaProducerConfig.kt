package ru.vsu.csf.asashina.paymentprocessing.config

import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.vsu.csf.asashina.paymentprocessing.model.kafka.EventMessage

@Configuration
class KafkaProducerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String
) {

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, EventMessage?> = KafkaTemplate(producerFactory())

    @Bean
    fun producerFactory(): ProducerFactory<String, EventMessage?> =
        DefaultKafkaProducerFactory(
            mapOf<String, Any>(
                BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                BATCH_SIZE_CONFIG to BATCH_SIZE.toString()
            ),
            StringSerializer(),
            JsonSerializer<EventMessage?>().apply {
                isAddTypeInfo = false
            }
        )

    private companion object {
        const val BATCH_SIZE = 64 * 1024
    }

}
