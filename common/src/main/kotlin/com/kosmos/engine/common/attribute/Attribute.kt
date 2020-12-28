package com.kosmos.engine.common.attribute

import kotlin.reflect.KClass

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
data class Attribute<T : Any> constructor(val name: String, private var value: T, val type: KClass<out T>) {

    companion object {
        inline fun <reified T: Any> create(name: String, value: T): Attribute<T> = Attribute(name, value, T::class)
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
