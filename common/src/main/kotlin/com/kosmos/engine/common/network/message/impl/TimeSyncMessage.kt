package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.message.ctx.GameContext
import io.netty.buffer.ByteBuf

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class TimeSyncMessage(): Message<GameContext>(MessageTarget.CLIENT) {

    var serverTicksExisted: Long = 0

    constructor(ticksExisted: Long): this() {
        this.serverTicksExisted = ticksExisted
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(serverTicksExisted)
    }

    override fun read(buffer: ByteBuf) {
        serverTicksExisted = buffer.readLong()
    }

    override fun handle(ctx: GameContext) = Unit
}