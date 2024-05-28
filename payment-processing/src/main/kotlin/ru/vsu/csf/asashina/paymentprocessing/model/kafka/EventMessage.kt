package ru.vsu.csf.asashina.paymentprocessing.model.kafka

import ru.vsu.csf.asashina.paymentprocessing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class EventMessage(
    var paymentId: Long? = null,
    var operation: OperationType? = null,
    var payer: String? = null,
    var recipient: String? = null,
    var amount: BigDecimal? = null,
    var paymentStatus: PaymentStatus? = null,
    var rowInsertTime: LocalDateTime? = null,
    var rowUpdateTime: LocalDateTime? = null,
    var comment: String? = null
) {

    override fun toString() = "EventMessage(paymentId=$paymentId, " +
            "operation=${operation?.name}, " +
            "payer=$payer, " +
            "recipient=$recipient, " +
            "amount=$amount, " +
            "paymentStatus=${paymentStatus?.name}, " +
            "rowInsertTime=${rowInsertTime.toString()}, " +
            "rowUpdateTime=${rowUpdateTime.toString()}, " +
            "comment=$comment)"

}
