package com.kosmos.engine.server.network

import com.kosmos.bootstrapper.event.PluginInitializationEvent
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.game.GameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */

fun main() {
    val engine = KosmosEngine()
    engine.init(PluginInitializationEvent())

    engine.eventBus.addListener(ServerListener)

    val server = GameServer()

    val gameContainer = object: GameContainer(server) {
        override fun init() {
            TODO("Not yet implemented")
        }

        override fun update() {
            TODO("Not yet implemented")
        }

        override fun dispose() {
            TODO("Not yet implemented")
        }

    }

    server.bind("127.0.0.1", 2323)
}