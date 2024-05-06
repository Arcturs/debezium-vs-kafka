package ru.vsu.csf.asashina.paymentprocessing.exception

class PaymentDoesNotExistException(message: String?) : RuntimeException(message)

class PaymentInFinalStatusException(message: String?) : RuntimeException(message)
