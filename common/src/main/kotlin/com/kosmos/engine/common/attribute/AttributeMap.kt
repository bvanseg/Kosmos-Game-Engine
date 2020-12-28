package com.kosmos.engine.common.attribute

import bvanseg.kotlincommons.any.getLogger
import bvanseg.kotlincommons.string.ToStringBuilder
import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.network.util.readUTF8String
import com.kosmos.engine.common.network.util.writeUTF8String
import io.netty.buffer.ByteBuf
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class AttributeMap(val bearer: Any? = null) {

    companion object {
        val logger = getLogger()
    }

    private val backingMap = ConcurrentHashMap<String, Attribute<out Any>>()

    private val modifiedAttributes = hashSetOf<String>()

    val size: Int
        get() = backingMap.size

    fun addAttribute(attribute: Attribute<out Any>) {
        backingMap[attribute.name] = attribute
        attribute.attributeMap = this
    }

    fun <T: Any> createAttribute(name: String, value: T): Attribute<T> {

        val klazz = value::class

        if(klazz == Attribute::class) {
            throw IllegalArgumentException("Attributes may not contain other attributes!")
        } else if(klazz == AttributeMap::class) {
            throw IllegalArgumentException("Attributes may not contain attribute maps!")
        }

        val attribute = Attribute(name, value, klazz)

        attribute.attributeMap = this
        backingMap[name] = attribute as Attribute<Any>

        return attribute
    }

    fun <T: Any> getAttribute(name: String): Attribute<T>? = backingMap[name] as Attribute<T>?

    fun <T: Any> getOrCreateAttribute(name: String, value: T): Attribute<T> = backingMap.computeIfAbsent(name) {
        createAttribute(name, value)
    } as Attribute<T>

    fun getAllAttributes() = backingMap.values

    fun clearAttributes() = backingMap.clear()

    fun write(buffer: ByteBuf) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        // Write the number of non-default or modified attributes to the buffer.
        val nonDefaultAttributes = backingMap.filter { !it.value.isDefault || modifiedAttributes.contains(it.key) }
        buffer.writeShort(nonDefaultAttributes.size)

        // Write all of our attributes to the buffer with name first and attribute data following the name.
        for((_, attribute) in nonDefaultAttributes) {
            val type = attribute.type

            // Get write data for attribute type.
            val readWriteEntry = registry.getEntry(type)
                ?: throw RuntimeException("Failed to get read/write entry for type '$type'")

            val write = readWriteEntry.value.second

            // write Attribute.
            buffer.writeUTF8String(attribute.name)
            val attributeTypeID = registry.getIDForKey(type) ?: throw RuntimeException("Attribute type is not registered: $type")
            buffer.writeInt(attributeTypeID)
            write(attribute.get(), buffer)
        }
    }

    fun read(buffer: ByteBuf) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        val attributeCount = buffer.readShort()

        for(i in 0 until attributeCount) {
            val name = buffer.readUTF8String()
            val attributeTypeID = buffer.readInt()
            val type = registry.getKeyByID(attributeTypeID) ?: throw RuntimeException("Failed to get key for attribute type using id $attributeTypeID")

            // Get read data for attribute type.
            val readWriteEntry = registry.getEntry(type)

            if(readWriteEntry == null) {
                logger.warn("Failed to get read/write entry for attribute type '$type'")
                continue
            }

            val read = readWriteEntry.value.first

            // read Attribute properties.
            val value = read(buffer)

            createAttribute(name, value)
        }
    }

    fun writeModifiedAttributes(buffer: ByteBuf, clearAttributeChanges: Boolean = true) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        // Write the number of attributes to the buffer.
        buffer.writeShort(modifiedAttributes.size)

        // Write all of our attributes to the buffer with name first and attribute data following the name.
        for(attributeName in modifiedAttributes) {
            val attribute = backingMap[attributeName] ?: continue

            val type = attribute.type

            // Get write data for attribute type.
            val readWriteEntry = registry.getEntry(type)
                ?: throw RuntimeException("Failed to get read/write entry for type '$type'")

            val write = readWriteEntry.value.second

            // write Attribute.
            buffer.writeUTF8String(attribute.name)
            val attributeTypeID = registry.getIDForKey(type) ?: throw RuntimeException("Attribute type is not registered: $type")
            buffer.writeInt(attributeTypeID)
            write(attribute.get(), buffer)
        }

        if(clearAttributeChanges) {
            modifiedAttributes.clear()
        }
    }

    fun notifyAttributeChange(attribute: Attribute<*>) {
        modifiedAttributes.add(attribute.name)
    }

    fun hasModifiedAttributes(): Boolean = modifiedAttributes.isNotEmpty()

    /**
     * Merges the given [AttributeMap] into this AttributeMap.
     *
     * @param attributeMap The attribute map to merge into this map.
     */
    fun merge(attributeMap: AttributeMap) {
        attributeMap.getAllAttributes().forEach { attribute ->
            this.addAttribute(attribute)
        }
        attributeMap.clearAttributes()
    }

    override fun toString(): String = backingMap.toString()
}