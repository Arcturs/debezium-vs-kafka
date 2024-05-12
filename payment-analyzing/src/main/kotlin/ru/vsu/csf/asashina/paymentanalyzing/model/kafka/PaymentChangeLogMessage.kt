package ru.vsu.csf.asashina.paymentanalyzing.model.kafka

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentanalyzing.validator.EnumValue
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentChangeLogMessage(
    @NotNull(message = "Поле ИД платежа не может быть пустым")
    var paymentId: Long? = null,

    @NotBlank(message = "Поле операция не может быть пустым")
    @EnumValue(
        acceptedValues = ["CREATE", "UPDATE", "DELETE"],
        message = "Поле операция должно соответствовать определенным значениям"
    )
    var operation: String? = null,

    var payer: String? = null,

    var recipient: String? = null,

    var amount: BigDecimal? = null,

    @NotBlank(message = "Поле статус платежа не может быть пустым")
    @EnumValue(
        acceptedValues = ["NEW", "VERIFICATION", "ACCEPTED", "DECLINED"],
        message = "Поле статус платежа должно соответствовать определенным значениям"
    )
    var paymentStatus: String? = null,

    @NotNull(message = "Поле время создания события не может быть пустым")
    var rowInsertTime: LocalDateTime? = null,

    @NotNull(message = "Поле время обновления события не может быть пустым")
    var rowUpdateTime: LocalDateTime? = null,

    var comment: String? = null
) {

    val actualOperation by lazy {
        runCatching { OperationType.valueOf(operation!!) }
            .onFailure { throw IllegalArgumentException("Значение $operation не является операцией") }
            .getOrThrow()
    }

    val actualPaymentStatus by lazy { PaymentStatus.fromValue(paymentStatus!!) }

}
