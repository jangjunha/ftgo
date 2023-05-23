package me.jangjunha.ftgo.consumer_service.domain

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ConsumerRepository: CrudRepository<Consumer, UUID> {}