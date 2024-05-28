package ru.vsu.csf.asashina.paymentprocessing

import jakarta.annotation.PostConstruct
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.randomizers.number.BigDecimalRandomizer
import org.jeasy.random.randomizers.range.LocalDateTimeRangeRandomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.vsu.csf.asashina.paymentprocessing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentprocessing.model.dto.CreatePaymentRequest
import ru.vsu.csf.asashina.paymentprocessing.model.dto.UpdatePaymentStatusRequest
import ru.vsu.csf.asashina.paymentprocessing.service.CreatePaymentService
import ru.vsu.csf.asashina.paymentprocessing.service.DeletePaymentService
import ru.vsu.csf.asashina.paymentprocessing.service.UpdatePaymentStatusService
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

@Component
class LoadTesting(
    private val createPaymentService: CreatePaymentService,
    private val updatePaymentStatusService: UpdatePaymentStatusService,
    private val deletePaymentService: DeletePaymentService,
    @Value("\${testing.entities-amount}")
    private val amount: Int
) {

    @PostConstruct
    fun loadTesting() {
        val createEntitiesAmount = amount * 0.5
        val verifyEntitiesAmount = amount * 0.25
        val acceptOrDeclineEntitiesAmount = amount * 0.2
        val deleteEntitiesAmount = amount * 0.05

        val testingTime = measureTimeMillis {
            log.trace("Отправка сущностей на создание")
            val entitiesId = (0 until createEntitiesAmount.toInt())
                .map {
                    createPaymentService.createPayment(EASY_RANDOM.nextObject(CreatePaymentRequest::class.java))
                }
                .map { it.id }
            log.trace("Сгенерированы сущности на создание")
            Thread.sleep(1000)

            for (i in 0 until verifyEntitiesAmount.toInt()) {
                updatePaymentStatusService.updatePaymentStatus(
                    entitiesId[i]!!,
                    UpdatePaymentStatusRequest(status = PaymentStatus.VERIFICATION.name)
                )
            }
            log.trace("Изменены сущности на создание")
            Thread.sleep(1000)

            for (i in 0 until acceptOrDeclineEntitiesAmount.toInt()) {
                updatePaymentStatusService.updatePaymentStatus(
                    entitiesId[i]!!,
                    UpdatePaymentStatusRequest(
                        status =
                            if (i % 1297 == 0) PaymentStatus.DECLINED.name
                            else PaymentStatus.ACCEPTED.name
                    )
                )
            }
            log.trace("Простановка конечного статуса выплат")
            Thread.sleep(1000)

            for (i in 0 until deleteEntitiesAmount.toInt()) {
                deletePaymentService.deletePayment(entitiesId[i]!!)
            }
            log.trace("Удаление случайных выплат")
        }
        log.trace("Генерация и отправка сущностей произошла за {} миллисекунд", testingTime)
    }

    private companion object {
        val log = LoggerFactory.getLogger(LoadTesting::class.java)
        val EASY_RANDOM = EasyRandom(
            EasyRandomParameters()
                .stringLengthRange(1, 250)
                .randomize(
                    LocalDateTime::class.java,
                    LocalDateTimeRangeRandomizer(
                        LocalDateTime.of(2020, 1, 1, 0, 0),
                        LocalDateTime.of(2024, 12, 31, 23, 59)
                    )
                )
                .randomize(BigDecimal::class.java, BigDecimalRandomizer(2 as Int))
        )
    }

}
