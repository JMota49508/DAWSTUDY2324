package pt.isel.study.daw.t2_2122

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class StatusOutputModel(
    val minTime: Long,
    val maxTime: Long
)

@RestController
class Controller_T2_2122(private val storage: Storage_T2_2122) {

    @GetMapping("/status/{method}")
    fun getStatus(@PathVariable method: String): ResponseEntity<StatusOutputModel> {
        val rsp = storage.getStatus(method) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(rsp)
    }

    @GetMapping("/hallo")
    fun hallo(): ResponseEntity<String> {
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("HALLO!")
    }

    @GetMapping("/meepmoop")
    fun meepMoop(): ResponseEntity<String> {
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("MEEPMOOP!")
    }

    @GetMapping("/byebye")
    fun byeBye(): ResponseEntity<String> {
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("BYEBYE")
    }

}

@Component
class Interceptor_T2_2122(private val storage: Storage_T2_2122): HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.requestURI.startsWith("/status")) return true
        val method = (handler as HandlerMethod).method.name
        request.setAttribute("method", method)
        request.setAttribute("time", System.currentTimeMillis())
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (request.requestURI.startsWith("/status")) return
        val method = request.getAttribute("method") as String
        val time = request.getAttribute("time") as Long
        val elapsed = System.currentTimeMillis() - time
        storage.updateStatus(method, elapsed)
    }
}

@Component
class Storage_T2_2122 {

    private val map = mutableMapOf<String, StatusOutputModel>()
    private val lock = ReentrantLock()

    fun getStatus(method: String): StatusOutputModel? {
        lock.withLock {
            return map[method]
        }
    }

    fun updateStatus(method: String, time: Long) {
        lock.withLock {
            val current = map[method]
            if (current == null) {
                map[method] = StatusOutputModel(time, time)
            } else {
                map[method] = StatusOutputModel(
                    minOf(current.minTime, time),
                    maxOf(current.maxTime, time)
                )
            }
        }
    }

}