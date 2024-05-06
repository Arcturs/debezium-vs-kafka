package ru.vsu.csf.asashina.paymentprocessing.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.vsu.csf.asashina.paymentprocessing.model.dto.CreatePaymentRequest
import ru.vsu.csf.asashina.paymentprocessing.model.dto.UpdatePaymentStatusRequest
import ru.vsu.csf.asashina.paymentprocessing.service.CreatePaymentService
import ru.vsu.csf.asashina.paymentprocessing.service.GetPaymentService
import ru.vsu.csf.asashina.paymentprocessing.service.UpdatePaymentStatusService

@RestController
@RequestMapping("/payment")
@Tag(name = "Payment Controller")
class PaymentController(
    private val createPaymentService: CreatePaymentService,
    private val getPaymentService: GetPaymentService,
    private val updatePaymentStatusService: UpdatePaymentStatusService
) {

    @GetMapping("/{id}")
    @Operation(description = "Получить информацию о выплате по ИД", operationId = "getPaymentById")
    fun getPaymentById(
        @Parameter(
            description = "ИД выплаты",
            `in` = ParameterIn.PATH,
            required = true
        )
        @PathVariable("id") paymentId: Long
    ) = ResponseEntity.ok(getPaymentService.getPaymentById(paymentId))

    @PostMapping
    @Operation(description = "Создать выплату", operationId = "createPayment")
    fun createPayment(@RequestBody @Valid request: CreatePaymentRequest) =
        ResponseEntity.status(201)
            .body(createPaymentService.createPayment(request))

    @PutMapping("/{id}")
    @Operation(description = "Обновить статус выплаты", operationId = "updatePaymentStatus")
    fun updatePaymentStatus(
        @Parameter(
            description = "ИД выплаты",
            `in` = ParameterIn.PATH,
            required = true
        )
        @PathVariable("id") paymentId: Long,
        @RequestBody @Valid request: UpdatePaymentStatusRequest
    ) = ResponseEntity.status(204)
            .body(updatePaymentStatusService.updatePaymentStatus(paymentId, request))

}
