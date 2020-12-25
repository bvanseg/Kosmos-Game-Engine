package com.kosmos.engine.common.network.message

import com.kosmos.engine.common.network.Side

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
enum class MessageTarget {
    CLIENT,
    COMMON,
    SERVER;

    fun opposite(): MessageTarget = when(this) {
        CLIENT -> SERVER
        SERVER -> CLIENT
        COMMON -> COMMON
    }

    fun toSide(): Side? = when(this) {
        CLIENT -> Side.CLIENT
        SERVER -> Side.SERVER
        COMMON -> null
    }
}