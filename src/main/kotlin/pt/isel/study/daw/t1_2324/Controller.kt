package pt.isel.study.daw.t1_2324

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Request(
    val method: String,
    val uri: String,
    val status: Int,
    val elapsed: Long,
    val controller: String,
    val handler: String
)

data class RequestListOutputModel(val status: List<Request>)

@Component
class Filter_T1_2324(private val storage: Storage_T1_2324) : HttpFilter() {
    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        log.info("doFilter: before chain call")
        val start = System.currentTimeMillis()
        chain.doFilter(request, response)
        log.info("doFilter: after chain call")
        if (response.status in 400 until 600) {
            val elapsed = System.currentTimeMillis() - start
            val controller = request.getAttribute("Controller") as String
            val handlerMethod = request.getAttribute("HandlerMethod") as String
            storage.add(
                Request(
                    method = request.method,
                    uri = request.requestURI,
                    status = response.status,
                    elapsed = elapsed,
                    controller = controller,
                    handler = handlerMethod
                )
            )
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Filter_T1_2324::class.java)
    }
}

@Component
class Interceptor_T1_2324 : HandlerInterceptor {
    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        @Nullable modelAndView: ModelAndView?
    ) {
        log.info("on postHandle")
        if (response.status !in 400 until 600) return
        val handlerMethod = handler as HandlerMethod
        request.setAttribute("Controller", handlerMethod.beanType.simpleName)
        request.setAttribute("HandlerMethod", handlerMethod.method.name)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Interceptor_T1_2324::class.java)
    }
}

@RestController
class Controller_T1_2324(val storage: Storage_T1_2324) {
    @GetMapping("/failures/{status}")
    fun getFailures(@PathVariable status: Int): ResponseEntity<RequestListOutputModel> {
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(storage.get(status))
    }
}

@Component
class Storage_T1_2324 {
    private val lock = ReentrantLock()
    private val list = mutableListOf<Request>()

    fun add(request: Request) {
        lock.withLock {
            list.add(request)
        }
    }

    fun get(status: Int): RequestListOutputModel {
        lock.withLock {
            val filtered = list.filter { it.status == status }.takeLast(10)
            return RequestListOutputModel(filtered)
        }
    }
}