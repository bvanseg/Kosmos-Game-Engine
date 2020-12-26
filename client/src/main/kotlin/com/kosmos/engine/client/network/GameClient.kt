package com.kosmos.engine.client.network

import bvanseg.kotlincommons.any.getLogger
import com.kosmos.engine.client.event.ClientCloseEvent
import com.kosmos.engine.client.event.ClientConnectEvent
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.decode.MessageDecoder
import com.kosmos.engine.common.network.message.encode.MessageEncoder
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class GameClient: Networker(), AutoCloseable {

    override lateinit var channel: Channel

    override lateinit var uuid: UUID

    private val group = NioEventLoopGroup()

    private val logger = getLogger()

    private val clientHandler = ClientInboundHandler(this)

    val messagesSent: AtomicLong = AtomicLong(0L)
    val messagesReceived: AtomicLong = AtomicLong(0L)

    var timestampConnected: Long = 0
        private set

    fun connect(host: String, port: Int) {

        val engine = KosmosEngine.getInstance()

        try {
            val bootstrap = Bootstrap()

            // Set groups for server bootstrap.
            bootstrap.group(group)
                .channel(NioSocketChannel::class.java)
                .handler(object: ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        val pipeline = channel.pipeline()
                        pipeline.addLast(MessageDecoder())
                        pipeline.addLast(MessageEncoder())
                        pipeline.addLast(clientHandler)
                    }

                })

            logger.info("Attempting to connect client to $host:$port...")
            engine.eventBus.fire(ClientConnectEvent.PRE())
            val channelFuture: ChannelFuture = bootstrap.connect(InetSocketAddress(host, port)).sync()
            timestampConnected = System.currentTimeMillis()
            logger.info("Client successfully connected to $host:$port")

            channel = channelFuture.channel()
            engine.eventBus.fire(ClientConnectEvent.POST(channel, host, port))

            // Wait until the server socket is closed.
            channelFuture.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully()
            engine.eventBus.fire(ClientCloseEvent.POST(channel))
        }
    }

    override fun send(message: Message) {
        channel.writeAndFlush(message)
        messagesSent.getAndIncrement()
    }

    override fun close() {
        val engine = KosmosEngine.getInstance()

        engine.eventBus.fire(ClientCloseEvent.PRE(channel))
        channel.close()
        group.shutdownGracefully()
        engine.eventBus.fire(ClientCloseEvent.POST(channel))
    }
}