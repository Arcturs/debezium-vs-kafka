package ru.vsu.csf.asashina.paymentanalyzing.listener

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.debezium.data.Envelope.FieldName.*
import io.debezium.data.Envelope.Operation
import io.debezium.embedded.Connect
import io.debezium.engine.DebeziumEngine
import io.debezium.engine.RecordChangeEvent
import io.debezium.engine.format.ChangeEventFormat
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.kafka.connect.data.Field
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.SourceRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.vsu.csf.asashina.paymentanalyzing.dictionary.PaymentStatus
import ru.vsu.csf.asashina.paymentanalyzing.metric.RegisterMetricService
import ru.vsu.csf.asashina.paymentanalyzing.model.debezium.PaymentChangeLogRecord
import ru.vsu.csf.asashina.paymentanalyzing.service.ProcessPaymentChangeLogService
import java.time.OffsetDateTime
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.system.measureTimeMillis

@Component
class DebeziumListener(
    debeziumConfig: io.debezium.config.Configuration,
    private val objectMapper: ObjectMapper,
    private val processPaymentChangeLogService: ProcessPaymentChangeLogService,
    private val metricService: RegisterMetricService,
    @Value("\${spring.profiles.active}")
    private val profile: String,
    private var debeziumEngine: DebeziumEngine<RecordChangeEvent<SourceRecord>>? = null,
) {

    init {
        if (profile != "kafka") {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            debeziumEngine = DebeziumEngine
                .create(ChangeEventFormat.of(Connect::class.java))
                .using(debeziumConfig.asProperties())
                .notifying { recordChangeEvents, committer ->
                    handleChangeEvents(recordChangeEvents)
                    committer.markBatchFinished()
                }
                .build()
        }
    }

    private fun handleChangeEvents(recordChangeEvents: List<RecordChangeEvent<SourceRecord>>) {
        log.trace("[DEBEZIUM] Были получены изменения из БД paymentdb [DEBEZIUM]")
        val processTime = measureTimeMillis {
            val receiveTime = OffsetDateTime.now().toEpochSecond()
            val records = recordChangeEvents.map { it.record() }
            val paymentStatusToCount = PaymentStatus.values()
                .associateWith { 0 }
                .toMutableMap()
            records.map { it.value() as Struct }
                .forEach { record ->
                    val operation = Operation.forCode(record.get(OPERATION) as String)
                    val recordCondition =
                        if (operation == Operation.DELETE) BEFORE
                        else AFTER
                    val recordConditionStruct = record.get(recordCondition) as Struct
                    val recordFieldMap = mapStructToMap(recordConditionStruct)
                    val paymentChangeLog = objectMapper.readValue(
                        objectMapper.writeValueAsString(recordFieldMap),
                        PaymentChangeLogRecord::class.java
                    )
                    metricService.registerQueueAwaitingMetric(
                        abs((receiveTime - (paymentChangeLog.rowUpdateTime!! / 1_000_000)) * 1000)
                    )
                    val coeff =
                        if (operation == Operation.DELETE) -1
                        else 1
                    paymentStatusToCount[paymentChangeLog.actualStatus] =
                        paymentStatusToCount[paymentChangeLog.actualStatus]!! + coeff
                }
            processPaymentChangeLogService.process(paymentStatusToCount)
            log.trace("[DEBEZIUM] Успешно обработано {} записей [DEBEZIUM]", paymentStatusToCount.values.sum())
        }
        metricService.registerAmountOfProcessedRecords(recordChangeEvents.size)
        log.trace("[DEBEZIUM] Записи были обработаны за {} миллисекунд [DEBEZIUM]", processTime)
    }

    @PostConstruct
    private fun start() = debeziumEngine?.let { Executors.newFixedThreadPool(1).execute(it) }

    @PreDestroy
    private fun stop() = debeziumEngine?.close()

    private companion object {
        val log: Logger = LoggerFactory.getLogger(DebeziumListener::class.java)

        @JvmStatic
        fun mapStructToMap(struct: Struct): Map<String, Any> {
            return struct.schema()
                .fields()
                .map(Field::name)
                .filter { struct.get(it) != null }
                .associateWith { struct.get(it) }
        }
    }

}
