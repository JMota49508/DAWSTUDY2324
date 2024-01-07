package pt.isel.study.daw.t2_2021

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
class Storage {
    val map = mutableMapOf<String, Int>()
    private val lock = ReentrantLock()

    fun inc(uri: String) {
        lock.withLock {
            val count = map[uri] ?: 0
            map[uri] = count + 1
        }
    }

    fun get(): Map<String, Int> {
        lock.withLock {
            return map
        }
    }
}

@Component
class Interceptor_T2_2021(
    val storage: Storage
): HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.cookies == null || request.getHeader("Authorization") == null) {
            storage.inc(request.requestURI)
        }
        return true
    }
}

@RestController
class ControllerT2_2021(val storage: Storage) {

    @GetMapping("/anonymous")
    fun anonymous(): ResponseEntity<Map<String, Int>> {
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(storage.get())
    }

    @GetMapping("/xpto")
    fun xpto(): ResponseEntity<String>  {
        return ResponseEntity.ok("Hello xpto")
    }
}