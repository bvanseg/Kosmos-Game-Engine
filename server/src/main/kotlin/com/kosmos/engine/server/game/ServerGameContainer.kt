package com.kosmos.engine.server.game

import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.network.message.impl.AttributeUpdateMessage
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.common.network.message.impl.EntityDestroyMessage
import com.kosmos.engine.common.registry.impl.EntityRegistry
import com.kosmos.engine.server.event.listener.ServerGameListener
import com.kosmos.engine.server.network.GameServer
import com.kosmos.engine.server.registry.impl.ServerEntityRegistry

open class ServerGameContainer(override val networker: GameServer): GameContainer(networker) {

    override val entityRegistry: EntityRegistry = ServerEntityRegistry(this)

    // TODO: Make sure this is thread-safe.
    private val queuedEntities = mutableListOf<Entity>()

    // TODO: Make sure this is thread-safe.
    internal fun queueCreatedEntity(entity: Entity) = queuedEntities.add(entity)

    override fun init() {
        super.init()
        KosmosEngine.getInstance().eventBus.addListener(ServerGameListener(this))
    }

    override fun update() {
        val entitiesToSyncAttribs = mutableListOf<Entity>()

        if (queuedEntities.isNotEmpty()) {
            networker.send(EntityCreateMessage(queuedEntities))
            queuedEntities.clear()
        }


        val entitiesToDestroy = mutableListOf<Entity>()
        entities.forEach { (_, entity) ->
            entity.update()

            if(entity.isDead()) {
                entitiesToDestroy.add(entity)
            } else if(entity.hasModifiedAttributes()) {
                entitiesToSyncAttribs.add(entity)
            }
        }

        if(entitiesToDestroy.isNotEmpty()) {
            networker.send(EntityDestroyMessage(entitiesToDestroy))
            entitiesToDestroy.clear()
        }

        if(entitiesToSyncAttribs.isNotEmpty()) {
            networker.send(AttributeUpdateMessage(entitiesToSyncAttribs))
            entitiesToSyncAttribs.clear()
        }
    }

    override fun dispose() {
        networker.close()
    }
}