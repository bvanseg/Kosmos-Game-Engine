package com.kosmos.engine.server.network

import bvanseg.kotlincommons.any.getLogger
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.server.event.ServerBindEvent
import com.kosmos.engine.server.event.ServerCloseEvent
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.ctx.MessageContext
import com.kosmos.engine.common.network.message.decode.MessageDecoder
import com.kosmos.engine.common.network.message.encode.MessageEncoder
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.common.network.message.impl.PingMessage
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelFuture
import io.netty.util.AttributeKey
import java.net.InetSocketAddress
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class GameServer: Networker(Side.SERVER), AutoCloseable {

    override var uuid: UUID = UUID.randomUUID()
    override lateinit var channel: Channel

    // Accepts connections from clients
    private val bossGroup = NioEventLoopGroup(1)

    // Worker group for actually managing clients.
    private val workerGroup = NioEventLoopGroup()

    private val dummyClientManager = DummyClientManager()

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
                        pipeline.addLast(DummyClientInboundHandler(dummyClientManager))
                    }

                })

            logger.info("Attempting to bind server to $host:$port...")
            engine.eventBus.fire(ServerBindEvent.PRE(this))
            val channelFuture: ChannelFuture = bootstrap.bind(InetSocketAddress(host, port)).sync()
            logger.info("Successfully bound server to $host:$port")

            channel = channelFuture.channel()
            engine.eventBus.fire(ServerBindEvent.POST(this, host, port))

            // Wait until the server socket is closed.
            channelFuture.channel().closeFuture().sync()
        } finally {

            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
            engine.eventBus.fire(ServerCloseEvent.POST(channel))
        }
    }

    override fun send(message: Message<out MessageContext>) = broadcast(message)

    fun sendToClient(message: Message<out MessageContext>, uuid: UUID) {
        val dummyClient = dummyClientManager.clients[uuid] ?: return
        dummyClient.send(message)
    }

    fun sendToClient(message: Message<out MessageContext>, dummyClient: DummyClient) {
        dummyClient.send(message)
    }

    fun sendToClient(message: Message<out MessageContext>, channel: Channel) {
        val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
        val uuid = channel.attr(uuidAttributeKey).get()
        dummyClientManager.clients[uuid]?.send(message)
    }

    fun sendToClients(message: Message<out MessageContext>, vararg uuids: UUID) {
        uuids.forEach { uuid ->
            sendToClient(message, uuid)
        }
    }

    fun sendToClients(message: Message<out MessageContext>, vararg dummyClients: DummyClient) {
        dummyClients.forEach { dummyClient ->
            dummyClient.send(message)
        }
    }

    fun sendToClients(message: Message<out MessageContext>, vararg channels: Channel) {
        channels.forEach { channel ->
            sendToClient(message, channel)
        }
    }

    fun broadcast(message: Message<out MessageContext>) {
        dummyClientManager.clients.forEach { (_, client) ->
            client.send(message)
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