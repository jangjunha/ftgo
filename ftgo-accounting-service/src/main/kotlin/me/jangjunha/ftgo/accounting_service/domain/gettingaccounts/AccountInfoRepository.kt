package me.jangjunha.ftgo.accounting_service.domain.gettingaccounts

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface AccountInfoRepository
    : CrudRepository<AccountInfo, UUID>
    , PagingAndSortingRepository<AccountInfo, UUID>
