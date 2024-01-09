package pt.isel.study.daw.t2_2223

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView


@RestController
class Controller_T2_2223() {

    @GetMapping("/example")
    fun getExample(): ResponseEntity<String> {
        Thread.sleep(2000)
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("Example")
    }
}

@Component
class Interceptor_T2_2223 : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(Interceptor_T2_2223::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            request.setAttribute("startTime", System.currentTimeMillis())
        }
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (handler is HandlerMethod) {
            val startTime = request.getAttribute("startTime") as Long
            val elapsed = System.currentTimeMillis() - startTime
            logger.info(
                "{\nMethod = ${handler.method.name}\nURI = ${request.requestURI}\nStatus Code = ${response.status}\n" +
                        "Elapsed Time = ${elapsed}ms\nHandler-Id=${handler.shortLogMessage}\n}"
            )
        }
    }
}
