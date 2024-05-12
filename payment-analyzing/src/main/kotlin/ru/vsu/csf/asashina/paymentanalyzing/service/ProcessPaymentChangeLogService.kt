package ru.vsu.csf.asashina.paymentanalyzing.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.StatisticStatus
import ru.vsu.csf.asashina.paymentanalyzing.repository.StatisticRepository

@Service
class ProcessPaymentChangeLogService(
    private val repository: StatisticRepository
) {

    @Transactional
    fun process(paymentStatusToCount: Map<PaymentStatus, Int>) {
        val filteredStatuses = paymentStatusToCount.filter { FINAL_PAYMENT_STATUSES.contains(it.key) }
        val statistics = repository.findByStatusesAndLock(StatisticStatus.values().asList())
            .associateBy { it.status }
            .toMutableMap()
        filteredStatuses.forEach {
            val status =
                if (it.key == PaymentStatus.ACCEPTED) StatisticStatus.SUCCESS
                else StatisticStatus.FAILURE
            statistics[status]!!.count = statistics[status]!!.count!! + it.value
        }
        repository.saveAll(statistics.values)
    }

    private companion object {
        val FINAL_PAYMENT_STATUSES = listOf(PaymentStatus.ACCEPTED, PaymentStatus.DECLINED)
    }

}
