package me.jangjunha.ftgo.accounting_service.domain.gettingbyid

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.jangjunha.ftgo.common.Money
import java.util.*

@Entity
@Table(name = "account_details")
data class AccountDetails(
    @Id
    val id: UUID = UUID(0, 0),

    @Column(nullable = false)
    val amount: Money = Money.ZERO,

    @JsonIgnore
    @Column(nullable = false)
    private var version: Long = 0,

    @JsonIgnore
    @Column(nullable = false)
    var lastProcessedPosition: Long = 0,
)
