package com.kosmos.engine.client.render.window

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.NativeResource

class Window internal constructor(width: Int, height: Int, title: CharSequence) : NativeResource {

    var handle: Long = NULL
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

        val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        if (videoMode != null)
            glfwSetWindowPos(handle, (videoMode.width() - this.width) / 2, (videoMode.height() - this.height) / 2)

        glfwSetFramebufferSizeCallback(handle) { window, width, height ->
            if (this.handle == window) {
                this.framebufferWidth = width
                this.framebufferHeight = height
            }
        }
        glfwSetWindowSizeCallback(handle) { window, width, height ->
            if (this.handle == window) {
                this.width = width
                this.height = height
            }
        }
        glfwSetWindowCloseCallback(handle) { window -> this.closing = this.closing || this.handle == window }
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