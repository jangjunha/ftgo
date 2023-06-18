package me.jangjunha.ftgo.accounting_service.domain.gettingbyid

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AccountDetailsRepository: JpaRepository<AccountDetails, UUID>
