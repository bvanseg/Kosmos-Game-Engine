package com.kosmos.engine.client.event

import com.kosmos.engine.client.network.GameClient
import com.kosmos.engine.common.event.KosmosEngineEvent
import com.kosmos.engine.common.network.message.Message
import io.netty.channel.Channel

open class ClientEvent: KosmosEngineEvent()

open class ClientConnectEvent: ClientEvent() {
    class PRE: ClientConnectEvent()
    class POST(val channel: Channel, val host: String, val port: Int): ClientConnectEvent()
}

open class ClientCloseEvent(val channel: Channel): ClientEvent() {
    class PRE(channel: Channel): ClientCloseEvent(channel)
    class POST(channel: Channel): ClientCloseEvent(channel)
}

open class ClientHandleMessageEvent(val client: GameClient, val message: Message): ClientEvent() {
    class PRE(client: GameClient, message: Message): ClientHandleMessageEvent(client, message)
    class POST(client: GameClient, message: Message): ClientHandleMessageEvent(client, message)
}