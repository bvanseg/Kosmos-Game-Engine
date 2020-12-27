package com.kosmos.engine.common.network.message

import bvanseg.kotlincommons.any.getLogger
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.ctx.MessageContext
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.util.AttributeKey

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class Message<T: MessageContext>(val targetSide: MessageTarget) {

    lateinit var header: MessageHeader
    lateinit var side: Side

    val logger = getLogger()

    abstract fun read(buffer: ByteBuf)
    abstract fun write(buffer: ByteBuf)
    abstract fun handle(ctx: T)

    fun isCorrectTarget(side: Side): Boolean {
        if (targetSide == MessageTarget.COMMON)
            return true
        if (side == Side.CLIENT && targetSide == MessageTarget.CLIENT)
            return true
        if (side == Side.SERVER && targetSide == MessageTarget.SERVER)
            return true

        return false
    }

    fun isCorrectTarget(networker: Networker): Boolean {
        val side = networker.side
        return isCorrectTarget(side)
    }
}