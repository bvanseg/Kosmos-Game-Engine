package com.kosmos.engine.common.state

import com.kosmos.engine.common.KosmosEngine
import com.kosmos.engine.common.event.StateDisposeEvent
import com.kosmos.engine.common.event.StateInitEvent

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class StateManager {

    var activeState: State = Stateless
        private set

    fun setState(state: State) {

        val engine = KosmosEngine.getInstance()

        engine.eventBus.fire(StateDisposeEvent.PRE(activeState))
        activeState.dispose()
        engine.eventBus.fire(StateDisposeEvent.POST(activeState))

        activeState = state

        engine.eventBus.fire(StateInitEvent.PRE(state))
        activeState.init()
        engine.eventBus.fire(StateInitEvent.POST(state))

    }
}