package ru.vsu.csf.asashina.paymentprocessing.service

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.vsu.csf.asashina.paymentprocessing.constant.CommonConstants
import ru.vsu.csf.asashina.paymentprocessing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentprocessing.exception.PaymentDoesNotExistException
import ru.vsu.csf.asashina.paymentprocessing.model.kafka.EventMessage
import ru.vsu.csf.asashina.paymentprocessing.producer.EventKafkaProducer
import ru.vsu.csf.asashina.paymentprocessing.repository.PaymentRepository

@Service
class DeletePaymentService(
    private val repository: PaymentRepository,
    private val producer: EventKafkaProducer,
    @Value("\${spring.profiles.active}")
    private val profile: String
) {

    @Transactional
    fun deletePayment(id: Long) {
        val payment = repository.findById(id)
            .orElseThrow { PaymentDoesNotExistException(message = "Платежа с ИД $id не существует") }
        repository.delete(payment)
        if (profile == CommonConstants.KAFKA_PROFILE) {
            runBlocking {
                producer.sendMessage(
                    EventMessage().apply {
                        paymentId = payment.id
                        operation = OperationType.DELETE
                        paymentStatus = payment.status
                        rowInsertTime = payment.rowInsertTime
                        rowUpdateTime = payment.rowUpdateTime
                    }
                )
            }
        }
    }

}
