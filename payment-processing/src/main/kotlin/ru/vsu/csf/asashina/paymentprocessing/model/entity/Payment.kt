package ru.vsu.csf.asashina.paymentprocessing.model.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentprocessing.model.entity.Payment.Companion.TABLE_NAME
import java.math.BigDecimal
import java.time.LocalDateTime

@Table(value = TABLE_NAME)
class Payment {

    @get:Column
    var id: Long? = null

    @get:Column
    var payer: String? = null

    @get:Column
    var recipient: String? = null

    @get:Column
    var amount: BigDecimal? = null

    @get:Column
    var status: PaymentStatus? = null

    @get:Column
    var rowInsertTime: LocalDateTime? = null

    @get:Column
    var rowUpdateTime: LocalDateTime? = null

    @get:Column
    var comment: String? = null

    override fun equals(other: Any?) = id == (other as? Payment)?.id

    override fun hashCode() = id.hashCode()

    override fun toString() = "Payment(id=$id, " +
            "payer=$payer, " +
            "recipient=$recipient, " +
            "amount=$amount, " +
            "paymentStatus=${status!!.name}, " +
            "rowInsertTime=${rowInsertTime.toString()}, " +
            "rowUpdateTime=${rowUpdateTime.toString()}, " +
            "comment=$comment)"

    companion object {
        const val TABLE_NAME = "payment"
    }

}
