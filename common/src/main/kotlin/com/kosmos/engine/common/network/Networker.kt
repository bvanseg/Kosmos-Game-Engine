package com.kosmos.engine.common.network

import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.ctx.MessageContext
import com.kosmos.engine.common.network.message.ctx.MessageContextImpl
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.LinkedHashMap

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class Networker(val side: Side, contextFactory: (() -> MessageContext)? = null) {

    var contextFactory: (() -> MessageContext) = contextFactory ?: {
        MessageContextImpl(this@Networker)
    }

    init {
        System.setProperty("io.netty.tryReflectionSetAccessible", "true")
    }

    abstract val channel: Channel
    abstract var uuid: UUID

    val messagesSent: AtomicLong = AtomicLong(0L)
    val messagesReceived: AtomicLong = AtomicLong(0L)

    val unresolvedMessages: MutableMap<Long, Message<MessageContext>>? = when(side) {
        Side.CLIENT -> Collections.synchronizedMap(LinkedHashMap())
        Side.SERVER -> null
    }

    abstract fun send(message: Message<out MessageContext>)

    fun <T> getAttribute(key: String): T? {
        val attributeKey = AttributeKey.valueOf<T>(key)
        return channel.attr(attributeKey).get()
    }

    fun <T> setAttribute(key: String, value: T) {
        val attributeKey = AttributeKey.valueOf<T>(key)
        channel.attr(attributeKey).set(value)
    }
}