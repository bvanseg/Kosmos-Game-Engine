package com.kosmos.engine.common.game

import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.entity.EntityDummy
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.registry.impl.EntityRegistry

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class GameContainer(val networker: Networker) {

    /**
     * The [Side] of the [GameContainer], whether it is Client or Server.
     */
    private val localSide = InheritableThreadLocal<Side>()

    protected val entityRegistry = EntityRegistry(networker)

    init {
        // Set the side of the game container
        localSide.set(networker.side)

        // Append
        KosmosEngine.getInstance().registryManager.addFactoryRegistry(entityRegistry)

        entityRegistry.register(EntityDummy::class)
    }

    fun getSide(): Side = localSide.get()

    abstract fun init()
    abstract fun update()
    abstract fun dispose()
}