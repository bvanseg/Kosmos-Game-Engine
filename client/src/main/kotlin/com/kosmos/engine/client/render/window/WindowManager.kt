package com.kosmos.engine.client.render.window

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL

object WindowManager {

    private val windows = HashSet<Window>()

    fun create(
        width: Int,
        height: Int,
        name: CharSequence,
        shareHandle: Long = NULL,
        windowHints: () -> Unit = {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        }
    ): Window {
        val window = Window(width, height, name)
        windows.add(window)
        glfwDefaultWindowHints()
        windowHints.invoke()
        window.create(shareHandle)
        return window
    }

    fun update() = glfwPollEvents()

    fun destroyWindows() {
        windows.forEach(Window::free)
        windows.clear()
    }

    internal fun init() {
        if (!glfwInit())
            throw RuntimeException("Failed to initialize GLFW")
    }

    internal fun free() {
        glfwTerminate()
        windows.clear()
    }

    internal fun removeWindow(window: Window) {
        windows.remove(window)
    }
}