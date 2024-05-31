package ru.vsu.csf.asashina.paymentanalyzing.metric

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.TimeGauge
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RegisterMetricService(
    @Value("\${spring.profiles.active}")
    private val profile: String,
    private val meterRegistry: MeterRegistry
) {

    private val counter = meterRegistry.counter("processed-records-amount-$profile")

    fun registerQueueAwaitingMetric(queueAwaitingTimeMs: Long) =
        TimeGauge.builder("queue-awaiting-time-$profile", { queueAwaitingTimeMs }, TimeUnit.MILLISECONDS)
            .register(meterRegistry)

    fun registerAmountOfProcessedRecords(amount: Int) {
        counter.increment(amount.toDouble())
    }

}
