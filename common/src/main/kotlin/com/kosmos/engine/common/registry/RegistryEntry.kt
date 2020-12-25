package com.kosmos.engine.common.registry

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
open class RegistryEntry<K, V : Any>(private val registry: Registry<K, V>)