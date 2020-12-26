package com.kosmos.engine.common.attribute

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
open class AttributeMutationSchema<T>(private val attribute: AttributeMap.Attribute<T>,
                                      protected val minLevel: Long = 0,
                                      protected val maxLevel: Long = 100,
                                      protected var currentLevel: Long = minLevel) {

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
}