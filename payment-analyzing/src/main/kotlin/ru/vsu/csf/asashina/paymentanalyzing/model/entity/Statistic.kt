package ru.vsu.csf.asashina.paymentanalyzing.model.entity

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.StatisticStatus
import ru.vsu.csf.asashina.paymentanalyzing.model.entity.Statistic.Companion.TABLE_NAME

@Table(value = TABLE_NAME)
class Statistic {

    @get:Column
    @field:Schema(description = "ИД статистики", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    var id: Long? = null

    @get:Column
    @field:Schema(description = "Статус статистики", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUCCESS")
    var status: StatisticStatus? = null

    @get:Column
    @field:Schema(description = "Количество выплат", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    var count: Int? = null

    override fun equals(other: Any?) = id == (other as? Statistic)?.id

    override fun hashCode() = id.hashCode()

    override fun toString() = "Statistic(id=$id, " +
            "status=${status!!.name}, " +
            "count=$count)"

    companion object {
        const val TABLE_NAME = "statistic"
    }

}
