package com.kosmos.engine.client.render.window

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.NativeResource

class Window internal constructor(width: Int, height: Int, title: CharSequence) : NativeResource {

    var handle: Long = NULL
        private set
    var creationTime: Long = 0
        private set
    var width: Int = width
        private set
    var height: Int = height
        private set
    var framebufferWidth: Int = width
        private set
    var framebufferHeight: Int = height
        private set
    var title: CharSequence = title
        private set
    var closing: Boolean = false
        private set

    internal fun create(shareHandle: Long) {
        if (handle != NULL)
            throw IllegalStateException("Window has already been created!")

        handle = glfwCreateWindow(width, height, title, NULL, shareHandle)
        if (handle == NULL)
            throw RuntimeException("Failed to create GLFW Window")
        creationTime = System.currentTimeMillis()

        val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        if (videoMode != null)
            glfwSetWindowPos(handle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2)

        glfwSetFramebufferSizeCallback(handle) { _, w, h ->
            framebufferWidth = w
            framebufferHeight = h
        }
        glfwSetWindowSizeCallback(handle) { _, w, h ->
            width = w
            height = h
        }
        glfwSetWindowCloseCallback(handle) { window -> closing = true }
    }

    fun update() = glfwSwapBuffers(handle)

    fun focus() = glfwFocusWindow(handle)

    fun requestFocus() = glfwRequestWindowAttention(handle)

    fun setIconified(iconified: Boolean) = if (iconified) glfwIconifyWindow(handle) else glfwRestoreWindow(handle)

    fun setMaximized(maximized: Boolean) = if (maximized) glfwMaximizeWindow(handle) else glfwRestoreWindow(handle)

    fun setVisible(visible: Boolean) = if (visible) glfwShowWindow(handle) else glfwHideWindow(handle)

    fun setSize(width: Int, height: Int) = glfwSetWindowSize(handle, width, height)

    fun setTitle(title: CharSequence) {
        this.title = title
        glfwSetWindowTitle(handle, title)
    }

    override fun free() {
        WindowManager.removeWindow(this) // The window doesn't need to be tracked anymore
        if (handle != NULL) {
            glfwDestroyWindow(handle)
            handle = NULL
        }
    }
}