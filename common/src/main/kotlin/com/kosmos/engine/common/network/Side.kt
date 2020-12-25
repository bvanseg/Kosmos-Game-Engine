package com.kosmos.engine.common.network

import com.kosmos.engine.common.network.message.MessageTarget

/**
 * Represents the different networking sides that exist for the game.
 *
 * @author Boston Vanseghi
 * @since 1.0.0
 */
enum class Side {
    CLIENT,
    SERVER;

    fun opposite(): Side = when(this) {
        CLIENT -> SERVER
        SERVER -> CLIENT
    }

    fun toMessageTarget(): MessageTarget = when(this) {
        CLIENT -> MessageTarget.CLIENT
        SERVER -> MessageTarget.SERVER
    }
}