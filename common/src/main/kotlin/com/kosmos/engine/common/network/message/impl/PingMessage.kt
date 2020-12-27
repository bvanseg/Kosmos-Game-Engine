package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.message.ctx.MessageContextImpl
import io.netty.buffer.ByteBuf

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class PingMessage: Message<MessageContextImpl>(MessageTarget.COMMON) {

    private var timestamp: Long = 0L

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(System.currentTimeMillis())
    }

    override fun read(buffer: ByteBuf) {
        timestamp = buffer.readLong()
    }

    override fun handle(ctx: MessageContextImpl) {
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