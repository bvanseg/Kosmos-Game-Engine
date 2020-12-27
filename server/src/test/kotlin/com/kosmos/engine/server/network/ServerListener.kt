package com.kosmos.engine.server.network

import bvanseg.kotlincommons.evenir.annotation.SubscribeEvent
import com.kosmos.engine.common.entity.EntityDummy
import com.kosmos.engine.common.event.RegisterEntitiesEvent
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.server.event.ServerBindEvent
import com.kosmos.engine.server.event.ServerClientConnectEvent

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
object ServerListener {

    @SubscribeEvent
    fun onEntitiesRegister(event: RegisterEntitiesEvent) {
        event.entityRegistry.register(EntityDummy::class)
    }

    @SubscribeEvent
    fun onClientConnect(event: ServerClientConnectEvent.POST) {
        val dummy = EntityDummy()
        val entityCreateMessage = EntityCreateMessage(dummy)
        dummy.attributeMap.getAttribute<Int>("health")?.set(110)
        event.dummyClient.send(entityCreateMessage)
    }

    @SubscribeEvent
    fun onServerBind(event: ServerBindEvent.POST) {
        println("Received server bind event!")
        //exitProcess(0)
    }
}