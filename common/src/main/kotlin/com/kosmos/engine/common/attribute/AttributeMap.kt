package com.kosmos.engine.common.attribute

import java.util.concurrent.ConcurrentHashMap

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class AttributeMap {

    private val backingMap = ConcurrentHashMap<String, Attribute<*>>()

    fun <T> createAttribute(name: String, value: T): Attribute<T> {

        val attribute = Attribute(name, value)

        backingMap[name] = attribute

        return attribute
    }

    fun <T> getAttribute(name: String, value: T): Attribute<T>? = backingMap[name] as Attribute<T>?

    fun <T> getOrCreateAttribute(name: String, value: T): Attribute<T> = backingMap.computeIfAbsent(name) {
        Attribute(name, value)
    } as Attribute<T>

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

    override fun toString(): String = backingMap.toString()

    /**
     * @author Boston Vanseghi
     * @since 1.0.0
     */
    class Attribute<T> internal constructor(val name: String, private var value: T) {

        val initialValue: T = value

        var attributeMutationSchema: AttributeMutationSchema<T>? = null

        fun get() = value

        fun set(value: T) {
            this.value = value
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

        override fun toString(): String = "$name:$value"
    }
}