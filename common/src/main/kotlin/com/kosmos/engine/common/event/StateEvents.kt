package com.kosmos.engine.common.event

import com.kosmos.engine.common.state.State

open class StateEvent(open val state: State): KosmosEngineEvent()

open class StateInitEvent(state: State): StateEvent(state) {
    class PRE(state: State): StateInitEvent(state)
    class POST(state: State): StateInitEvent(state)
}

open class StateDisposeEvent(state: State): StateEvent(state) {
    class PRE(state: State): StateDisposeEvent(state)
    class POST(state: State): StateDisposeEvent(state)
}