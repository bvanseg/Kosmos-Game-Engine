package com.kosmos.engine.common.network.message.decode

import bvanseg.kotlincommons.any.getLogger
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageHeader
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class MessageDecoder: ReplayingDecoder<Message>() {

    val logger = getLogger()

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        try {
            val engine = KosmosEngine.getInstance()

            val messageHeader = MessageHeader(UUID(input.readLong(), input.readLong()), input.readInt(), input.readInt())

            val factoryEntry = engine.messageRegistry.getEntryByID(messageHeader.messageID)

            if (factoryEntry == null) {
                logger.warn("Failed to find factory entry for message with id ${messageHeader.messageID}")
                return
            }

            val message = factoryEntry.createInstance()

            if (message == null) {
                logger.warn("Failed to create message instance from factory entry for message with id ${messageHeader.messageID}")
                return
            }

            if (!message.isCorrectTarget(ctx.channel())) {
                logger.warn("Received a message '${message::class.java}' with the incorrect target side: Expected message targeting ${message.getSide(ctx.channel())} but got ${message.targetSide} instead!")
                return
            }

            message.header = messageHeader

            message.read(input)

            out.add(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}