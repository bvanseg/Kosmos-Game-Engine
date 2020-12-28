package com.kosmos.engine.client.game

import com.kosmos.engine.client.event.listener.ClientGameListener
import com.kosmos.engine.client.network.GameClient
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.registry.impl.EntityRegistry

open class ClientGameContainer(override val networker: GameClient): GameContainer(networker) {

    override val entityRegistry: EntityRegistry = EntityRegistry(this)

    override fun init() {
        super.init()
        KosmosEngine.getInstance().eventBus.addListener(ClientGameListener(this))
    }

    override fun update() {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }
}