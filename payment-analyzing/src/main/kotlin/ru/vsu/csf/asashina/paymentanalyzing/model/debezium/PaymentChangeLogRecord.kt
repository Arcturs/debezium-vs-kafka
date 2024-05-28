package ru.vsu.csf.asashina.paymentanalyzing.model.debezium

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import java.math.BigDecimal
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentChangeLogRecord(
    var id: Long? = null,
    var payer: String? = null,
    var recipient: String? = null,
    var amount: BigDecimal? = null,
    var status: String? = null,

    @get:JsonAlias(value = [ "row_insert_time" ])
    var rowInsertTime: Instant? = null,

    @get:JsonAlias(value = [ "row_update_time" ])
    var rowUpdateTime: Instant? = null,
    var comment: String? = null
) {

    @delegate:JsonIgnore
    val actualStatus by lazy { PaymentStatus.fromValue(status!!) }

}
