package ru.vsu.csf.asashina.paymentprocessing.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreatePaymentRequest(
    @field:NotBlank(message = "Имя плательщика не может быть пустым")
    @field:Schema(description = "Имя плательщика", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    val payer: String? = null,

    @field:NotBlank(message = "Имя получателя платежа не может быть пустым")
    @field:Schema(description = "Имя получателя платежа", requiredMode = Schema.RequiredMode.REQUIRED, example = "Bob")
    val recipient: String? = null,

    @field:NotNull(message = "Сумма выплаты должна присутствовать")
    @field:Schema(description = "Сумма выплаты", requiredMode = Schema.RequiredMode.REQUIRED, example = "200.00")
    val amount: BigDecimal? = null
)
