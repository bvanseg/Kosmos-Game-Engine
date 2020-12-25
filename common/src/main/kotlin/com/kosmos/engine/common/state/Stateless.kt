package com.kosmos.engine.common.state

/**
 * A default state that functionally does nothing.
 *
 * @author Boston Vanseghi
 * @since 1.0.0
 */
object Stateless: State {
    override fun init() = Unit
    override fun update() = Unit
    override fun dispose() = Unit
}