package ru.vsu.csf.asashina.paymentprocessing.producer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import ru.vsu.csf.asashina.paymentprocessing.model.kafka.EventMessage

@Component
class EventKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, EventMessage>,
    @Value("\${spring.kafka.topic}")
    private val topic: String
) {

    fun sendMessage(message: EventMessage) {
        kafkaTemplate.send(topic, message)
            .whenComplete { _, exception ->
                if (exception == null) {
                    log.trace("--- Сообщение {} успешно отправлено в Кафку ---", message)
                } else {
                    log.warn("--- Сообщение {} не было отправлено в Кафку ---", message, exception)
                }
            }
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(EventKafkaProducer::class.java)
    }

}
