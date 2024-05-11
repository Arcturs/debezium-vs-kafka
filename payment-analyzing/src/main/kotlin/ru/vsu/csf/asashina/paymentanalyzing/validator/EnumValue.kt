package ru.vsu.csf.asashina.paymentanalyzing.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.collections.ArrayList
import kotlin.reflect.KClass


annotation class EnumValue(
    val acceptedValues: Array<String>,
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class Validator : ConstraintValidator<EnumValue, String> {

    private val values: ArrayList<String> = arrayListOf()

    override fun initialize(constraintAnnotation: EnumValue) {
        values.addAll(constraintAnnotation.acceptedValues)
    }

    override fun isValid(value: String, context: ConstraintValidatorContext) = values.contains(value)

}
