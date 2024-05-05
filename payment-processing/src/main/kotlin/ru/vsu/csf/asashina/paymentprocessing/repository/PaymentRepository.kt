package ru.vsu.csf.asashina.paymentprocessing.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.vsu.csf.asashina.paymentprocessing.model.entity.Payment

@Repository
interface PaymentRepository : CrudRepository<Payment, Long>
