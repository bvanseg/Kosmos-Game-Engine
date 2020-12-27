package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.message.ctx.MessageContext
import io.netty.buffer.ByteBuf

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class PingMessage: Message<MessageContext>(MessageTarget.COMMON) {

    private var timestamp: Long = System.currentTimeMillis()

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(timestamp)
    }

    override fun read(buffer: ByteBuf) {
        timestamp = buffer.readLong()
    }

    override fun handle(ctx: MessageContext) {
        when (side) {
            Side.CLIENT -> {
                logger.debug("Ping: ${System.currentTimeMillis() - timestamp}ms")
            }
            Side.SERVER -> {
                logger.debug("Server side received ping from client ${ctx.networker.uuid}. Echoing...")
                ctx.networker.send(this)
            }
        }
    }
}