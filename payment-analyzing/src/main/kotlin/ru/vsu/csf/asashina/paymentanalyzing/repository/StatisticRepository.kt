package ru.vsu.csf.asashina.paymentanalyzing.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.StatisticStatus
import ru.vsu.csf.asashina.paymentanalyzing.model.Statistic

@Repository
interface StatisticRepository : CrudRepository<Statistic, Long> {

    @Query("""
            select *
            from statistic s
            where s.status in (:statuses)
            for update
    """)
    fun findByStatusesAndLock(statuses: Collection<StatisticStatus>): List<Statistic>

}
