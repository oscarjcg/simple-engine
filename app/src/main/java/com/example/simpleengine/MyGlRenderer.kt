package com.example.simpleengine

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.simpleengine.shapes.Line
import com.example.simpleengine.shapes.Triangle
import com.example.simpleengine.utils.RGBA
import com.example.simpleengine.utils.Vector3

class MyGLRenderer : GLSurfaceView.Renderer {

    private lateinit var triangle: Triangle
    private lateinit var xAxis: Line
    private lateinit var yAxis: Line
    private lateinit var zAxis: Line

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private val rotationMatrix = FloatArray(16)

    @Volatile
    var angle: Float = 0f

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // initialize a triangle
        /*
        in counterclockwise order:
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
         */
        triangle = Triangle(
            Vector3(0.0f, 0.622008459f, 0.0f),
            Vector3(-0.5f, -0.311004243f, 0.0f),
            Vector3(0.5f, -0.311004243f, 0.0f),
            RGBA(0.63671875f, 0.76953125f, 0.22265625f, 1.0f))

        val origin = Vector3(0f, 0f, 0f)
        val xCoor = Vector3(1f, 0f, 0f)
        val yCoor = Vector3(0f, 1f, 0f)
        val zCoor = Vector3(0f, 0f, 1f)
        val red = RGBA(1f, 0f, 0f, 1f)
        val green = RGBA(0f, 1f, 0f, 1f)
        val blue = RGBA(0f, 0f, 1f, 1f)
        xAxis = Line(origin, xCoor, red)
        yAxis = Line(origin, yCoor, green)
        zAxis = Line(origin, zCoor, blue)
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0,
            1f, 1f, 3f, // Position
            0f, 0f, 0f, // Where to look
            0f, 1.0f, 0.0f) //

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Draw shape
        //mTriangle.draw(vPMatrix)

        val scratch = FloatArray(16)

        // Create a rotation transformation for the triangle
        /*val time = SystemClock.uptimeMillis() % 4000L
        val angle = 0.090f * time.toInt()*/
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        // Draw triangle
        triangle.draw(scratch)

        xAxis.draw(scratch)
        yAxis.draw(scratch)
        zAxis.draw(scratch)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()


        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 7f)

    }

    companion object {
        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            return GLES20.glCreateShader(type).also { shader ->

                // add the source code to the shader and compile it
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
            }
        }
    }

}
