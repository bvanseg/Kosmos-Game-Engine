package com.kosmos.engine.client.network

import com.kosmos.bootstrapper.event.PluginInitializationEvent
import com.kosmos.engine.client.network.GameClient
import com.kosmos.engine.common.KosmosEngine

fun main() {
    // Initialize engine
    KosmosEngine().init(PluginInitializationEvent())

    // Create client
    val client = GameClient()

    // Connect to server
    client.connect("127.0.0.1", 2323)
}