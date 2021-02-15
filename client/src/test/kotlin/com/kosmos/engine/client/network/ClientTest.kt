package com.kosmos.engine.client.network

import com.kosmos.bootstrapper.event.PluginInitializationEvent
import com.kosmos.engine.client.game.ClientGameContainer
import com.kosmos.engine.common.KosmosEngine

fun main() {
    val engine = KosmosEngine()
    // Initialize engine
    engine.init(PluginInitializationEvent())

    engine.eventBus.addListener(ClientListener)

    // Create client
    val client = GameClient()

    val gameContainer = ClientGameContainer(20L, client)

    gameContainer.init()

    // Connect to server
    client.connect("127.0.0.1", 2323)
}