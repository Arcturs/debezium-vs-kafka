package ru.vsu.csf.asashina.paymentprocessing.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import java.math.BigDecimal

data class GetPaymentResponse(
    @field:Schema(description = "ИД выплаты", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    val id: Long? = null,

    @field:Schema(description = "Имя плательщика", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    val payer: String? = null,

    @field:Schema(description = "Имя получателя платежа", requiredMode = Schema.RequiredMode.REQUIRED, example = "Bob")
    val recipient: String? = null,

    @field:Schema(
        description = "Сумма выплаты",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "200.00",
        format = "double"
    )
    val amount: BigDecimal? = null,

    @field:Schema(description = "Статус выплаты", requiredMode = Schema.RequiredMode.REQUIRED, example = "VERIFICATION")
    val status: PaymentStatus? = null,

    @field:Schema(
        description = "Комментарий к выплате",
        example = "Выплата была не обработана из-за нарушений правил системы"
    )
    val comment: String? = null,
)
