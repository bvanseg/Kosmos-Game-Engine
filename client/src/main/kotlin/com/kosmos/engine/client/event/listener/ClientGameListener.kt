package com.kosmos.engine.client.event.listener

import bvanseg.kotlincommons.io.logging.getLogger
import bvanseg.kotlincommons.util.event.SubscribeEvent
import com.kosmos.engine.client.event.ClientHandleMessageEvent
import com.kosmos.engine.client.game.ClientGameContainer
import com.kosmos.engine.common.network.message.impl.TimeSyncMessage

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ClientGameListener(private val clientGameContainer: ClientGameContainer) {

    private val logger = getLogger()

    @SubscribeEvent
    fun onMessageHandled(event: ClientHandleMessageEvent.POST) {
        if (event.message is TimeSyncMessage) {
            logger.info("Synchronizing client ticks with server ticks...")
            clientGameContainer.setTicksExisted(event.message.serverTicksExisted)
        }
    }
}