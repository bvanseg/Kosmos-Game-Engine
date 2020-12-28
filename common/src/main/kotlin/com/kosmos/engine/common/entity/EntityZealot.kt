package com.kosmos.engine.common.entity

import bvanseg.kotlincommons.comparable.clamp
import com.kosmos.engine.common.attribute.Attribute
import com.kosmos.engine.common.attribute.AttributeMap

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityZealot: Entity() {

    val health                  = Attribute.create("health", 100.0f)
    val healthRegen             = Attribute.create("healthRegen", 0.25f)
    val maxHealth               = Attribute.create("maxHealth", 100.0f)

    val shields                 = Attribute.create("shields", 200.0f)
    val shieldsRegen            = Attribute.create("shieldsRegen", 2.0f)
    val maxShields              = Attribute.create("maxShields", 200.0f)

    val energy                  = Attribute.create("energy", 50.0f)
    val energyRegen             = Attribute.create("energyRegen", 0.025f)
    val maxEnergy               = Attribute.create("maxEnergy", 200.0f)

    override fun initAttributes(attributeMap: AttributeMap) {
        super.initAttributes(attributeMap)

        attributeMap.addAttribute(health)
        attributeMap.addAttribute(healthRegen)
        attributeMap.addAttribute(maxHealth)

        attributeMap.addAttribute(shields)
        attributeMap.addAttribute(shieldsRegen)
        attributeMap.addAttribute(maxShields)

        attributeMap.addAttribute(energy)
        attributeMap.addAttribute(energyRegen)
        attributeMap.addAttribute(maxEnergy)
    }

    override fun update() {
        super.update()
        health.set(clamp(health.get() + healthRegen.get(), 0f, maxHealth.get()))
        shields.set(clamp(shields.get() + shieldsRegen.get(), 0f, maxShields.get()))
        energy.set(clamp(energy.get() + energyRegen.get(), 0f, maxEnergy.get()))
    }
}