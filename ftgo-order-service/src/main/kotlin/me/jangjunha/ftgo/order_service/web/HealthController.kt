package me.jangjunha.ftgo.order_service.web

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class HealthController @Autowired constructor(
    private val em: EntityManager,
) {
    @RequestMapping(path = ["/ping/"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun health(@RequestParam(required = false, defaultValue = "false") include_db: Boolean) {
        if (include_db) {
            val q = em.createNativeQuery("SELECT 1")
            q.singleResult
        }
    }
}
