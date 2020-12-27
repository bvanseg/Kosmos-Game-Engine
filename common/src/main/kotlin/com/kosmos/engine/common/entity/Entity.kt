package com.kosmos.engine.common.entity

import com.kosmos.engine.common.attribute.AttributeMap
import io.netty.buffer.ByteBuf

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class Entity {
    val attributeMap = AttributeMap()

    fun write(buffer: ByteBuf) {
        attributeMap.write(buffer)
    }

    fun read(buffer: ByteBuf) {
        attributeMap.read(buffer)
    }
}