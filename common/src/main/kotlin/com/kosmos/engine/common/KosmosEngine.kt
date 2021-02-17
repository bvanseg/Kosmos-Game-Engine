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

        val networkedTypes = hashSetOf<KClass<*>>(
            Byte::class, Short::class, Int::class, Long::class,
            Float::class, Double::class,
            BigInteger::class, BigDecimal::class,
            String::class,
            Vector2ic::class, Vector2fc::class, Vector2dc::class,
            Vector3ic::class, Vector3fc::class, Vector3dc::class,
            Vector4ic::class, Vector4fc::class, Vector4dc::class,
            Matrix2fc::class, Matrix2dc::class,
            Matrix3fc::class, Matrix3dc::class,
            Matrix4fc::class, Matrix4dc::class
        )

        networkedTypes.forEach { kclass ->
            val readCb: (ByteBuf) -> Unit = { it.read() }
            val writeCb: (Any, ByteBuf) -> Unit = { value, byteBuf -> byteBuf.write(value) }
            networkReadWriteRegistry.register(kclass, Pair(readCb, writeCb))
        }

        logger.info("Finished initializing Kosmos Engine in ${System.currentTimeMillis() - start}ms")
    }
}