package com.kosmos.engine.common.attribute

import bvanseg.kotlincommons.util.comparable.clamp
import kotlin.reflect.KClass

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ClampingAttribute<T: Comparable<T>>(
    name: String,
    value: T,
    type: KClass<out T>,
    private val lowerBound: T,
    private val upperBound: T
): Attribute<T>(name, value, type) {

    private var lowerBoundCallback: (() -> T)? = null
    private var upperBoundCallback: (() -> T)? = null

    fun setLowerBoundCallback(lowerBoundCallback: () -> T): ClampingAttribute<T> {
        this.lowerBoundCallback = lowerBoundCallback
        return this
    }

    fun setUpperBoundCallback(upperBoundCallback: () -> T): ClampingAttribute<T> {
        this.upperBoundCallback = upperBoundCallback
        return this
    }

    override fun set(value: T) {
        val clampedValue = clamp(value, lowerBoundCallback?.invoke() ?: lowerBound, upperBoundCallback?.invoke() ?: upperBound)
        super.set(clampedValue)
    }
}