package com.kosmos.engine.common.registry

import kotlin.reflect.KClass

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
open class FactoryRegistryEntry<T: Any>(private val factoryRegistry: FactoryRegistry<T>, val value: KClass<out T>): RegistryEntry<KClass<out T>, T>(factoryRegistry) {
    fun createInstance(): T? {
        val instance = factoryRegistry.factory(this)
        factoryRegistry.onCreateInstance(instance)
        return instance
    }
}