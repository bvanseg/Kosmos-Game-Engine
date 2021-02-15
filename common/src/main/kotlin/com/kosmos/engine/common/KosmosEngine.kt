package com.kosmos.engine.common

import bvanseg.kotlincommons.io.logging.getLogger
import bvanseg.kotlincommons.util.event.EventBus
import com.kosmos.bootstrapper.event.PluginInitializationEvent
import com.kosmos.bootstrapper.plugin.Plugin
import com.kosmos.bootstrapper.plugin.PluginContainer
import com.kosmos.bootstrapper.plugin.PluginManager
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.ctx.MessageContext
import com.kosmos.engine.common.network.message.impl.*
import com.kosmos.engine.common.network.util.*
import com.kosmos.engine.common.registry.RegistryManager
import io.netty.buffer.ByteBuf
import org.joml.*
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * @author Boston Vanseghi
 * @author Ocelot5836
 * @since 1.0.0
 */
@Plugin(
    version = "1.0.0",
    name = "Kosmos Engine",
    domain = "kosmos_engine"
)
class KosmosEngine {

    init {
        PluginManager.EVENT_BUS.addListener(this)
    }

    companion object {
        private lateinit var instance: KosmosEngine

        fun getInstance() = instance
    }

    val pluginInfo: PluginContainer by lazy {
        val annotationData = this::class.java.getAnnotation(Plugin::class.java)

        // Return the plugin manager's container, otherwise create our own.
        return@lazy PluginManager.getPlugin("kosmos_engine") ?:
        PluginContainer(this, this::class.java, annotationData, mutableListOf(), null)
    }

    /**
     * Primary event bus for the game engine.
     */
    val eventBus = EventBus()

    /**
     * Primary logger for the game engine.
     */
    private val logger = getLogger()

    val registryManager = RegistryManager()
    val messageRegistry = registryManager.addFactoryRegistry<Message<out MessageContext>>()
    val networkReadWriteRegistry = registryManager.addInstanceRegistry<KClass<*>, Pair<(ByteBuf) -> Any, (Any, ByteBuf) -> Unit>>()

