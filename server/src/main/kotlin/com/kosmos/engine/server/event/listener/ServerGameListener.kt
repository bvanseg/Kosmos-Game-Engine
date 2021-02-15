package com.kosmos.engine.server.event.listener

import bvanseg.kotlincommons.io.logging.getLogger
import bvanseg.kotlincommons.util.event.SubscribeEvent
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.common.network.message.impl.TimeSyncMessage
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
        if (gameContainer.entities.isNotEmpty()) {
            logger.info("Synchronizing entities with client ${event.dummyClient.uuid}")
            gameContainer.networker.send(EntityCreateMessage(*gameContainer.entities.values.toTypedArray()))
        }

        gameContainer.networker.send(TimeSyncMessage(gameContainer.ticksExisted))
    }
}