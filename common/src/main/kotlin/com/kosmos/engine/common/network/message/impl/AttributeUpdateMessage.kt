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

    private var entities = mutableListOf<Entity>()
    private var attributeMaps = hashMapOf<UUID, AttributeMap>()

    constructor(vararg entities: Entity) : this() {
        this.entities.addAll(entities)
    }

    constructor(entities: Collection<Entity>) : this() {
        this.entities.addAll(entities)
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeInt(entities.size)

        entities.forEach { entity ->
            buffer.writeUUID(entity.uuid)
            entity.attributeMap.writeModifiedAttributes(buffer)
        }
    }

    lateinit var buf: ByteBuf

    override fun read(buffer: ByteBuf) {
        val entityCount = buffer.readInt()

        for(i in 0 until entityCount) {
            val uuid = buffer.readUUID()
            val attributeMap = AttributeMap()
            attributeMap.read(buffer)

            attributeMaps[uuid] = attributeMap
        }
    }

    override fun handle(ctx: GameContext) {
        logger.debug("Updating entity attribute maps...")
        val start = System.currentTimeMillis()
        for((entityUUID, attributeMap) in attributeMaps) {
            val entity = ctx.gameContainer.entities[entityUUID] ?: continue // TODO: log warning or exception here
            // The updated attributes override the attributes in the entity's current map.
            entity.attributeMap.merge(attributeMap)
        }
        logger.debug("Finished updating entity attribute maps in ${System.currentTimeMillis() - start}ms")
    }
}