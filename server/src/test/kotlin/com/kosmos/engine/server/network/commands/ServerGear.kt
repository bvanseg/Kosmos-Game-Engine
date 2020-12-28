package com.kosmos.engine.server.network.commands

import bvanseg.kotlincommons.armada.annotation.Command
import bvanseg.kotlincommons.armada.context.Context
import bvanseg.kotlincommons.armada.gear.Gear
import com.kosmos.engine.server.game.ServerGameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class ServerGear(val serverGameContainer: ServerGameContainer): Gear("server") {

    @Command
    fun update(ctx: Context) {
        serverGameContainer.update()
    }
}