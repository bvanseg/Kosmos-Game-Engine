package com.kosmos.engine.common.network.message

import io.netty.buffer.ByteBuf
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
data class MessageHeader(
    val uuid: UUID,
    val messageTypeID: Int,
    val messageID: Long,
    val messageSize: Int
) {

    fun write(buffer: ByteBuf) {
        buffer.writeLong(uuid.mostSignificantBits)
        buffer.writeLong(uuid.leastSignificantBits)
        buffer.writeInt(messageTypeID)
        buffer.writeLong(messageID)
        buffer.writeInt(messageSize)
    }

    companion object {
        fun read(buffer: ByteBuf): MessageHeader = MessageHeader(
            UUID(buffer.readLong(), buffer.readLong()),
            buffer.readInt(),
            buffer.readLong(),
            buffer.readInt()
        )
    }
}