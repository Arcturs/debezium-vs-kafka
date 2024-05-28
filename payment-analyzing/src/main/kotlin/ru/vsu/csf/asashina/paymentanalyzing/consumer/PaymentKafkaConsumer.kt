package ru.vsu.csf.asashina.paymentanalyzing.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Validator
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentanalyzing.model.kafka.PaymentChangeLogMessage
import ru.vsu.csf.asashina.paymentanalyzing.service.ProcessPaymentChangeLogService
import kotlin.system.measureTimeMillis

@Component
@Profile("kafka")
class PaymentKafkaConsumer(
    private val validator: Validator,
    private val objectMapper: ObjectMapper,
    private val service: ProcessPaymentChangeLogService,
) {

    @KafkaListener(
        topics = ["\${spring.kafka.consumer.topic}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        concurrency = "\${spring.kafka.consumer.partitions}"
    )
    fun receiveAndProcessMessages(
        messages: List<ConsumerRecord<String, ByteArray>>,
        acknowledgment: Acknowledgment
    ) {
        val processTime = measureTimeMillis {
            log.trace("[KAFKA] Получены сообщения из Кафки [KAFKA]")
            val filteredMessages = messages
                .map { objectMapper.readValue(it.value(), PaymentChangeLogMessage::class.java) }
                .filter {
                    val errors = validator.validate(it)
                    if (errors.isNotEmpty()) {
                        log.warn(
                            "[KAFKA] Сообщение {} не прошло валидацию по следующим причинам: {} [KAFKA]",
                            it,
                            errors
                        )
                    }
                    errors.isEmpty()
                }
            log.trace("[KAFKA] Валидацю прошли следующие сообщения: {} [KAFKA]", filteredMessages)
            val paymentStatusToCount = PaymentStatus.values()
                .associateWith { 0 }
                .toMutableMap()
            filteredMessages.forEach {
                val coeff =
                    if (it.actualOperation == OperationType.DELETE) -1
                    else 1
                paymentStatusToCount[it.actualPaymentStatus] = paymentStatusToCount[it.actualPaymentStatus]!! + coeff
            }
            service.process(paymentStatusToCount)
        }
        log.trace("[KAFKA] Успешно обработаны сообщения за {} миллисекунд [KAFKA]", processTime)
        acknowledgment.acknowledge()
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(PaymentKafkaConsumer::class.java)
    }

}
