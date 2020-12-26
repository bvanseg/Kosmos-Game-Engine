package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageTarget
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class PingMessage: Message(MessageTarget.COMMON) {

    private var timestamp: Long = 0L

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(System.currentTimeMillis())
    }

    override fun read(buffer: ByteBuf) {
        timestamp = buffer.readLong()
    }

    override fun handle(networker: Networker) {
        getSide(networker)?.let { side ->
            when (side) {
                Side.CLIENT -> {
                    logger.debug("Ping: ${System.currentTimeMillis() - timestamp}ms")
                }
                Side.SERVER -> {
                    logger.debug("Server side received ping from client ${networker.uuid}. Echoing...")
                    networker.send(this)
                }
            }
        }
    }
}