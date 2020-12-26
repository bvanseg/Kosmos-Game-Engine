package com.kosmos.engine.common.network

import com.kosmos.engine.common.network.message.Message
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class Networker {

    abstract val channel: Channel
    abstract var uuid: UUID

    abstract fun send(message: Message)

    fun <T> getAttribute(key: String): T? {
        val attributeKey = AttributeKey.valueOf<T>(key)
        return channel.attr(attributeKey).get()
    }

    fun <T> setAttribute(key: String, value: T) {
        val attributeKey = AttributeKey.valueOf<T>(key)
        channel.attr(attributeKey).set(value)
    }
}