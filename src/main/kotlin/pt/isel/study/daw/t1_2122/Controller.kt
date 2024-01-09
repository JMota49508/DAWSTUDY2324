package pt.isel.study.daw.t1_2122

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class PendingOutputModel(val inPending: Int)

@RestController
class Controller_T1_2122(private val storage: Storage_T1_2122) {

    @GetMapping("/pending")
    fun getPending(): ResponseEntity<PendingOutputModel> {
        val rsp = PendingOutputModel(storage.getPending())
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(rsp)
    }

    @GetMapping("/await")
    fun await() {
        Thread.sleep(20000)
    }

}

@Component
class Interceptor_T1_2122(private val storage: Storage_T1_2122) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.requestURI == "/pending") return true
        storage.incrementPending()
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (request.requestURI == "/pending") return
        storage.decrementPending()
    }
}


@Component
class Storage_T1_2122 {

    private var pending = 0
    private val lock = ReentrantLock()

    fun getPending(): Int {
        lock.withLock {
            return pending
        }
    }

    fun incrementPending() {
        lock.withLock {
            pending++
        }
    }

    fun decrementPending() {
        lock.withLock {
            pending--
        }
    }

}