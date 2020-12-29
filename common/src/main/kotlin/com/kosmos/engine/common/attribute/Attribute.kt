package com.kosmos.engine.common.attribute

import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.util.readUTF8String
import com.kosmos.engine.common.network.util.writeUTF8String
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
data class Attribute<T : Any> constructor(val name: String, private var value: T, val type: KClass<out T>) {

    companion object {
        inline fun <reified T: Any> create(name: String, value: T): Attribute<T> = Attribute(name, value, T::class)

        fun read(buffer: ByteBuf): Attribute<out Any>? {
            val engine = KosmosEngine.getInstance()
            val registry = engine.networkReadWriteRegistry

            val name = buffer.readUTF8String()
            val attributeTypeID = buffer.readInt()
            val type = registry.getKeyByID(attributeTypeID) ?: throw RuntimeException("Failed to get key for attribute type using id $attributeTypeID")

            // Get read data for attribute type.
            val readWriteEntry = registry.getEntry(type)

            if(readWriteEntry == null) {
                AttributeMap.logger.warn("Failed to get read/write entry for attribute type '$type'")
                return null
            }

            val read = readWriteEntry.value.first

            // read Attribute properties.
            val value = read(buffer)

            return Attribute(name, value, type)
        }
    }

    val initialValue: T = value

    var isDefault: Boolean = false

    internal lateinit var attributeMap: AttributeMap

    fun get() = value

    fun set(value: T) {
        val hasChanged = this.value != value
        this.value = value

        if (hasChanged) {
            attributeMap.notifyAttributeChange(this)
        }
    }

    fun write(buffer: ByteBuf) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        val readWriteEntry = registry.getEntry(type)
            ?: throw RuntimeException("Failed to get read/write entry for type '$type'")

        val write = readWriteEntry.value.second

        // write Attribute.
        buffer.writeUTF8String(name)
        val attributeTypeID = registry.getIDForKey(type) ?: throw RuntimeException("Attribute type is not registered: $type")
        buffer.writeInt(attributeTypeID)
        write(this.get(), buffer)
    }
}
