package com.kosmos.engine.common.entity

import com.kosmos.engine.common.attribute.Attribute
import com.kosmos.engine.common.attribute.AttributeMap
import com.kosmos.engine.common.attribute.add

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityZealot: Entity() {

    val maxHealth = Attribute.create("maxHealth", 100.0f)
    val health = Attribute.create("health", maxHealth.get(), 0f, ubcb = { maxHealth.get() })
    val healthRegen = Attribute.create("healthRegen", 0.25f)

    val maxShields = Attribute.create("maxShields", 200.0f)
    val shields = Attribute.create("shields", maxShields.get(), 0f, ubcb = { maxShields.get() })
    val shieldsRegen = Attribute.create("shieldsRegen", 2.0f)

    val maxEnergy = Attribute.create("maxEnergy", 200.0f)
    val energy = Attribute.create("energy", 50.0f, 0f, ubcb = { maxEnergy.get() })
    val energyRegen = Attribute.create("energyRegen", 0.025f)

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
        health.add(healthRegen)
        shields.add(shieldsRegen)
        energy.add(energyRegen)
    }
}