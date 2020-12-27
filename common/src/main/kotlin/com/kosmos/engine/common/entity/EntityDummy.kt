package com.kosmos.engine.common.entity

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityDummy: Entity() {

    init {
        attributeMap.createAttribute("health", 100)
    }
}