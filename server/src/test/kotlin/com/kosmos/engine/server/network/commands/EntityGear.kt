package com.kosmos.engine.server.network.commands

import bvanseg.kotlincommons.armada.annotation.Command
import bvanseg.kotlincommons.armada.context.Context
import bvanseg.kotlincommons.armada.gear.Gear
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.entity.EntityDummy
import com.kosmos.engine.server.game.ServerGameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityGear(private val gameContainer: ServerGameContainer): Gear("entity") {

    @Command
    fun spawn(ctx: Context, amount: Int = 0) {
        println("creating entities...")
        val entities = mutableListOf<Entity>()
        for(i in 0 until amount) {
            entities.add(gameContainer.createEntity<EntityDummy>())
        }
    }
}