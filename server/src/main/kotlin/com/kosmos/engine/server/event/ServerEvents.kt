package com.kosmos.engine.server.event

import com.kosmos.engine.common.event.KosmosEngineEvent
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.server.network.DummyClient
import com.kosmos.engine.server.network.DummyClientManager
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

open class ServerEvent: KosmosEngineEvent()

open class ServerBindEvent: ServerEvent() {
    class PRE: ServerBindEvent()
    class POST(val channel: Channel, val host: String, val port: Int): ServerBindEvent()
}

open class ServerClientConnectEvent(val dummyClientManager: DummyClientManager): ServerEvent() {
    class PRE(dummyClientManager: DummyClientManager, val ctx: ChannelHandlerContext): ServerClientConnectEvent(dummyClientManager)
    class POST(dummyClientManager: DummyClientManager, val ctx: ChannelHandlerContext, val dummyClient: DummyClient): ServerClientConnectEvent(dummyClientManager)
}

open class ServerCloseEvent(val channel: Channel): ServerEvent() {
    class PRE(channel: Channel): ServerCloseEvent(channel)
    class POST(channel: Channel): ServerCloseEvent(channel)
}

open class ServerHandleMessageEvent(val dummyClient: DummyClient, val message: Message): ServerEvent() {
    class PRE(dummyClient: DummyClient, message: Message): ServerHandleMessageEvent(dummyClient, message)
    class POST(dummyClient: DummyClient, message: Message): ServerHandleMessageEvent(dummyClient, message)
}