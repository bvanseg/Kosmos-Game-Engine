package com.kosmos.engine.common.registry

import bvanseg.kotlincommons.grouping.collection.DualHashMap
import bvanseg.kotlincommons.io.logging.getLogger
import kotlin.reflect.KClass

/**
 * Represents the base of a registry.
 *
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class InstanceRegistry<K : Any, V : Any>: Registry<K, V>() {

    private val logger = getLogger()

    private val idToKeyMap = DualHashMap<Int, K>()
    private val keyToIDMap = idToKeyMap.reverse()

    fun register(key: K, value: V) {
        val entry = InstanceRegistryEntry(this, value)

        if(entries.contains(entry)) {
            throw IllegalStateException("Attempted to register an entry that already exists: $value")
        }

        entries[key] = entry

        when (key) {
            is KClass<*> -> idToKeyMap[key.simpleName.hashCode()] = key
            else -> idToKeyMap[key.hashCode()] = key
        }

        logger.info("Successfully registered entry $value")
    }

    fun getKeyByID(id: Int) = idToKeyMap[id]
    fun getIDForKey(key: K) = keyToIDMap[key]
    fun getEntry(key: K) = entries[key] as InstanceRegistryEntry?
}