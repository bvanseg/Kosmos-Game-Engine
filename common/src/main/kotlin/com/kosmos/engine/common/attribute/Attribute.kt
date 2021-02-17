package com.kosmos.engine.common.attribute

import bvanseg.kotlincommons.math.to
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.util.readUTF8String
import com.kosmos.engine.common.network.util.writeUTF8String
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
open class Attribute<T : Any> constructor(val name: String, private var value: T, val type: KClass<out T>) {

    companion object {
        // Normal attribute creation.
        inline fun <reified T: Any> create(name: String, value: T): Attribute<T> = Attribute(name, value, T::class)

        // Clamping attribute creation.
        inline fun <reified T: Comparable<T>> create(name: String, value: T, lowerBound: T, upperBound: T): ClampingAttribute<T>
            = ClampingAttribute(name, value, T::class, lowerBound, upperBound)
        inline fun <reified T: Comparable<T>> create(name: String, value: T, lowerBound: T, noinline ubcb: () -> T): ClampingAttribute<T>
                = ClampingAttribute(name, value, T::class, lowerBound, ubcb()).setUpperBoundCallback(ubcb)
        inline fun <reified T: Comparable<T>> create(name: String, value: T, noinline lbcb: () -> T, upperBound: T): ClampingAttribute<T>
                = ClampingAttribute(name, value, T::class, lbcb(), upperBound).setLowerBoundCallback(lbcb)
        inline fun <reified T: Comparable<T>> create(name: String, value: T, noinline lbcb: () -> T, noinline ubcb: () -> T): ClampingAttribute<T>
                = ClampingAttribute(name, value, T::class, lbcb(), ubcb()).setLowerBoundCallback(lbcb).setUpperBoundCallback(ubcb)

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

    open fun get() = value

    open fun set(value: T) {
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
