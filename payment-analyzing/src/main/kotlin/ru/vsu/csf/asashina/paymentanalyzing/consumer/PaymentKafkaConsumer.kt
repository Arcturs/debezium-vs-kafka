package ru.vsu.csf.asashina.paymentanalyzing.consumer

import jakarta.validation.Validator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.ACKNOWLEDGMENT
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentanalyzing.model.kafka.PaymentChangeLogMessage
import ru.vsu.csf.asashina.paymentanalyzing.service.ProcessPaymentChangeLogService

@Component
class PaymentKafkaConsumer(
    private val validator: Validator,
    private val service: ProcessPaymentChangeLogService
) {

    @KafkaListener(
        topics = ["\${spring.kafka.consumer.topic}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        concurrency = "\${spring.kafka.consumer.partitions}"
    )
    fun receiveAndProcessMessages(
        messages: List<PaymentChangeLogMessage>,
        @Header(ACKNOWLEDGMENT) acknowledgment: Acknowledgment
    ) {
        log.trace("--- Получены следующие сообщения из Кафки: {} ---", messages)
        val filteredMessages = messages.filter {
            val errors = validator.validate(it)
            if (errors.isNotEmpty()) {
                log.warn("--- Сообщение {} не прошло валидацию по следующим причинам: {} ---", it, errors)
            }
            errors.isEmpty()
        }
        log.trace("--- Валидацю прошли следующие сообщения: {} ---", filteredMessages)
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
        log.trace("--- Успешно обработаны сообщения {} ---", filteredMessages)
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(PaymentKafkaConsumer::class.java)
    }

}
