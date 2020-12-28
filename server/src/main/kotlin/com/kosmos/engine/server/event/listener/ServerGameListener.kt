package com.kosmos.engine.server.event.listener

import bvanseg.kotlincommons.any.getLogger
import bvanseg.kotlincommons.evenir.annotation.SubscribeEvent
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.server.event.ServerClientConnectEvent
import com.kosmos.engine.server.game.ServerGameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ServerGameListener(private val gameContainer: ServerGameContainer) {

    private val logger = getLogger()

    @SubscribeEvent
    fun onClientConnect(event: ServerClientConnectEvent.POST) {
        // Synchronize all entities with the client.
        logger.info("Synchronizing entities with client ${event.dummyClient.uuid}")
        gameContainer.networker.send(EntityCreateMessage(*gameContainer.entities.values.toTypedArray()))
    }
}