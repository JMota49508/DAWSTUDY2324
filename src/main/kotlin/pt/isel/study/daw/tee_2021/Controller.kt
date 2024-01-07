package pt.isel.study.daw.tee_2021

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

@Component
class Interceptor_TEE_2021: HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerMethod = handler as HandlerMethod
        request.setAttribute("handlerMethod", handlerMethod.method.name)
        request.setAttribute("startTime", System.currentTimeMillis())
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        val handlerMethod = request.getAttribute("handlerMethod") as String
        val startTime = request.getAttribute("startTime") as Long
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        val header = "$handlerMethod took $duration ms"
        response.addHeader("HTTP Debug", header)
    }
}

@RestController
class ControllerTEE_2021 {

    @GetMapping("/debug")
    fun debug(response: HttpServletResponse) {
        ResponseEntity.ok("Hello Debug")
    }

}