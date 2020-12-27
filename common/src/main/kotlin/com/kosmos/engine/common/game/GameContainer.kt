package com.kosmos.engine.common.game

import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.event.RegisterEntitiesEvent
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.ctx.GameContext
import com.kosmos.engine.common.registry.impl.EntityRegistry
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class GameContainer(val networker: Networker) {

    /**
     * The [Side] of the [GameContainer], whether it is Client or Server.
     */
    private val localSide = InheritableThreadLocal<Side>()

    val entityRegistry = EntityRegistry(this)

    val entities = ConcurrentHashMap<UUID, Entity>()

    fun getSide(): Side = localSide.get()

    inline fun <reified T: Entity> createEntity(): T =
        (entityRegistry.getEntry(T::class)?.createInstance() ?: throw RuntimeException("I dunno man")) as T

    open fun init() {
        // Set the side of the game container
        localSide.set(networker.side)

        val engine = KosmosEngine.getInstance()

        // Append
        engine.registryManager.addFactoryRegistry(entityRegistry)

        engine.eventBus.fire(RegisterEntitiesEvent(entityRegistry))

        networker.contextFactory = {
            GameContext(this)
        }
    }

    abstract fun update()
    abstract fun dispose()
}