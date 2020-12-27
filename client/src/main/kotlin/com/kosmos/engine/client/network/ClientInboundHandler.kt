package com.kosmos.engine.client.network

import com.kosmos.engine.client.event.ClientHandleMessageEvent
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.ctx.MessageContext
import io.netty.channel.ChannelHandlerContext

import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey

class ClientInboundHandler(private val gameClient: GameClient) : SimpleChannelInboundHandler<Message<MessageContext>>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        val sideAttributeKey = AttributeKey.valueOf<Side>("side")
        ctx.channel().attr(sideAttributeKey).set(Side.CLIENT)

        val networkerAttributeKey = AttributeKey.valueOf<Networker>("networker")
        ctx.channel().attr(networkerAttributeKey).set(gameClient)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message<MessageContext>) {
        val engine = KosmosEngine.getInstance()

        engine.eventBus.fire(ClientHandleMessageEvent.PRE(gameClient, msg))
        msg.handle(gameClient.contextFactory.invoke())
        engine.eventBus.fire(ClientHandleMessageEvent.POST(gameClient, msg))

        gameClient.messagesReceived.getAndIncrement()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
    }
}