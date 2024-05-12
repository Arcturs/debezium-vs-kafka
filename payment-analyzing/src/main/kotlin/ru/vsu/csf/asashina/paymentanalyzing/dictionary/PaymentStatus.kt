package ru.vsu.csf.asashina.paymentanalyzing.dictionary

import java.lang.IllegalArgumentException

enum class PaymentStatus {

    NEW, VERIFICATION, ACCEPTED, DECLINED;

    companion object {

        @JvmStatic
        fun fromValue(value: String) =
            runCatching { PaymentStatus.valueOf(value) }
                .onFailure { throw IllegalArgumentException("Значение $value не является статусом платежа") }
                .getOrThrow()

    }

}
