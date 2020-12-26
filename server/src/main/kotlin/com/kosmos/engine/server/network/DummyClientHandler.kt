package com.kosmos.engine.server.network

import bvanseg.kotlincommons.any.getLogger
import bvanseg.kotlincommons.project.Version
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.server.event.ServerHandleMessageEvent
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.impl.ClientInitMessage
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class DummyClientHandler(val dummyClientManager: DummyClientManager): SimpleChannelInboundHandler<Message>() {

    private val logger = getLogger()

    /**
     * Fired when a client connects.
     */
    override fun channelActive(ctx: ChannelHandlerContext) {

        // Set a UUID for the client.
        val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
        ctx.channel().attr(uuidAttributeKey).set(UUID.randomUUID())
        val clientUUID = ctx.channel().attr(uuidAttributeKey).get()

        logger.info("Client connected: $clientUUID")

        // Set the side that the channel knows.
        val sideAttributeKey = AttributeKey.valueOf<Side>("side")
        ctx.channel().attr(sideAttributeKey).set(Side.SERVER)

        // Track the client within our map.
        val dummyClient = DummyClient(ctx.channel())
        dummyClientManager.clients[clientUUID] = dummyClient

        // Initialize the client with the UUID we assign it.
        val clientInitMessage = ClientInitMessage(clientUUID, Version(KosmosEngine.getInstance().pluginInfo.annotationData.version))
        ctx.writeAndFlush(clientInitMessage)
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
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message) {
        val engine = KosmosEngine.getInstance()

        engine.eventBus.fire(ServerHandleMessageEvent.PRE(ctx.channel(), msg))
        msg.handle(ctx.channel())
        engine.eventBus.fire(ServerHandleMessageEvent.POST(ctx.channel(), msg))
    }

    /**
     * Handles exceptions from the client.
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}