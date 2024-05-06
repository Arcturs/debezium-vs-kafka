package ru.vsu.csf.asashina.paymentprocessing.model.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import java.lang.IllegalArgumentException

data class UpdatePaymentStatusRequest(
    @field:Schema(
        description = "Статус выплаты",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "ACCEPTED",
        allowableValues = [
            "VERIFICATION",
            "ACCEPTED",
            "DECLINED"
        ]
    )
    @field:NotBlank(message = "Статус выплаты не может быть пустым")
    val status: String? = null,

    @field:Schema(
        description = "Комментарий к выплате",
        example = "Выплата была не обработана из-за нарушений правил системы"
    )
    val comment: String? = null
) {

    @delegate:Hidden
    @delegate:JsonIgnore
    val actualStatus: PaymentStatus? by lazy {
        kotlin.runCatching { PaymentStatus.valueOf(status!!) }
            .onFailure { throw IllegalArgumentException("Значение $status не является статусом выплаты") }
            .getOrThrow()
    }

}
