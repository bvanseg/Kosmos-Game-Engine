package com.kosmos.engine.server.network.commands

import bvanseg.kotlincommons.util.command.annotation.Command
import bvanseg.kotlincommons.util.command.context.Context
import bvanseg.kotlincommons.util.command.gear.Gear
import com.kosmos.engine.server.game.ServerGameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ServerGear(val serverGameContainer: ServerGameContainer): Gear("server") {

    @Command
    fun update(ctx: Context, count: Int = 1) {
        for(i in 0 until count) {
            serverGameContainer.update()
        }
    }
}