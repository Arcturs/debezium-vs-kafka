package ru.vsu.csf.asashina.paymentprocessing.service

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.vsu.csf.asashina.paymentprocessing.constant.CommonConstants.KAFKA_PROFILE
import ru.vsu.csf.asashina.paymentprocessing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentprocessing.model.dto.CreatePaymentRequest
import ru.vsu.csf.asashina.paymentprocessing.model.dto.CreatePaymentResponse
import ru.vsu.csf.asashina.paymentprocessing.model.entity.Payment
import ru.vsu.csf.asashina.paymentprocessing.model.kafka.EventMessage
import ru.vsu.csf.asashina.paymentprocessing.producer.EventKafkaProducer
import ru.vsu.csf.asashina.paymentprocessing.repository.PaymentRepository
import java.time.LocalDateTime

@Service
class CreatePaymentService(
    private val repository: PaymentRepository,
    private val producer: EventKafkaProducer,
    @Value("\${spring.profiles.active}")
    private val profile: String
) {

    @Transactional
    fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse {
        val payment = repository.save(
            Payment().apply {
                payer = request.payer
                recipient = request.recipient
                amount = request.amount
                status = PaymentStatus.NEW
                rowInsertTime = LocalDateTime.now()
                rowUpdateTime = LocalDateTime.now()
            }
        )
        if (profile == KAFKA_PROFILE) {
             runBlocking {
                producer.sendMessage(
                    EventMessage().apply {
                        paymentId = payment.id
                        operation = OperationType.CREATE
                        payer = payment.payer
                        recipient = payment.recipient
                        amount = payment.amount
                        paymentStatus = payment.status
                        rowInsertTime = payment.rowInsertTime
                        rowUpdateTime = payment.rowUpdateTime
                        comment = payment.comment
                    }
                )
            }
        }
        return CreatePaymentResponse(id = payment.id)
    }

}
