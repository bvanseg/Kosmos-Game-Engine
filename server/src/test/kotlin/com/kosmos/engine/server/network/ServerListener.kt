package com.kosmos.engine.server.network

import bvanseg.kotlincommons.evenir.annotation.SubscribeEvent
import com.kosmos.engine.server.event.ServerBindEvent
import kotlin.system.exitProcess

object ServerListener {

    @SubscribeEvent
    fun onServerBind(serverBindEvent: ServerBindEvent.POST) {
        println("Received server bind event! Shutting down...")
        exitProcess(0)
    }
}