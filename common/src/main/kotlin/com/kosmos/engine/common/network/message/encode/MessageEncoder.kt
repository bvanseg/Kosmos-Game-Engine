package com.kosmos.engine.common.network.message.encode

import com.kosmos.engine.common.KosmosEngine
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

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {
        try {
            val engine = KosmosEngine.getInstance()

            val messageID = engine.messageRegistry.getIDFor(msg)

            val sampleBuf = Unpooled.buffer()

            try {
                msg.write(sampleBuf)
            } catch (e: Exception) {
                //e.printStackTrace()
            }

            val size = sampleBuf.capacity()

            val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
            val uuid = ctx.channel().attr(uuidAttributeKey).get()

            val header = MessageHeader(uuid, messageID, size)
            header.write(out)
            msg.write(out)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}