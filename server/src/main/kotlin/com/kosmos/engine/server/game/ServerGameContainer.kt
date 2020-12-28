package com.kosmos.engine.server.game

import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.server.event.listener.ServerGameListener

open class ServerGameContainer(networker: Networker): GameContainer(networker) {

    override fun init() {
        super.init()
        KosmosEngine.getInstance().eventBus.addListener(ServerGameListener(this))
    }

    override fun update() {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }
}