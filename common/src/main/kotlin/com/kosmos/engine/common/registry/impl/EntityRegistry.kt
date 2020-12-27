package com.kosmos.engine.common.registry.impl

import bvanseg.kotlincommons.javaclass.createNewInstance
import com.kosmos.engine.common.entity.Entity
import com.kosmos.engine.common.network.Networker
import com.kosmos.engine.common.network.Side
import com.kosmos.engine.common.network.message.impl.EntityCreateMessage
import com.kosmos.engine.common.registry.FactoryRegistry

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class EntityRegistry(val networker: Networker): FactoryRegistry<Entity>(factory = { entry ->
    createNewInstance(entry.value.java)
}) {

    override fun onCreateInstance(instance: Entity?) {
        super.onCreateInstance(instance)

        if (networker.side == Side.SERVER && instance != null) {
            val entityMessage = EntityCreateMessage(instance)
            networker.send(entityMessage)
        }
    }
}