package ru.vsu.csf.asashina.paymentprocessing.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentprocessing.exception.PaymentDoesNotExistException
import ru.vsu.csf.asashina.paymentprocessing.exception.PaymentInFinalStatusException
import ru.vsu.csf.asashina.paymentprocessing.model.dto.UpdatePaymentStatusRequest
import ru.vsu.csf.asashina.paymentprocessing.repository.PaymentRepository

@Service
class UpdatePaymentStatusService(
    private val repository: PaymentRepository
) {

    @Transactional
    fun updatePaymentStatus(id: Long, request: UpdatePaymentStatusRequest) {
        val payment = repository.findById(id)
            .orElseThrow { PaymentDoesNotExistException(message = "Платежа с ИД $id не существует") }
        if (FINAL_STATUSES.contains(payment.status)) {
            throw PaymentInFinalStatusException(message = "Платеж находится в конечном статусе, изменения не применены")
        }
        repository.save(
            payment.apply {
                status = request.actualStatus
                comment = request.comment
            }
        )
    }

    private companion object {
        val FINAL_STATUSES = setOf(PaymentStatus.ACCEPTED, PaymentStatus.DECLINED)
    }

}
