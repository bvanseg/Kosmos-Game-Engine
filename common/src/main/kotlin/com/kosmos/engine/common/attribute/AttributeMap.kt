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
    private val deletedAttributes = hashSetOf<String>()

    val size: Int
        get() = backingMap.size

    fun clearModifiedAttributes() = modifiedAttributes.clear()

    fun addAttribute(attribute: Attribute<out Any>) {
        backingMap[attribute.name] = attribute
        attribute.attributeMap = this
        modifiedAttributes.add(attribute.name)
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

        modifiedAttributes.add(attribute.name)

        return attribute
    }

    fun deleteAttribute(name: String) {
        if(backingMap.containsKey(name)) {
            backingMap.remove(name)
            deletedAttributes.add(name)
        }
    }

    fun <T: Any> getAttribute(name: String): Attribute<T>? = backingMap[name] as Attribute<T>?

    fun <T: Any> getOrCreateAttribute(name: String, value: T): Attribute<T> = backingMap.computeIfAbsent(name) {
        createAttribute(name, value)
    } as Attribute<T>

    fun getAllAttributes() = backingMap.values

    fun clearAttributes() = backingMap.clear()

    fun write(buffer: ByteBuf) {

        // Write the number of non-default or modified attributes to the buffer.
        val nonDefaultAttributes = backingMap.filter { !it.value.isDefault || modifiedAttributes.contains(it.key) }
        buffer.writeShort(nonDefaultAttributes.size)
        buffer.writeShort(deletedAttributes.size)

        // Write all of our attributes to the buffer with name first and attribute data following the name.
        for((_, attribute) in nonDefaultAttributes) {
            attribute.write(buffer)
        }
        modifiedAttributes.clear()

        for(attributeName in deletedAttributes) {
            buffer.writeUTF8String(attributeName)
        }
        deletedAttributes.clear()
    }

    fun read(buffer: ByteBuf) {

        val attributeCount = buffer.readShort()
        val deletedAttributeCount = buffer.readShort()

        for(i in 0 until attributeCount) {
            val attribute = Attribute.read(buffer)
            if (attribute != null) {
                addAttribute(attribute)
            } else {
                // TODO: Add logger warning here.
            }
        }

        for(i in 0 until deletedAttributeCount) {
            val attributeName = buffer.readUTF8String()
            backingMap.remove(attributeName)
        }
    }

    fun writeModifiedAttributes(buffer: ByteBuf) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        // Write the number of attributes to the buffer.
        buffer.writeShort(modifiedAttributes.size)
        buffer.writeShort(deletedAttributes.size)

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
        modifiedAttributes.clear()

        for(attributeName in deletedAttributes) {
            buffer.writeUTF8String(attributeName)
        }
        deletedAttributes.clear()
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