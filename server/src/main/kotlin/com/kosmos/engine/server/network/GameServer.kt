package com.kosmos.engine.server.network

import bvanseg.kotlincommons.any.getLogger
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.server.event.ServerBindEvent
import com.kosmos.engine.server.event.ServerCloseEvent
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.decode.MessageDecoder
import com.kosmos.engine.common.network.message.encode.MessageEncoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelFuture
import java.net.InetSocketAddress
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class GameServer: AutoCloseable {

    private lateinit var channel: Channel

    // Accepts connections from clients
    private val bossGroup = NioEventLoopGroup(1)

    // Worker group for actually managing clients.
    private val workerGroup = NioEventLoopGroup()

    private val multiClientHandler = MultiClientHandler()

    private val logger = getLogger()


    fun bind(host: String, port: Int) {
        val engine = KosmosEngine.getInstance()

        try {
            val bootstrap = ServerBootstrap()

            // Set groups for server bootstrap.
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object: ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        val pipeline = channel.pipeline()
                        pipeline.addLast(MessageDecoder())
                        pipeline.addLast(MessageEncoder())
                        pipeline.addLast(multiClientHandler)
                    }

                })

            logger.info("Attempting to bind server to $host:$port...")
            engine.eventBus.fire(ServerBindEvent.PRE())
            val channelFuture: ChannelFuture = bootstrap.bind(InetSocketAddress(host, port)).sync()
            logger.info("Successfully bound server to $host:$port")

            channel = channelFuture.channel()
            engine.eventBus.fire(ServerBindEvent.POST(channel, host, port))

            // Wait until the server socket is closed.
            channelFuture.channel().closeFuture().sync()
        } finally {

            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
            engine.eventBus.fire(ServerCloseEvent.POST(channel))
        }
    }

    fun sendToClient(message: Message, uuid: UUID) {
        val client = multiClientHandler.clients[uuid] ?: return
        client.channel.writeAndFlush(message)
    }

    fun sendToClient(message: Message, dummyClient: DummyClient) {
        dummyClient.channel.writeAndFlush(message)
    }

    fun sendToClient(message: Message, channel: Channel) {
        channel.writeAndFlush(message)
    }

    fun sendToClients(message: Message, vararg uuids: UUID) {
        uuids.forEach { uuid ->
            sendToClient(message, uuid)
        }
    }

    fun sendToClients(message: Message, vararg dummyClients: DummyClient) {
        dummyClients.forEach { dummyClient ->
            sendToClient(message, dummyClient)
        }
    }

    fun sendToClients(message: Message, vararg channels: Channel) {
        channels.forEach { channel ->
            sendToClient(message, channel)
        }
    }

    fun broadcast(message: Message) {
        multiClientHandler.clients.forEach { (_, client) ->
            client.channel.writeAndFlush(message)
        }
    }

    override fun close() {
        val engine = KosmosEngine.getInstance()

        engine.eventBus.fire(ServerCloseEvent.PRE(channel))
        channel.close()
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
        engine.eventBus.fire(ServerCloseEvent.POST(channel))
    }
}