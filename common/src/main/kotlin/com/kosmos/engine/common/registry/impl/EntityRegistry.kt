package com.kosmos.engine.common.registry.impl

import bvanseg.kotlincommons.javaclass.createNewInstance
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.common.registry.FactoryRegistry

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityRegistry(private val gameContainer: GameContainer): FactoryRegistry<Entity>(factory = { entry ->
    val instance = createNewInstance(entry.value.java)
    if (instance != null) {
        instance.gameContainer = gameContainer
    }
    instance
}) {

    override fun onCreateInstance(instance: Entity?) {
        super.onCreateInstance(instance)

        val networker = gameContainer.networker

        if (networker.side == Side.SERVER && instance != null) {
            gameContainer.entities[instance.uuid] = instance

            val entityMessage = EntityCreateMessage(instance)
            networker.send(entityMessage)
        }
    }
}