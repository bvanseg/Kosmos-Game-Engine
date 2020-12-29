package com.kosmos.engine.client.game

import com.kosmos.engine.client.event.listener.ClientGameListener
import com.kosmos.engine.client.network.GameClient
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.registry.impl.EntityRegistry

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
open class ClientGameContainer(tickRate: Long, override val networker: GameClient): GameContainer(tickRate, networker) {

    override val entityRegistry: EntityRegistry = EntityRegistry(this)

    override fun init() {
        super.init()
        KosmosEngine.getInstance().eventBus.addListener(ClientGameListener(this))
    }

    internal fun setTicksExisted(ticksExisted: Long) {
        this.ticksExisted = ticksExisted
    }

    override fun dispose() {
        networker.close()
    }
}