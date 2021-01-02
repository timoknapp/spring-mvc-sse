package com.example.swsse

import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArraySet
import javax.servlet.http.HttpServletRequest

@RestController
class TemperatureController {
    val clients: MutableSet<SseEmitter> = CopyOnWriteArraySet<SseEmitter>()

    @RequestMapping(
        value = arrayOf("/temperature-stream"),
        method = arrayOf(RequestMethod.GET)
    )
    fun events(request: HttpServletRequest) : SseEmitter {
        val emitter = SseEmitter()
        clients.add(emitter)

        //Remove emitter from clients on error or disconnect
        emitter.onTimeout { clients.remove(emitter) }
        emitter.onCompletion { clients.remove(emitter) }
        return emitter
    }

    @Async
    @EventListener
    fun handleMessage(temperature: Temperature) {
        val deadEmitters = mutableListOf<SseEmitter>()
        clients.forEach { emitter ->
            try {
                emitter.send(temperature, MediaType.APPLICATION_JSON)
            } catch (e: Exception) {
                deadEmitters.add(emitter)
            }
        }
        clients.removeAll(deadEmitters)
    }
}