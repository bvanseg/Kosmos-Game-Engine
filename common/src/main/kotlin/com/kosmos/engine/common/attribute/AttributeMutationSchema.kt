package com.kosmos.engine.common.attribute

import bvanseg.kotlincommons.string.ToStringBuilder

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
open class AttributeMutationSchema<T : Any>(private val attribute: AttributeMap.Attribute<T>,
                                            val minLevel: Long = 0,
                                            val maxLevel: Long = 100,
                                            var currentLevel: Long = minLevel) {

    private var upgrade: ((AttributeMap.Attribute<T>) -> Unit)? = null
    private var downgrade: ((AttributeMap.Attribute<T>) -> Unit)? = null

    fun upgrade() = upgrade?.let {
        if (currentLevel < maxLevel) {
            it(attribute)
            currentLevel++
        }
    }

    fun downgrade() = downgrade?.let {
        if (currentLevel > minLevel) {
            it(attribute)
            currentLevel--
        }
    }

    fun setUpgradeSchema(upgrade: (AttributeMap.Attribute<T>) -> Unit) {
        this.upgrade = upgrade
    }

    fun setDowngradeSchema(downgrade: (AttributeMap.Attribute<T>) -> Unit) {
        this.downgrade = downgrade
    }

    override fun toString(): String = ToStringBuilder.builder(this::class)
        .append("minLevel", minLevel)
        .append("currentLevel", currentLevel)
        .append("maxLevel", maxLevel)
        .toString()
}