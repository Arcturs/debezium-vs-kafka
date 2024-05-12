package ru.vsu.csf.asashina.paymentanalyzing.listener

import com.fasterxml.jackson.databind.ObjectMapper
import io.debezium.data.Envelope.FieldName.*
import io.debezium.data.Envelope.Operation
import io.debezium.embedded.Connect
import io.debezium.engine.DebeziumEngine
import io.debezium.engine.RecordChangeEvent
import io.debezium.engine.format.ChangeEventFormat
import io.debezium.engine.format.Json
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.SourceRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentanalyzing.model.debezium.PaymentChangeLogRecord
import ru.vsu.csf.asashina.paymentanalyzing.service.ProcessPaymentChangeLogService
import java.util.concurrent.Executors

@Component
class DebeziumListener(
    debeziumConfig: io.debezium.config.Configuration,
    private val objectMapper: ObjectMapper,
    private val processPaymentChangeLogService: ProcessPaymentChangeLogService,
    private var debeziumEngine: DebeziumEngine<RecordChangeEvent<SourceRecord>>? = null
) {

    init {
        debeziumEngine = DebeziumEngine
            .create(ChangeEventFormat.of(Connect::class.java))
            .using(debeziumConfig.asProperties())
            .notifying { recordChangeEvents, committer ->
                handleChangeEvent(recordChangeEvents)
                committer.markBatchFinished()
            }
            .build()
    }

    private fun handleChangeEvent(recordChangeEvents: List<RecordChangeEvent<SourceRecord>>) {
        // TODO: Замерять время
        val records = recordChangeEvents.map { it.record() }
        log.trace("---- Были получены следующие изменения из БД paymentdb: {} ----", records.map { it.value() })
        val paymentStatusToCount = PaymentStatus.values()
            .associateWith { 0 }
            .toMutableMap()
        records.map { it.value() as Struct }
            .forEach { record ->
                val operation = record.get(OPERATION) as Operation
                val recordCondition =
                    if (operation == Operation.DELETE) BEFORE
                    else AFTER
                val recordJson = record.get(recordCondition) as Json
                val paymentChangeLog = objectMapper.readValue(recordJson.toString(), PaymentChangeLogRecord::class.java)
                val coeff =
                    if (operation == Operation.DELETE) -1
                    else 1
                paymentStatusToCount[paymentChangeLog.actualStatus] =
                    paymentStatusToCount[paymentChangeLog.actualStatus]!! + coeff
            }
        processPaymentChangeLogService.process(paymentStatusToCount)
        log.trace("---- Успешно обработано {} записей ----", paymentStatusToCount.values.sum())
    }

    @PostConstruct
    private fun start() = debeziumEngine?.let { Executors.newFixedThreadPool(4).execute(it) }

    @PreDestroy
    private fun stop() = debeziumEngine?.close()

    private companion object {
        val log: Logger = LoggerFactory.getLogger(DebeziumListener::class.java)
    }

}
