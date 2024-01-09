package pt.isel.study.daw.t1_2223

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
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class HandlersOutputModel(
    val handlers: List<String>
)

data class HandlerOutputModel(
    val count: Int
)

data class Handler(
    val id: String,
    val link: String,
    val count: Int
)

@RestController
class Controller_T1_2223(private val storage: Storage_T1_2223) {

    @GetMapping("/handlers2223")
    fun getHandlers(): ResponseEntity<HandlersOutputModel> {
        val rsp = HandlersOutputModel(storage.getHandlers())
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(rsp)
    }

    @GetMapping("/handlers2223/{handlerId}")
    fun getHandler(@PathVariable handlerId: String): ResponseEntity<HandlerOutputModel> {
        val rsp = storage.getHandler(handlerId) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(HandlerOutputModel(rsp.count))
    }

}

@Component
class Interceptor_T1_2223(private val storage: Storage_T1_2223): HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerMethod = handler as HandlerMethod
        val id = handlerMethod.shortLogMessage
        storage.addHandler(id,request.requestURI)
        return true
    }

}

@Component
class Storage_T1_2223 {

    private val lock = ReentrantLock()
    private val list = mutableListOf<Handler>()

    fun getHandlers(): List<String> {
        lock.withLock {
            return list.map { it.link }
        }
    }

    fun getHandler(id: String): Handler? {
        lock.withLock { return list.firstOrNull { it.id == id } }
    }

    fun addHandler(id:String, link: String) {
        lock.withLock {
            val handler = list.firstOrNull { it.id == id }
            if (handler == null) {
                list.add(Handler(id, link, 1))
            } else {
                val i = list.indexOf(handler)
                list[i] = handler.copy(count = handler.count + 1)
            }
        }
    }

}