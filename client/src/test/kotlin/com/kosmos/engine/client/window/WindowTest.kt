package com.kosmos.engine.client.window

import com.kosmos.engine.client.render.window.WindowManager

fun main() {
    WindowManager.init()
    val windows = Array(50) { index -> WindowManager.create(1280, 720, "Test $index") }

    var complete = false
    while (!complete) {
        complete = true
        WindowManager.update()
        for (window in windows) {
            if (window.closing) {
                window.free()
                continue
            }
            window.update()
            complete = false
        }
    }
    WindowManager.free()
}