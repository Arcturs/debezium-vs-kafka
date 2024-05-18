package ru.vsu.csf.asashina.paymentprocessing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class PaymentProcessingApplication

fun main(args: Array<String>) {
	runApplication<PaymentProcessingApplication>(*args)
}
