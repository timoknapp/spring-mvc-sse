package com.example.swsse

import org.springframework.stereotype.Component
import rx.Observable
import java.util.Random
import java.util.concurrent.TimeUnit

@Component
class TemperatureSensor {
    private val rnd = Random()

    private val dataStream =
        Observable
            .range(0, Integer.MAX_VALUE)
            .concatMap {
                Observable.just(it)
                    .delay(rnd.nextInt(5000).toLong(), TimeUnit.MILLISECONDS)
                    .map { this.probe() }
            }
            .publish()
            .refCount()

    fun probe() = Temperature(16 + rnd.nextGaussian() * 10)

    fun temperatureStream() = dataStream
}