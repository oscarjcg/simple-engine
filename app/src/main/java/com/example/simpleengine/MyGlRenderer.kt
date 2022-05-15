package com.example.simpleengine

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import com.example.simpleengine.components.CameraComponent
import com.example.simpleengine.components.WorldAxisComponent
import com.example.simpleengine.shapes.Line
import com.example.simpleengine.shapes.Triangle
import com.example.simpleengine.utils.Model3dData
import com.example.simpleengine.utils.RGBA
import com.example.simpleengine.utils.Vector3
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyGLRenderer : GLSurfaceView.Renderer {

    var cameraComponent = CameraComponent()
    var worldAxis = WorldAxisComponent()

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    /** Used to hold a light centered on the origin in model space.
     * We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.  */
    private val mLightPosInModelSpace = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    /** Used to hold the transformed position of the light in eye space
     * (after transformation via modelview matrix)  */
    private val mLightPosInEyeSpace = FloatArray(4)
    /** Used to hold the current position of the light in world space
     * (after transformation via model matrix).  */
    private val mLightPosInWorldSpace = FloatArray(4)

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private val mLightModelMatrix = FloatArray(16)

    private val rotationMatrix = FloatArray(16)

    private var mTextureDataHandle: Int = 0
    lateinit var texture: Bitmap

    @Volatile
    var angle: Float = 0f
    var model3dData = Model3dData()

    var lineLight = Line(Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), RGBA(1f, 0f, 0f, 1f))

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        worldAxis.initComponent()

        // Load the texture
        mTextureDataHandle = loadTexture(texture)

        model3dData.buildTriangles(mTextureDataHandle)


        GLES20.glEnable(GLES20.GL_CULL_FACE)
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        cameraComponent.calculateCameraDirection()

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0,
            cameraComponent.pos.x, cameraComponent.pos.y, cameraComponent.pos.z, // Position
            cameraComponent.direction.x, cameraComponent.direction.y, cameraComponent.direction.z, // Where to look
            0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val scratch = FloatArray(16)

        // Create a rotation transformation for the triangle
        /*val time = SystemClock.uptimeMillis() % 4000L
        val angle = 0.090f * time.toInt()*/
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        // Calculate position of the light
        Matrix.setIdentityM(mLightModelMatrix, 0)
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 3.0f, 5.0f)

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0)
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0)

        lineLight = Line(
            Vector3(0f, 0f, 0f),
            Vector3(mLightPosInWorldSpace[0], mLightPosInWorldSpace[1], mLightPosInWorldSpace[2]),
            RGBA(1f, 1f, 1f, 1f))

        // Draw stores triangles
        model3dData.draw(scratch, vPMatrix, mLightPosInEyeSpace)

        worldAxis.draw(scratch, vPMatrix, mLightPosInEyeSpace)
        lineLight.draw(scratch, vPMatrix, mLightPosInEyeSpace)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()


        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 100f)

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

        fun loadTexture(bitmap: Bitmap): Int {
            val textureHandle = IntArray(1)
            GLES20.glGenTextures(1, textureHandle, 0)

            if (textureHandle[0] != 0) {
                // Bind to the texture in OpenGL
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

                // Set filtering
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST
                )

                // Load the bitmap into the bound texture.
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

                // Recycle the bitmap, since its data has been loaded into OpenGL.
                bitmap.recycle()
            }

            if (textureHandle[0] == 0) {
                throw RuntimeException("Error loading texture.")
            }

            return textureHandle[0]
        }
    }
}
