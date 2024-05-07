package ru.vsu.csf.asashina.paymentanalyzing.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.vsu.csf.asashina.paymentanalyzing.service.GetStatisticsService

@RestController
@RequestMapping("/statistic")
@Tag(name = "Statistic Controller")
class StatisticController(
    private val getStatisticsService: GetStatisticsService
) {

    @GetMapping
    @Operation(description = "Получить всю статистику", operationId = "getStatistics")
    fun getStatistics() = ResponseEntity.ok(getStatisticsService.getAllStatistics())

}
