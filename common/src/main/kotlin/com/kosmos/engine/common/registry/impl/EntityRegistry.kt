package com.kosmos.engine.common.registry.impl

import bvanseg.kotlincommons.reflect.createInstanceFrom
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.registry.FactoryRegistry

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
open class EntityRegistry(private val gameContainer: GameContainer): FactoryRegistry<Entity>(factory = { entry ->
    val instance = createInstanceFrom(entry.value.java)
    if (instance != null) {
        instance.gameContainer = gameContainer
        instance.init()
    }
    instance
})