package me.jangjunha.ftgo.order_history_service.web

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class HealthController {
    @RequestMapping(path = ["/ping/"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun health() {}
}
