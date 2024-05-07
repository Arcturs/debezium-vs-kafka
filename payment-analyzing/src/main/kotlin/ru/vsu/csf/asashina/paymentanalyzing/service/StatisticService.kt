package ru.vsu.csf.asashina.paymentanalyzing.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.StatisticStatus
import ru.vsu.csf.asashina.paymentanalyzing.repository.StatisticRepository

@Service
class UpdateStatisticService(
    private val repository: StatisticRepository
) {

    @Transactional
    fun updateStatistics(statusesToCount: Map<StatisticStatus, Int>) {
        val statistics = repository.findByStatusesAndLock(statusesToCount.keys)
            .onEach { it.count = it.count!! + statusesToCount[it.status]!! }
        repository.saveAll(statistics)
    }

}

@Service
class GetStatisticsService(
    private val repository: StatisticRepository
) {

    fun getAllStatistics() = repository.findAll()

}
