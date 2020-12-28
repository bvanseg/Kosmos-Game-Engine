package com.kosmos.engine.client.game

import com.kosmos.engine.client.event.listener.ClientGameListener
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.network.Networker

open class ClientGameContainer(networker: Networker): GameContainer(networker) {

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