package com.kosmos.engine.common.entity

import com.kosmos.engine.common.attribute.Attribute
import com.kosmos.engine.common.attribute.AttributeMap

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityDummy: Entity() {

    val health      = Attribute.create("health", 100)
    val shields     = Attribute.create("shields", 100)
    val energy      = Attribute.create("energy", 100)

    override fun initAttributes(attributeMap: AttributeMap) {
        super.initAttributes(attributeMap)

        attributeMap.addAttribute(health)
        attributeMap.addAttribute(shields)
        attributeMap.addAttribute(energy)
    }

    override fun update() {
        super.update()
        health.set(health.get() - 1)
    }
}