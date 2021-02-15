package com.kosmos.engine.server.network

import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.ctx.MessageContext
import io.netty.channel.Channel
import java.util.UUID

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
    val timestampConnected: Long): Networker(Side.SERVER) {

    override fun send(message: Message<out MessageContext>) {
        channel.writeAndFlush(message)
        messagesSent.getAndIncrement()
    }
}