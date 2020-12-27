package com.kosmos.engine.common.entity

import com.kosmos.engine.common.attribute.AttributeMap
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.network.message.impl.AttributeUpdateMessage
import com.kosmos.engine.common.network.util.readUUID
import com.kosmos.engine.common.network.util.writeUUID
import io.netty.buffer.ByteBuf
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class Entity {

    var uuid = UUID.randomUUID()
        private set

    val attributeMap by lazy { AttributeMap(this) }

    internal lateinit var gameContainer: GameContainer

    fun write(buffer: ByteBuf) {
        buffer.writeUUID(uuid)
        attributeMap.write(buffer)
    }

    fun read(buffer: ByteBuf) {
        uuid = buffer.readUUID()
        attributeMap.read(buffer)
    }

    open fun update() {
        if (attributeMap.hasModifiedAttributes()) {
            gameContainer.networker.send(AttributeUpdateMessage(this))
        }
    }
}