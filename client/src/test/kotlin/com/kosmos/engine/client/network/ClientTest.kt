package com.kosmos.engine.client.network

import com.kosmos.bootstrapper.event.PluginInitializationEvent
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.game.GameContainer

fun main() {
    val engine = KosmosEngine()
    // Initialize engine
    engine.init(PluginInitializationEvent())

    engine.eventBus.addListener(ClientListener)

    // Create client
    val client = GameClient()


    val gameContainer = object: GameContainer(client) {
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

    // Connect to server
    client.connect("127.0.0.1", 2323)
}