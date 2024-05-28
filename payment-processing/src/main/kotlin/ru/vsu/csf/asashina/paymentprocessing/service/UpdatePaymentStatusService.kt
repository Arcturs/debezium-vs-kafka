package ru.vsu.csf.asashina.paymentprocessing.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.vsu.csf.asashina.paymentprocessing.constant.CommonConstants
import ru.vsu.csf.asashina.paymentprocessing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentprocessing.exception.PaymentDoesNotExistException
import ru.vsu.csf.asashina.paymentprocessing.exception.PaymentInFinalStatusException
import ru.vsu.csf.asashina.paymentprocessing.model.dto.UpdatePaymentStatusRequest
import ru.vsu.csf.asashina.paymentprocessing.model.kafka.EventMessage
import ru.vsu.csf.asashina.paymentprocessing.producer.EventKafkaProducer
import ru.vsu.csf.asashina.paymentprocessing.repository.PaymentRepository
import java.time.LocalDateTime

@Service
class UpdatePaymentStatusService(
    private val repository: PaymentRepository,
    private val producer: EventKafkaProducer,
    @Value("\${spring.profiles.active}")
    private val profile: String
) {

    @Transactional
    fun updatePaymentStatus(id: Long, request: UpdatePaymentStatusRequest) {
        val payment = repository.findById(id)
            .orElseThrow { PaymentDoesNotExistException(message = "Платежа с ИД $id не существует") }
        if (FINAL_STATUSES.contains(payment.status)) {
            throw PaymentInFinalStatusException(message = "Платеж находится в конечном статусе, изменения не применены")
        }
        val updateTime = LocalDateTime.now()
        repository.save(
            payment.apply {
                status = request.actualStatus
                comment = request.comment
                rowUpdateTime = updateTime
            }
        )
        if (profile == CommonConstants.KAFKA_PROFILE) {
            producer.sendMessage(
                EventMessage().apply {
                    paymentId = payment.id
                    operation = OperationType.UPDATE
                    paymentStatus = payment.status
                    rowInsertTime = payment.rowInsertTime
                    rowUpdateTime = updateTime
                }
            )
        }
    }

    private companion object {
        val FINAL_STATUSES = setOf(PaymentStatus.ACCEPTED, PaymentStatus.DECLINED)
    }

}
