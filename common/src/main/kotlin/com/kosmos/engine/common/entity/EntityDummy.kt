package com.kosmos.engine.common.entity

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityDummy: Entity() {

    val health      = attributeMap.createAttribute("health", 100)
    val shields     = attributeMap.createAttribute("shields", 100)
    val energy      = attributeMap.createAttribute("energy", 100)
}