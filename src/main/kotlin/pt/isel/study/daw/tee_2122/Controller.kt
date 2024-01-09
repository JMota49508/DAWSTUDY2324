package pt.isel.study.daw.tee_2122

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
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Failure(
    val method: String,
    val uri: String,
    val status: Int,
    val controller: String,
    val handler: String
)

data class FailuresOutputModel(
    val failures: List<Failure>
)

@RestController
class Controller_TEE_2122(private val storage: Storage_TEE_2122) {

    @GetMapping("/failures")
    fun getFailures(): ResponseEntity<FailuresOutputModel> {
        val rsp = FailuresOutputModel(storage.get())
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(rsp)
    }

    @GetMapping("/error1")
    fun fail1() = ResponseEntity.status(500).body("YA ERRO 1")

    @GetMapping("/error2")
    fun fail2() = ResponseEntity.status(500).body("YA ERRO 2")

}

@Component
class Interceptor_TEE_2122(private val storage: Storage_TEE_2122) : HandlerInterceptor {

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (response.status < 500 || request.requestURI == "/failures") return
        val handlerMethod = handler as HandlerMethod
        val failure = Failure(
            method = request.method,
            uri = request.requestURI,
            status = response.status,
            controller = handlerMethod.beanType.simpleName,
            handler = handlerMethod.method.name
        )
        storage.add(failure)
    }
}

@Component
class Storage_TEE_2122 {

    private val lock = ReentrantLock()
    private val map = mutableListOf<Failure>()

    fun get(): List<Failure> {
        lock.withLock {
            return map.takeLast(10)
        }
    }

    fun add(failure: Failure) {
        lock.withLock {
            map.add(failure)
        }
    }

}