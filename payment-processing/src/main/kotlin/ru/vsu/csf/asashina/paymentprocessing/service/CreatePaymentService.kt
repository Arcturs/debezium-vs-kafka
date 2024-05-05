package ru.vsu.csf.asashina.paymentprocessing.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentprocessing.model.dto.CreatePaymentRequest
import ru.vsu.csf.asashina.paymentprocessing.model.entity.Payment
import ru.vsu.csf.asashina.paymentprocessing.repository.PaymentRepository
import java.time.LocalDateTime

interface CreatePaymentService {

    fun createPayment(request: CreatePaymentRequest)

}

@Component
@Profile("kafka")
class CreatePaymentServiceWithKafka(
    private val repository: PaymentRepository
): CreatePaymentService {

    override fun createPayment(request: CreatePaymentRequest) {
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
        // отправка сообщения в кафку
    }

}

@Component
@Profile("!kafka")
class CreatePaymentServiceWithoutKafka(
    private val repository: PaymentRepository
): CreatePaymentService {

    override fun createPayment(request: CreatePaymentRequest) {
        repository.save(
            Payment().apply {
                payer = request.payer
                recipient = request.recipient
                amount = request.amount
                status = PaymentStatus.NEW
                rowInsertTime = LocalDateTime.now()
                rowUpdateTime = LocalDateTime.now()
            }
        )
    }

}
