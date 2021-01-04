package com.example.swsse

import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var temperatureSensor: TemperatureSensor

    @RequestMapping(
        value = arrayOf("/temperature-stream"),
        method = arrayOf(RequestMethod.GET)
    )
    fun events(request: HttpServletRequest) : SseEmitter {
        val emitter = RxSseEmitter()

        temperatureSensor.temperatureStream()
            .subscribe(emitter.getSubscriber())

        return emitter
    }
}