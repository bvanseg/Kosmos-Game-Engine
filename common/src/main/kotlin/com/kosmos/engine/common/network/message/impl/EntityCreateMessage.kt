package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.message.ctx.GameContext
import io.netty.buffer.ByteBuf

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityCreateMessage(): GameMessage(MessageTarget.CLIENT) {

    var entities: MutableList<Entity> = mutableListOf()

    constructor(vararg entities: Entity): this() {
        this.entities.addAll(entities)
    }

    constructor(entities: Collection<Entity>): this() {
        this.entities.addAll(entities)
    }

    override fun write(buffer: ByteBuf) {
        val registry = KosmosEngine.getInstance().registryManager.getFactoryRegistry<Entity>()

        buffer.writeInt(entities.size)
        for(entity in entities) {
            val id = registry.getIDFor(entity)
            buffer.writeInt(id)
            entity.write(buffer)
        }
    }

    override fun read(buffer: ByteBuf) {
        val registry = KosmosEngine.getInstance().registryManager.getFactoryRegistry<Entity>()

        val entityCount = buffer.readInt()

        for(i in 0 until entityCount) {
            val entityID = buffer.readInt()

            val entityFactoryEntry = registry.getEntryByID(entityID)

            if(entityFactoryEntry == null) {
                logger.warn("Failed to find an entity factory entry with entity ID $entityID")
                return
            }

            val createdEntity = entityFactoryEntry.createInstance()

            if(createdEntity == null) {
                logger.warn("Failed to create an entity from the given factory entry with entity ID $entityID")
                return
            }

            createdEntity.read(buffer)

            entities.add(createdEntity)
        }

    }

    override fun handle(ctx: GameContext) {
        for(entity in entities) {
            ctx.gameContainer.entities[entity.uuid] = entity
        }
    }
}