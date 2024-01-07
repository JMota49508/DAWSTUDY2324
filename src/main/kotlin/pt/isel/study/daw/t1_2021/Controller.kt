package pt.isel.study.daw.t1_2021

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

data class HandlerStats(
    val counter: Int,
    private val totalTime: Long,
    val averageTime: Double = 0.0
) {
    fun calculateAverage(elapsed: Long) = (totalTime.toDouble() + elapsed.toDouble()) / (counter.toDouble() + 1)
    fun getTime() = totalTime
}

private val handlers = mutableMapOf<String, HandlerStats>()

@Component
class Interceptor_T1_2021 : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.requestURI == "/handlers") return true
        val handlerMethod = handler as HandlerMethod
        val handlerKey = handlerMethod.shortLogMessage
        if (!handlers.containsKey(handlerKey)) handlers[handlerKey] = HandlerStats(0, 0)
        request.setAttribute("handlerKey", handlerKey)
        request.setAttribute("startTime", System.currentTimeMillis())
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (request.requestURI == "/handlers") return
        val handlerKey = request.getAttribute("handlerKey") as String
        val startTime = request.getAttribute("startTime") as Long
        val endTime = System.currentTimeMillis()
        val elapsed = endTime - startTime
        handlers[handlerKey] = handlers[handlerKey]!!.copy(
            counter = handlers[handlerKey]!!.counter + 1,
            totalTime = handlers[handlerKey]!!.getTime() + elapsed,
            averageTime = handlers[handlerKey]!!.calculateAverage(elapsed)
        )
    }
}

@RestController
class ControllerT1_2021 {

    @GetMapping("/handlers")
    fun getHandlers(): ResponseEntity<MutableMap<String, HandlerStats>> {
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(handlers)
    }

    @GetMapping("/hello")
    fun getCounter(): ResponseEntity<String> {
        return ResponseEntity.status(200).body("Hello")
    }

    @GetMapping("/bye")
    fun bye(): ResponseEntity<String> {
        return ResponseEntity.status(200).body("Goodbye")
    }
}