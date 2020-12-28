package com.kosmos.engine.server.network

import com.kosmos.bootstrapper.event.PluginInitializationEvent
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.server.game.ServerGameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */

fun main() {
    val engine = KosmosEngine()
    engine.init(PluginInitializationEvent())

    val server = GameServer()

    val gameContainer = ServerGameContainer(server)

    engine.eventBus.addListener(ServerListener(gameContainer))

    gameContainer.init()

    server.bind("127.0.0.1", 2323)
}