package com.kosmos.engine.common.event

import com.kosmos.engine.common.registry.impl.EntityRegistry

open class RegistryEvent: KosmosEngineEvent()

class RegisterEntitiesEvent(val entityRegistry: EntityRegistry): RegistryEvent()