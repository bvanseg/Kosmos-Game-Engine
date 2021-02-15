package com.kosmos.engine.server.network.commands

import bvanseg.kotlincommons.util.command.annotation.Command
import bvanseg.kotlincommons.util.command.context.Context
import bvanseg.kotlincommons.util.command.gear.Gear
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.entity.EntityZealot
import com.kosmos.engine.server.game.ServerGameContainer
import java.util.UUID

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityGear(private val serverGameContainer: ServerGameContainer): Gear("entity") {

    @Command
    fun spawn(ctx: Context, amount: Int = 0) {
        println("creating entities...")
        val entities = mutableListOf<Entity>()
        for(i in 0 until amount) {
            entities.add(serverGameContainer.createEntity<EntityZealot>())
        }
    }

    @Command
    fun count(ctx: Context) {
        println(serverGameContainer.entities.size)
    }

    @Command
    fun kill(ctx: Context, arg: String) {
        when(arg.toLowerCase()) {
            "all" -> {
                serverGameContainer.entities.forEach { (_, entity) ->
                    entity.setDead()
                }
            }
            else -> {
                val uuid = UUID.fromString(arg)
                serverGameContainer.entities[uuid]?.setDead()
            }
        }
    }
}