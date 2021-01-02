package com.example.swsse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Component
class TemperatureSensor {
    private val rnd = Random()
    private val executor = Executors.newSingleThreadScheduledExecutor()

    @Autowired
    private lateinit var publisher: ApplicationEventPublisher

    @PostConstruct
    fun startProcessing() {
        executor.schedule(this::probe, 1, TimeUnit.SECONDS)
    }

    fun probe() {
        val temperature = 16 + rnd.nextGaussian() * 10
        publisher.publishEvent(Temperature(temperature))
        executor.schedule(this::probe, rnd.nextInt(5000).toLong(), TimeUnit.MILLISECONDS)
    }
}