    fun init(event: PluginInitializationEvent) {
        logger.info("Initializing Kosmos Engine...")
        val start = System.currentTimeMillis()
        instance = this // Allows plugins dependent on the engine to grab the engine instance.

        messageRegistry.register(ClientInitMessage::class)
        messageRegistry.register(LogMessage::class)
        messageRegistry.register(PingMessage::class)

        messageRegistry.register(EntityCreateMessage::class)
        messageRegistry.register(AttributeUpdateMessage::class)
        messageRegistry.register(EntityDestroyMessage::class)
        messageRegistry.register(TimeSyncMessage::class)

        /** network write registry **/

        networkReadWriteRegistry.register(Byte::class, Pair({ byteBuf -> byteBuf.readByte() }, { value, byteBuf -> byteBuf.writeByte((value as Byte).toInt()) }))
        networkReadWriteRegistry.register(Short::class, Pair({ byteBuf -> byteBuf.readShort() }, { value, byteBuf -> byteBuf.writeShort((value as Short).toInt()) }))
        networkReadWriteRegistry.register(Int::class, Pair({ byteBuf -> byteBuf.readInt() }, { value, byteBuf -> byteBuf.writeInt(value as Int) }))
        networkReadWriteRegistry.register(Long::class, Pair({ byteBuf -> byteBuf.readLong() }, { value, byteBuf -> byteBuf.writeLong(value as Long) }))

        networkReadWriteRegistry.register(Float::class, Pair({ byteBuf -> byteBuf.readFloat() }, { value, byteBuf -> byteBuf.writeFloat(value as Float) }))
        networkReadWriteRegistry.register(Double::class, Pair({ byteBuf -> byteBuf.readDouble() }, { value, byteBuf -> byteBuf.writeDouble(value as Double) }))

        networkReadWriteRegistry.register(BigInteger::class, Pair({ byteBuf -> byteBuf.readBigInteger() }, { value, byteBuf -> byteBuf.writeBigInteger(value as BigInteger) }))
        networkReadWriteRegistry.register(BigDecimal::class, Pair({ byteBuf -> byteBuf.readBigDecimal() }, { value, byteBuf -> byteBuf.writeBigDecimal(value as BigDecimal) }))

        networkReadWriteRegistry.register(String::class, Pair({ byteBuf -> byteBuf.readUTF8String() }, { value, byteBuf -> byteBuf.writeUTF8String(value as String) }))

        networkReadWriteRegistry.register(Vector2ic::class, Pair({ byteBuf -> byteBuf.readVector2ic() }, { value, byteBuf -> byteBuf.writeVector2ic(value as Vector2ic) }))
        networkReadWriteRegistry.register(Vector2fc::class, Pair({ byteBuf -> byteBuf.readVector2fc() }, { value, byteBuf -> byteBuf.writeVector2fc(value as Vector2fc) }))
        networkReadWriteRegistry.register(Vector2dc::class, Pair({ byteBuf -> byteBuf.readVector2dc() }, { value, byteBuf -> byteBuf.writeVector2dc(value as Vector2dc) }))

        networkReadWriteRegistry.register(Vector3ic::class, Pair({ byteBuf -> byteBuf.readVector3ic() }, { value, byteBuf -> byteBuf.writeVector3ic(value as Vector3ic) }))
        networkReadWriteRegistry.register(Vector3fc::class, Pair({ byteBuf -> byteBuf.readVector3fc() }, { value, byteBuf -> byteBuf.writeVector3fc(value as Vector3fc) }))
        networkReadWriteRegistry.register(Vector3dc::class, Pair({ byteBuf -> byteBuf.readVector3dc() }, { value, byteBuf -> byteBuf.writeVector3dc(value as Vector3dc) }))

        networkReadWriteRegistry.register(Vector4ic::class, Pair({ byteBuf -> byteBuf.readVector4ic() }, { value, byteBuf -> byteBuf.writeVector4ic(value as Vector4ic) }))
        networkReadWriteRegistry.register(Vector4fc::class, Pair({ byteBuf -> byteBuf.readVector4fc() }, { value, byteBuf -> byteBuf.writeVector4fc(value as Vector4fc) }))
        networkReadWriteRegistry.register(Vector4dc::class, Pair({ byteBuf -> byteBuf.readVector4dc() }, { value, byteBuf -> byteBuf.writeVector4dc(value as Vector4dc) }))

        networkReadWriteRegistry.register(Matrix2fc::class, Pair({ byteBuf -> byteBuf.readMatrix2fc() }, { value, byteBuf -> byteBuf.writeMatrix2fc(value as Matrix2fc) }))
        networkReadWriteRegistry.register(Matrix2dc::class, Pair({ byteBuf -> byteBuf.readMatrix2dc() }, { value, byteBuf -> byteBuf.writeMatrix2dc(value as Matrix2dc) }))

        networkReadWriteRegistry.register(Matrix3fc::class, Pair({ byteBuf -> byteBuf.readMatrix3fc() }, { value, byteBuf -> byteBuf.writeMatrix3fc(value as Matrix3fc) }))
        networkReadWriteRegistry.register(Matrix3dc::class, Pair({ byteBuf -> byteBuf.readMatrix3dc() }, { value, byteBuf -> byteBuf.writeMatrix3dc(value as Matrix3dc) }))

        networkReadWriteRegistry.register(Matrix4fc::class, Pair({ byteBuf -> byteBuf.readMatrix4fc() }, { value, byteBuf -> byteBuf.writeMatrix4fc(value as Matrix4fc) }))
        networkReadWriteRegistry.register(Matrix4dc::class, Pair({ byteBuf -> byteBuf.readMatrix4dc() }, { value, byteBuf -> byteBuf.writeMatrix4dc(value as Matrix4dc) }))

        logger.info("Finished initializing Kosmos Engine in ${System.currentTimeMillis() - start}ms")
    }
}