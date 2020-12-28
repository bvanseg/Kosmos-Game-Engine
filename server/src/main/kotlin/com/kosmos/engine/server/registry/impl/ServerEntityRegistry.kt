package com.kosmos.engine.server.registry.impl

import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.registry.impl.EntityRegistry
import com.kosmos.engine.server.game.ServerGameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ServerEntityRegistry(private val serverGameContainer: ServerGameContainer): EntityRegistry(serverGameContainer) {

    override fun onCreateInstance(instance: Entity?) {
        super.onCreateInstance(instance)
        if (instance != null) {
            serverGameContainer.entities[instance.uuid] = instance
            serverGameContainer.queueCreatedEntity(instance)
        }
    }
}