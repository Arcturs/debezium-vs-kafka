package ru.vsu.csf.asashina.paymentprocessing.model.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CreatePaymentResponse(
    @field:Schema(description = "ИД выплаты", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    val id: Long? = null
)
