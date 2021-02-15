package com.kosmos.engine.server.network

import bvanseg.kotlincommons.util.command.CommandManager
import bvanseg.kotlincommons.util.event.SubscribeEvent
import com.kosmos.engine.common.entity.EntityZealot
import com.kosmos.engine.common.event.RegisterEntitiesEvent
import com.kosmos.engine.common.network.message.impl.PingMessage
import com.kosmos.engine.server.event.ServerBindEvent
import com.kosmos.engine.server.event.ServerClientConnectEvent
import com.kosmos.engine.server.game.ServerGameContainer
import com.kosmos.engine.server.network.commands.EntityGear
import com.kosmos.engine.server.network.commands.ServerGear
import java.util.Scanner

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

        val commandManager = CommandManager<Long>()
        commandManager.addGear(EntityGear(gameContainer))
        commandManager.addGear(ServerGear(gameContainer))

        val scanner = Scanner(System.`in`)

        var firstEntity: EntityZealot? = null
        while(scanner.hasNext()) {
            val input = scanner.nextLine()
            commandManager.execute(input)

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
                    val dummy = gameContainer.createEntity<EntityZealot>()

                    if(firstEntity == null) {
                        firstEntity = dummy
                    }
                }
                "level" -> {
                    println("upgrading dummy entity...")
                    val health = firstEntity?.health
                    health?.set(health.get() + 10)
                }
            }
        }
    }
}