package com.kosmos.engine.common.network.message.decode

import bvanseg.kotlincommons.any.getLogger
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageHeader
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder
import io.netty.util.AttributeKey

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class MessageDecoder: ReplayingDecoder<Message>() {

    val logger = getLogger()

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        try {
            val engine = KosmosEngine.getInstance()

            val header = MessageHeader.read(input)
            logger.debug("Reading message with header info $header")

            val factoryEntry = engine.messageRegistry.getEntryByID(header.messageTypeID)

            if (factoryEntry == null) {
                logger.warn("Failed to find factory entry for message with id ${header.messageTypeID}")
                return
            }

            val message = factoryEntry.createInstance()

            if (message == null) {
                logger.warn("Failed to create message instance from factory entry for message with id ${header.messageTypeID}")
                return
            }

            val networkerAttributeKey = AttributeKey.valueOf<Networker>("networker")
            val networker = ctx.channel().attr(networkerAttributeKey).get()

            if (!message.isCorrectTarget(networker)) {
                logger.warn("Received a message '${message::class.java}' with the incorrect target side: Expected message targeting ${networker.side} but got ${message.targetSide} instead!")
                return
            }

            message.header = header
            message.side = networker.side

            message.read(input)

            out.add(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}