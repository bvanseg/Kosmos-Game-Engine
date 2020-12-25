package com.kosmos.engine.client.network

import com.kosmos.engine.client.event.ClientHandleMessageEvent
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import io.netty.channel.ChannelHandlerContext

import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey

class ClientHandler : SimpleChannelInboundHandler<Message>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        val sideAttribute = AttributeKey.valueOf<Side>("side")
        ctx.channel().attr(sideAttribute).set(Side.CLIENT)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message) {
        val engine = KosmosEngine.getInstance()

        engine.eventBus.fire(ClientHandleMessageEvent.PRE(ctx.channel(), msg))
        msg.handle(ctx.channel())
        engine.eventBus.fire(ClientHandleMessageEvent.POST(ctx.channel(), msg))
    }
}