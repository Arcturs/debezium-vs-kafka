package ru.vsu.csf.asashina.paymentanalyzing.service

import org.springframework.stereotype.Service
import ru.vsu.csf.asashina.paymentanalyzing.model.entity.Statistic
import ru.vsu.csf.asashina.paymentanalyzing.repository.StatisticRepository

@Service
class GetStatisticsService(
    private val repository: StatisticRepository
) {

    fun getAllStatistics(): List<Statistic> = repository.findAll().toList()

}
