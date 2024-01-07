package pt.isel.study.daw

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.study.daw.t1_2021.Interceptor_T1_2021
import pt.isel.study.daw.t2_2021.Interceptor_T2_2021
import pt.isel.study.daw.tee_2021.Interceptor_TEE_2021

@SpringBootApplication
class DawApplication {

	@Configuration
	class Config(
		//val interceptor: Interceptor_T1_2021
		//val interceptor: Interceptor_T2_2021
		val interceptor: Interceptor_TEE_2021
	): WebMvcConfigurer {

		override fun addInterceptors(registry: InterceptorRegistry) {
			registry.addInterceptor(interceptor)
		}

	}
}

fun main(args: Array<String>) {
	runApplication<DawApplication>(*args)
}
