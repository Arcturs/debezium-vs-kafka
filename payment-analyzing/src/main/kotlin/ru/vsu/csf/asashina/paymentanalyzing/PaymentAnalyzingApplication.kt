package ru.vsu.csf.asashina.paymentanalyzing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentAnalyzingApplication

fun main(args: Array<String>) {
	runApplication<PaymentAnalyzingApplication>(*args)
}
