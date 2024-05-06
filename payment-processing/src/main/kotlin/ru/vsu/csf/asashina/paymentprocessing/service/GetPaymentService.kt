package ru.vsu.csf.asashina.paymentprocessing.service

import org.springframework.stereotype.Service
import ru.vsu.csf.asashina.paymentprocessing.exception.PaymentDoesNotExistException
import ru.vsu.csf.asashina.paymentprocessing.model.dto.GetPaymentResponse
import ru.vsu.csf.asashina.paymentprocessing.repository.PaymentRepository

@Service
class GetPaymentService(
    private val repository: PaymentRepository
) {

    fun getPaymentById(id: Long): GetPaymentResponse {
        val payment = repository.findById(id)
            .orElseThrow { PaymentDoesNotExistException(message = "Платежа с ИД $id не существует") }
        return GetPaymentResponse(
            id = payment.id,
            payer = payment.payer,
            recipient = payment.recipient,
            amount = payment.amount,
            status = payment.status,
            comment = payment.comment
        )
    }

}
