package com.kosmos.engine.common.network.message.impl

import bvanseg.kotlincommons.project.Version
import com.kosmos.bootstrapper.plugin.PluginManager
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.util.readUUID
import com.kosmos.engine.common.network.util.readVersion
import com.kosmos.engine.common.network.util.writeUUID
import com.kosmos.engine.common.network.util.writeVersion
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import java.util.*

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ClientInitMessage: Message() {

    // The client needs to know their own UUID.
    lateinit var uuid: UUID

    // The client needs to know what version of the engine the server is running.
    lateinit var version: Version

    override fun write(buffer: ByteBuf) {
        buffer.writeUUID(uuid)
        buffer.writeVersion(version)
    }

    override fun read(buffer: ByteBuf) {
        uuid = buffer.readUUID()
        version = buffer.readVersion()
    }

    override fun handle(channel: Channel) {
        getSide(channel)?.let { side ->
            if (side == Side.CLIENT) {
                val uuidAttributeKey = AttributeKey.valueOf<UUID>("uuid")
                channel.attr(uuidAttributeKey).set(uuid)
                logger.info("Client UUID established by server: $uuid")
            }
        }

        KosmosEngine.getInstance().pluginInfo.let {
            val clientVersion = Version(it.annotationData.version)

            if (clientVersion != version) {
                logger.warn("Server engine is running version '$version' but the client engine is running version '$clientVersion'. Unexpected behavior might occur!")
            } else {
                logger.info("Client and server are running matching engine versions ($version).")
            }
        }
    }
}