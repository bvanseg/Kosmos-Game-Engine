package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.attribute.AttributeMap
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.message.ctx.GameContext
import com.kosmos.engine.common.network.util.readUUID
import com.kosmos.engine.common.network.util.writeUUID
import io.netty.buffer.ByteBuf
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class AttributeUpdateMessage(): GameMessage(MessageTarget.CLIENT) {

    lateinit var entityUUID: UUID
    lateinit var attributeMap: AttributeMap

    constructor(entity: Entity) : this() {
        this.entityUUID = entity.uuid
        this.attributeMap = entity.attributeMap
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeUUID(entityUUID)
        logger.debug("Written attribute map: $attributeMap")
        attributeMap.writeModifiedAttributes(buffer)
    }

    lateinit var buf: ByteBuf

    override fun read(buffer: ByteBuf) {
        entityUUID = buffer.readUUID()
        attributeMap = AttributeMap()
        attributeMap.read(buffer)
    }

    override fun handle(ctx: GameContext) {
        val entity = ctx.gameContainer.entities[entityUUID] ?: return // TODO: log warning or exception here
        // The updated attributes override the attributes in the entity's current map.
        logger.debug("Updating entity attribute map: ${entity.attributeMap}")
        entity.attributeMap.merge(attributeMap)
        logger.debug("Finished updating entity attribute map: ${entity.attributeMap}")
    }
}