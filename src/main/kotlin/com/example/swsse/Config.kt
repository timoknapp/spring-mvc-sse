package com.example.swsse

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class Config : AsyncConfigurer {
    override fun getAsyncExecutor(): Executor? {
		val executor = ThreadPoolTaskExecutor()
		executor.corePoolSize = 2
		executor.maxPoolSize = 100
		executor.setQueueCapacity(5)
		executor.initialize()
		return executor
	}

	override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
		return SimpleAsyncUncaughtExceptionHandler()
	}
}