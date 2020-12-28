package com.kosmos.engine.common.entity

import com.kosmos.engine.common.attribute.AttributeMap
import com.kosmos.engine.common.game.GameContainer
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

    protected var ticksExisted: Long = 0

    private var isDead: Boolean = false

    fun isDead() = isDead

    fun setDead() {
        isDead = true
    }

    internal lateinit var gameContainer: GameContainer

    internal val attributeMap by lazy { AttributeMap(this) }

    protected open fun initAttributes(attributeMap: AttributeMap) = Unit

    fun hasModifiedAttributes(): Boolean = attributeMap.hasModifiedAttributes()

    fun init() {
        initAttributes(attributeMap)
        attributeMap.clearModifiedAttributes()
        attributeMap.getAllAttributes().forEach {
            it.isDefault = true
        }
    }

    fun write(buffer: ByteBuf) {
        buffer.writeUUID(uuid)
        attributeMap.write(buffer)
    }

    fun read(buffer: ByteBuf) {
        uuid = buffer.readUUID()
        attributeMap.read(buffer)
    }

    open fun update() {
        this.ticksExisted++
    }
}