package me.jangjunha.ftgo.consumer_service.web

import me.jangjunha.ftgo.common.web.AuthContextArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfigurer: WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthContextArgumentResolver())
    }
}
