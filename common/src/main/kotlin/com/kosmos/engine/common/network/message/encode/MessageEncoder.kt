package com.kosmos.engine.common.network.message.encode

import bvanseg.kotlincommons.any.getLogger
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageHeader
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.util.AttributeKey
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class MessageEncoder: MessageToByteEncoder<Message>() {

    val logger = getLogger()

    override fun encode(ctx: ChannelHandlerContext, message: Message, out: ByteBuf) {
        try {

            val networkerAttributeKey = AttributeKey.valueOf<Networker>("networker")
            val networker = ctx.channel().attr(networkerAttributeKey).get()

            // Get the opposite side of this channel. If this channel is the client, this returns the server, for ex.
            val oppositeSide = networker.side.opposite()

            /*
                If:
                    - We are client side
                    - This message targets the server

                This check makes sure that the Message#messageTarget and opposite of our side are the same. (We can't
                target clients as a client or servers as a server).
             */
            if (!message.isCorrectTarget(oppositeSide)) {
                logger.warn("Received a message '${message::class.java}' with the incorrect target side: Expected message targeting $oppositeSide but got ${message.targetSide} instead!")
                return
            }

            val engine = KosmosEngine.getInstance()

            val messageID = engine.messageRegistry.getIDFor(message)

            val sampleBuf = Unpooled.buffer()

            message.side = networker.side

            message.write(sampleBuf)

            val size = sampleBuf.capacity()

            val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
            val uuid = ctx.channel().attr(uuidAttributeKey).get()

            val header = MessageHeader(uuid, messageID, networker.messagesSent.get(), size)

            logger.debug("Writing message with header info $header")
            header.write(out)
            out.writeBytes(sampleBuf)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}