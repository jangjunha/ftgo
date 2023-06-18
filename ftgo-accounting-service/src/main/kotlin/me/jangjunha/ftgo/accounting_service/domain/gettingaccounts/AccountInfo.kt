package me.jangjunha.ftgo.accounting_service.domain.gettingaccounts

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import me.jangjunha.ftgo.common.Money
import java.util.*

@Entity
@Table(name = "account_infos")
data class AccountInfo(
    @Id
    val id: UUID = UUID(0, 0),

    @Embedded
    @AttributeOverrides(
        AttributeOverride(
            name = "amount",
            column = Column(name = "deposit_accumulate_amount", nullable = false),
        )
    )
    val depositAccumulate: Money = Money.ZERO,

    @Column(nullable = false)
    val depositCount: Long = 0,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(
            name = "amount",
            column = Column(name = "withdraw_accumulate_amount", nullable = false),
        )
    )
    val withdrawAccumulate: Money = Money.ZERO,

    @Column(nullable = false)
    val withdrawCount: Long = 0,

    @Column(nullable = false)
    @JsonIgnore
    val lastProcessedPosition: Long = 0,
)
