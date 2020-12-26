package com.kosmos.engine.server.network

import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.message.Message
import io.netty.channel.Channel
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * Represents a client as the server sees it.
 *
 * @param uuid The [UUID] representing the client.
 *
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class DummyClient(
    /**
     * The channel connecting the server to the dummy client.
     */
    override val channel: Channel,

    override var uuid: UUID,
    /**
     * The time relative to the server at which the client connected.
     */
    val timestampConnected: Long,
    /**
     * The number of messages the server has sent to the client.
     */
    var messagesSent: AtomicLong = AtomicLong(0L),
    /**
     * The number of messages the server has received from the client.
     */
    var messagesReceived: AtomicLong = AtomicLong(0L)): Networker() {

    override fun send(message: Message) {
        channel.writeAndFlush(message)
        messagesSent.getAndIncrement()
    }
}