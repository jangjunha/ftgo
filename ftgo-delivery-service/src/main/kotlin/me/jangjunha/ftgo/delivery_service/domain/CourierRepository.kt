package me.jangjunha.ftgo.delivery_service.domain

import org.springframework.data.repository.CrudRepository
import java.util.UUID
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

interface CourierRepository: CrudRepository<Courier, UUID>, CustomCourierRepository

interface CustomCourierRepository {
    fun findAllAvailable(): List<Courier>

    fun findByIdOrNullWithPlan(id: UUID): Courier?
}

class CustomCourierRepositoryImpl: QuerydslRepositorySupport(Courier::class.java), CustomCourierRepository {
    override fun findAllAvailable(): List<Courier> {
        val courier = QCourier.courier
        val query = from(courier)
            .where(courier.available.isTrue())
        return query.fetch()
    }

    override fun findByIdOrNullWithPlan(id: UUID): Courier? {
        val courier = QCourier.courier
        val query = from(courier)
            .join(courier.plan.actions).fetchJoin()
            .where(courier.id.eq(id))
        return query.fetchFirst()
    }
}
