package com.kosmos.engine.server.network

import bvanseg.kotlincommons.util.event.SubscribeEvent
import com.kosmos.engine.common.entity.EntityZealot
import com.kosmos.engine.common.event.RegisterEntitiesEvent
import com.kosmos.engine.server.event.ServerBindEvent
import com.kosmos.engine.server.event.ServerClientConnectEvent
import com.kosmos.engine.server.game.ServerGameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ServerListener(val gameContainer: ServerGameContainer) {

    @SubscribeEvent
    fun onEntitiesRegister(event: RegisterEntitiesEvent) {
        event.entityRegistry.register(EntityZealot::class)
    }

    @SubscribeEvent
    fun onClientConnect(event: ServerClientConnectEvent.POST) {
//        val dummy = EntityDummy()
//        val entityCreateMessage = EntityCreateMessage(dummy)
//        dummy.attributeMap.getAttribute<Int>("health")?.set(110)
//        event.dummyClient.send(entityCreateMessage)
    }

    @SubscribeEvent
    fun onServerBind(event: ServerBindEvent.POST) {
        println("Received server bind event!")
    }
}