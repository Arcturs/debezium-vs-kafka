package ru.vsu.csf.asashina.paymentanalyzing.config

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricConfig {

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> =
        MeterRegistryCustomizer { it.config().commonTags("service", "payment-analyzing") }

}
