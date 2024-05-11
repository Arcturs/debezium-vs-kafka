package ru.vsu.csf.asashina.paymentanalyzing.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.OperationType
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.StatisticStatus
import ru.vsu.csf.asashina.paymentanalyzing.model.kafka.PaymentChangeLogMessage
import ru.vsu.csf.asashina.paymentanalyzing.repository.StatisticRepository

@Service
class ProcessPaymentMessageService(
    private val repository: StatisticRepository
) {

    @Transactional
    fun processPaymentMessages(messages: List<PaymentChangeLogMessage>) {
        val filteredMessages = messages.filter { FINAL_PAYMENT_STATUSES.contains(it.actualPaymentStatus) }
        val statistics = repository.findByStatusesAndLock(StatisticStatus.values().asList())
            .associateBy { it.status }
            .toMutableMap()
        filteredMessages.forEach {
            var coeff = 1
            if (it.actualOperation == OperationType.DELETE) coeff = -1
            if (it.actualPaymentStatus == PaymentStatus.ACCEPTED) {
                statistics[StatisticStatus.SUCCESS]!!.count = statistics[StatisticStatus.SUCCESS]!!.count!! + coeff * 1
            } else {
                statistics[StatisticStatus.FAILURE]!!.count = statistics[StatisticStatus.FAILURE]!!.count!! + coeff * 1
            }
        }
        repository.saveAll(statistics.values)
    }

    private companion object {
        val FINAL_PAYMENT_STATUSES = listOf(PaymentStatus.ACCEPTED, PaymentStatus.DECLINED)
    }

}
