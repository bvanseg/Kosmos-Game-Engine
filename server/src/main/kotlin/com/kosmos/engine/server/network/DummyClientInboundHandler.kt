package com.kosmos.engine.server.network

import bvanseg.kotlincommons.any.getLogger
import bvanseg.kotlincommons.project.Version
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.server.event.ServerHandleMessageEvent
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.ctx.MessageContext
import com.kosmos.engine.common.network.message.impl.ClientInitMessage
import com.kosmos.engine.server.event.ServerClientConnectEvent
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class DummyClientInboundHandler(private val dummyClientManager: DummyClientManager): SimpleChannelInboundHandler<Message<MessageContext>>() {

    private val logger = getLogger()

    /**
     * Fired when a client connects.
     */
    override fun channelActive(ctx: ChannelHandlerContext) {
        val engine = KosmosEngine.getInstance()

        engine.eventBus.fire(ServerClientConnectEvent.PRE(dummyClientManager, ctx))
        // Set a UUID for the client.
        val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
        ctx.channel().attr(uuidAttributeKey).set(UUID.randomUUID())
        val clientUUID = ctx.channel().attr(uuidAttributeKey).get()

        logger.info("Client connected: $clientUUID")

        // Set the side that the channel knows.
        val sideAttributeKey = AttributeKey.valueOf<Side>("side")
        ctx.channel().attr(sideAttributeKey).set(Side.SERVER)

        // Track the client within our map.
        val dummyClient = DummyClient(ctx.channel(), clientUUID, System.currentTimeMillis())
        dummyClientManager.clients[clientUUID] = dummyClient

        val networkerAttributeKey = AttributeKey.valueOf<Networker>("networker")
        ctx.channel().attr(networkerAttributeKey).set(dummyClient)

        // Initialize the client with the UUID we assign it.
        val clientInitMessage = ClientInitMessage(clientUUID, Version(KosmosEngine.getInstance().pluginInfo.annotationData.version))
        dummyClient.send(clientInitMessage)

        engine.eventBus.fire(ServerClientConnectEvent.POST(dummyClientManager, ctx, dummyClient))
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
        val uuid = ctx.channel().attr(uuidAttributeKey).get()

        logger.info("Client disconnected: $uuid")

        dummyClientManager.clients.remove(uuid)
    }

    /**
     * Fired when a message is received from the client.
     */
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message<MessageContext>) {
        val engine = KosmosEngine.getInstance()

        val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
        val uuid = ctx.channel().attr(uuidAttributeKey).get()

        val dummyClient = dummyClientManager.clients[uuid] ?: return

        engine.eventBus.fire(ServerHandleMessageEvent.PRE(dummyClient, msg))
        msg.handle(dummyClient.contextFactory.invoke())
        engine.eventBus.fire(ServerHandleMessageEvent.POST(dummyClient, msg))

        dummyClient.messagesReceived.getAndIncrement()
    }



    /**
     * Handles exceptions from the client.
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}