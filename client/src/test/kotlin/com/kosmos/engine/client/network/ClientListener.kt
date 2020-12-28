package com.kosmos.engine.client.network

import bvanseg.kotlincommons.evenir.annotation.SubscribeEvent
import com.kosmos.engine.common.entity.EntityZealot
import com.kosmos.engine.common.event.RegisterEntitiesEvent

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
object ClientListener {

    @SubscribeEvent
    fun onEntitiesRegister(event: RegisterEntitiesEvent) {
        event.entityRegistry.register(EntityZealot::class)
    }
}