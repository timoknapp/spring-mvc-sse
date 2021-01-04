package com.example.swsse

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import rx.Subscriber
import java.io.IOException

import java.util.concurrent.atomic.AtomicInteger

class RxSseEmitter : SseEmitter(SSE_SESSION_TIMEOUT) {
    private val log: Logger = LoggerFactory.getLogger(RxSseEmitter::class.java)

    val sessionId = sessionIdSequence.incrementAndGet()
    private val subscriber: Subscriber<Temperature>


    companion object {
        const val SSE_SESSION_TIMEOUT = 30 * 60 * 1000L
        private val sessionIdSequence = AtomicInteger(0)
    }

    init {
        subscriber = object : Subscriber<Temperature>() {
            override fun onNext(p0: Temperature) {
                try {
                    this@RxSseEmitter.send(p0)
                    log.info("[{}] << {} ", sessionId, p0.value)
                } catch (e: IOException) {
                    log.warn(
                        "[{}] Can not send event to SSE, closing subscription, message: {}",
                        sessionId, e.message
                    )
                    unsubscribe()
                }
            }

            override fun onError(e: Throwable) {
                log.warn("[{}] Received sensor error: {}", sessionId, e.message)
            }

            override fun onCompleted() {
                log.warn("[{}] Stream completed", sessionId)
            }
        }
        onCompletion {
            log.info("[{}] SSE completed", sessionId)
            subscriber.unsubscribe()
        }
        onTimeout {
            log.info("[{}] SSE timeout", sessionId)
            subscriber.unsubscribe()
        }
    }

    fun getSubscriber(): Subscriber<Temperature> {
        return subscriber
    }
}
