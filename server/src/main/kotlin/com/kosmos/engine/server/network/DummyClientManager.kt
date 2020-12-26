package com.kosmos.engine.server.network

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DummyClientManager {
    val clients = ConcurrentHashMap<UUID, DummyClient>()
}