package com.kosmos.engine.server.network

import bvanseg.kotlincommons.evenir.annotation.SubscribeEvent
import com.kosmos.engine.common.entity.EntityDummy
import com.kosmos.engine.common.event.RegisterEntitiesEvent
import com.kosmos.engine.common.game.GameContainer
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.common.network.message.impl.PingMessage
import com.kosmos.engine.server.event.ServerBindEvent
import com.kosmos.engine.server.event.ServerClientConnectEvent
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ServerListener(val gameContainer: GameContainer) {

    @SubscribeEvent
    fun onEntitiesRegister(event: RegisterEntitiesEvent) {
        event.entityRegistry.register(EntityDummy::class)
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

        val scanner = Scanner(System.`in`)

        var firstEntity: EntityDummy? = null
        while(scanner.hasNext()) {
            val input = scanner.nextLine()

            when(input) {
                "exit" -> {
                    println("exiting input loop...")
                    break
                }
                "ping" -> {
                    println("pinging...")
                    event.gameServer.broadcast(PingMessage())
                }
                "entity" -> {
                    println("sending dummy entity...")
                    val dummy = gameContainer.createEntity<EntityDummy>()

                    if(firstEntity == null) {
                        firstEntity = dummy
                    }

                    val entityCreateMessage = EntityCreateMessage(dummy)
                    event.gameServer.broadcast(entityCreateMessage)
                }
                "level" -> {
                    println("upgrading dummy entity...")
                    val health = firstEntity?.attributeMap?.getAttribute<Int>("health")
                    health?.set(health.get() + 10)
                }
                "update" -> {
                    println("updating dummy entity...")
                    firstEntity?.update()
                }
            }
        }
    }
}