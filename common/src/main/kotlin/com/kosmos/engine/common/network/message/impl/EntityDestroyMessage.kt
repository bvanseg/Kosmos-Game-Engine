package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.message.ctx.GameContext
import com.kosmos.engine.common.network.util.readUUID
import com.kosmos.engine.common.network.util.writeUUID
import io.netty.buffer.ByteBuf
import java.util.UUID

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityDestroyMessage(): GameMessage(MessageTarget.CLIENT) {

    var entityUUIDs: MutableList<UUID> = mutableListOf()

    constructor(vararg entities: Entity): this() {
        this.entityUUIDs.addAll(entities.map { it.uuid })
    }

    constructor(entities: Collection<Entity>): this() {
        this.entityUUIDs.addAll(entities.map { it.uuid })
    }

    constructor(vararg uuids: UUID): this() {
        this.entityUUIDs.addAll(uuids)
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeInt(entityUUIDs.size)
        for(entityUUID in entityUUIDs) {
            buffer.writeUUID(entityUUID)
        }
    }

    override fun read(buffer: ByteBuf) {
        val size = buffer.readInt()

        for(i in 0 until size) {
            entityUUIDs.add(buffer.readUUID())
        }
    }

    override fun handle(ctx: GameContext) {
        for(entityUUID in entityUUIDs) {
            val entity = ctx.gameContainer.entities.remove(entityUUID) ?: continue
            entity.setDead()
        }
    }
}