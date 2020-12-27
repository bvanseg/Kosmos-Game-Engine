package com.kosmos.engine.common.attribute

import bvanseg.kotlincommons.any.getLogger
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
class AttributeMap(bearer: Any? = null) {

    companion object {
        val logger = getLogger()
    }

    private val backingMap = ConcurrentHashMap<String, Attribute<*>>()

    private val modifiedAttributes = hashSetOf<String>()

    fun addAttribute(attribute: Attribute<*>) {
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

        backingMap[name] = attribute

        return attribute
    }

    fun <T: Any> getAttribute(name: String): Attribute<T>? = backingMap[name] as Attribute<T>?

    fun <T: Any> getOrCreateAttribute(name: String, value: T): Attribute<T> = backingMap.computeIfAbsent(name) {
        createAttribute(name, value)
    } as Attribute<T>

    fun getAllAttributes() = backingMap.values

    fun clearAttributes() = backingMap.clear()

    fun upgrade() {
        backingMap.forEach { (_, attribute) ->
            attribute.attributeMutationSchema?.upgrade()
        }
    }

    fun downgrade() {
        backingMap.forEach { (_, attribute) ->
            attribute.attributeMutationSchema?.downgrade()
        }
    }

    fun write(buffer: ByteBuf) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        // Write the number of attributes to the buffer.
        buffer.writeShort(backingMap.size)

        // Write all of our attributes to the buffer with name first and attribute data following the name.
        for((_, attribute) in backingMap) {
            val type = attribute.type

            // Get write data for attribute type.
            val readWriteEntry = registry.getEntry(type) ?: continue // TODO: Warn or throw exception
            val write = readWriteEntry.value.second

            // write Attribute.
            buffer.writeUTF8String(attribute.name)
            write(attribute.get(), buffer)

            // write AttributeMutationSchema.
            val schema = attribute.attributeMutationSchema
            if(schema != null) {
                buffer.writeLong(schema.currentLevel)
            }
        }
    }

    fun read(buffer: ByteBuf) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        val attributeCount = buffer.readShort()

        for(i in 0 until attributeCount) {
            val name = buffer.readUTF8String()

            val attribute = (backingMap[name] ?: return) as Attribute<Any>

            val type = attribute.type

            // Get read data for attribute type.
            val readWriteEntry = registry.getEntry(type)

            if(readWriteEntry == null) {
                logger.warn("Failed to get read/write entry for attribute type '$type'")
                continue
            }

            val read = readWriteEntry.value.first

            // read Attribute properties.
            val value = read(buffer)

            // Set Attribute to value from buffer.
            attribute.set(value)

            // Read AttributeMutationSchema data.
            val schema = attribute.attributeMutationSchema
            if(schema != null) {
                val currentLevel = buffer.readLong()

                // We need to upgrade/downgrade up until the current level of the schema.
                // We must do this because not all schemas use level to calculate current value (some follow algorithms
                // using nothing but the base value, so we must take each step through those algorithms).
                if (schema.currentLevel < currentLevel) {
                    while (schema.currentLevel < currentLevel) {
                        schema.upgrade()
                    }
                } else if (schema.currentLevel > currentLevel) {
                    while (schema.currentLevel > currentLevel) {
                        schema.downgrade()
                    }
                }
            }
        }
    }

    fun writeModifiedAttributes(buffer: ByteBuf, clearAttributeChanges: Boolean = true) {
        val engine = KosmosEngine.getInstance()
        val registry = engine.networkReadWriteRegistry

        // Write the number of attributes to the buffer.
        buffer.writeShort(modifiedAttributes.size)

        // Write all of our attributes to the buffer with name first and attribute data following the name.
        for(attributeName in modifiedAttributes) {
            val attribute = backingMap[attributeName] ?: continue // TODO: Warn or throw exception

            val type = attribute.type

            // Get write data for attribute type.
            val readWriteEntry = registry.getEntry(type) ?: continue // TODO: Warn or throw exception
            val write = readWriteEntry.value.second

            // write Attribute.
            buffer.writeUTF8String(attribute.name)
            write(attribute.get(), buffer)

            // write AttributeMutationSchema.
            val schema = attribute.attributeMutationSchema
            if(schema != null) {
                buffer.writeLong(schema.currentLevel)
            }
        }

        if(clearAttributeChanges) {
            modifiedAttributes.clear()
        }
    }

    fun notifyAttributeChange(attribute: Attribute<*>) {
        modifiedAttributes.add(attribute.name)
    }

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

    /**
     * @author Boston Vanseghi
     * @since 1.0.0
     */
    data class Attribute<T : Any> internal constructor(val name: String, private var value: T, val type: KClass<out T>) {

        val initialValue: T = value

        var attributeMutationSchema: AttributeMutationSchema<T>? = null

        lateinit var attributeMap: AttributeMap

        fun get() = value

        fun set(value: T) {
            val hasChanged = this.value != value
            this.value = value

            if (hasChanged) {
                attributeMap.notifyAttributeChange(this)
            }
        }

        override fun hashCode(): Int = ((name.hashCode() * 47) + value.hashCode()) * 47
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Attribute<*>

            if (name != other.name) return false
            if (value != other.value) return false

            return true
        }
    }
}