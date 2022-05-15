package com.example.simpleengine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.simpleengine.utils.ModelParser

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyGLRenderer

    private val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = MyGLRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        // Render the view only when there is a change in the drawing data
        //renderMode = RENDERMODE_WHEN_DIRTY

        val path = context.getFilesDir()
        //Log.e("path", path.path)
        val models =
            arrayOf("cube.obj", "cilinder.obj", "icosphere.obj", "sphere.obj", "monkey.obj")
        renderer.model3dData = ModelParser.parse("${path.path}/${models[0]}")

        renderer.texture = getResourceTexture(R.drawable.wood)
    }

    private fun getResourceTexture(id: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inScaled = false // No pre-scaling
        return BitmapFactory.decodeResource(getResources(), id, options)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx: Float = x - previousX
                var dy: Float = y - previousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }

    fun goForward() {
        renderer.cameraComponent.goForward()
        requestRender()
    }

    fun goBackward() {
        renderer.cameraComponent.goBackward()
        requestRender()
    }

    fun goLeft() {
        renderer.cameraComponent.goLeft()
        requestRender()
    }

    fun goRight() {
        renderer.cameraComponent.goRight()
        requestRender()
    }

    fun lookUp() {
        renderer.cameraComponent.lookUp()
        requestRender()
    }

    fun lookDown() {
        renderer.cameraComponent.lookDown()
        requestRender()
    }

    fun lookLeft() {
        renderer.cameraComponent.lookLeft()
        requestRender()
    }

    fun lookRight() {
        renderer.cameraComponent.lookRight()
        requestRender()
    }
}
