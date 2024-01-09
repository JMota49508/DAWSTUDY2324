package pt.isel.study.daw.tee_2223

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

data class Error(
    val received: Long,
    val method: String,
    val uri: String,
    val controller: String
)

data class ErrorsOutputModel(
    val errors: List<Error>
)

@RestController
class Controller_TEE_2223(private val storage: Storage_TEE_2223) {

    @GetMapping("/errors")
    fun getErrors(): ResponseEntity<ErrorsOutputModel> {
        val rsp = storage.getErrors()
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(ErrorsOutputModel(rsp))
    }

    @GetMapping("/fail2223")
    fun getFail(): ResponseEntity<String> {
        return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body("Fail")
    }

}

@Component
class Interceptor_TEE_2223(private val storage: Storage_TEE_2223) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setAttribute("received", System.currentTimeMillis())
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (response.status >= 500) {
            val received = request.getAttribute("received") as Long
            val method = request.method
            val uri = request.requestURI
            val controller = (handler as HandlerMethod).beanType.simpleName
            val error = Error(received, method, uri, controller)
            storage.addError(error)
        }
    }
}

@Component
class Storage_TEE_2223 {

    private val lock = ReentrantLock()
    private val errors = mutableListOf<Error>()

    fun getErrors(): List<Error> {
        lock.withLock {
            val now = System.currentTimeMillis()
            return errors.filter { now - it.received < 600000 }
        }
    }

    fun addError(error: Error) {
        lock.withLock {
            errors.add(error)
        }
    }
}