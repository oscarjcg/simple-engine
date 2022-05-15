package com.example.simpleengine.shapes

import android.opengl.GLES20
import com.example.simpleengine.MyGLRenderer.Companion.loadShader
import com.example.simpleengine.utils.RGBA
import com.example.simpleengine.utils.Vector2
import com.example.simpleengine.utils.Vector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


// number of coordinates per vertex in this array
private const val COORDS_PER_VERTEX = 3

class Triangle(
    p1: Vector3, p2: Vector3, p3: Vector3,
    vt1: Vector2, vt2: Vector2, vt3: Vector2,
    vn1: Vector3, vn2: Vector3, vn3: Vector3,
    rgba: RGBA, texture: Int) : IShape {
    private var triangleCoords = floatArrayOf(
        p1.x, p1.y, p1.z,
        p2.x, p2.y, p2.z,
        p3.x, p3.y, p3.z,
    )

    private var triangleTextureCoords = floatArrayOf(
        vt1.x, vt1.y,
        vt2.x, vt2.y,
        vt3.x, vt3.y,
    )

    private var triangleNormals = floatArrayOf(
        vn1.x, vn1.y, vn1.z,
        vn2.x, vn2.y, vn2.z,
        vn3.x, vn3.y, vn3.z,
    )

    private var texture = texture


    private val vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
        "uniform mat4 uMVMatrix;" +

        "attribute vec2 a_TexCoordinate;" + // Per-vertex texture coordinate information we will pass in.
        "varying vec2 v_TexCoordinate;" +   // This will be passed into the fragment shader.
        "attribute vec4 a_Position;" +
        "attribute vec3 a_Normal;" +
        "uniform vec4 a_Color;" +
        "varying vec4 v_Color;" +
        "varying vec3 v_Position;" +
        "varying vec3 v_Normal;" +
            "void main() {" +
            // Transform the vertex into eye space.
            "v_Position = vec3(uMVMatrix * a_Position);" +
            // Pass through the color.
            "v_Color = a_Color;" +
            // Transform the normal's orientation into eye space.
            "v_Normal = vec3(uMVMatrix * vec4(a_Normal, 0.0));" +
            // gl_Position is a special variable used to store the final position.
            // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
            "gl_Position = uMVMatrix * a_Position;" +
    "}"

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private var vMVMatrixHandle: Int = 0

    private val fragmentShaderCode =
        "precision mediump float;" +
        //"uniform sampler2D u_Texture;" +    // The input texture.
        //"varying vec2 v_TexCoordinate;" + // Interpolated texture coordinate per fragment.
        "varying vec4 v_Color;" +
        "uniform vec3 u_LightPos;" +      // The position of the light in eye space.
        "varying vec3 v_Position;" +      // Interpolated position for this fragment.
        "varying vec3 v_Normal;" +         // Interpolated normal for this fragment.
        "void main() {" +
            // Will be used for attenuation.
            "float distance = length(u_LightPos - v_Position);" +
            // Get a lighting direction vector from the light to the vertex.
            "vec3 lightVector = normalize(u_LightPos - v_Position);" +
            // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
            // pointing in the same direction then it will get max illumination.
            "float diffuse = max(dot(v_Normal, lightVector), 0.1);" +
            // Add attenuation.
            "diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));" +
            // Multiply the color by the diffuse illumination level to get final output color.
            "gl_FragColor = v_Color * diffuse;" +

            //"  gl_FragColor = v_Color;" +
            // Multiply the color by the texture value to get final output color.
            //"gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
            "}"

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(rgba.r, rgba.g, rgba.b, rgba.a)

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var uvBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleTextureCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleTextureCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var vnBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleNormals.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleNormals)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var mProgram: Int

    init {

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
    }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var normalHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    override fun draw(mvpMatrix: FloatArray, mvMatrix: FloatArray, lightPosInEyeSpace: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)

        //val a_texCoord = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate")
        //val u_texture = GLES20.glGetUniformLocation(mProgram, "u_Texture")

        // Set the active texture unit to texture unit 0.
        //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //GLES20.glEnableVertexAttribArray(a_texCoord)
        //GLES20.glEnableVertexAttribArray(u_texture)

        // Prepare the texture coordinates
        //GLES20.glVertexAttribPointer(a_texCoord, 2, GLES20.GL_FLOAT, false, 0, uvBuffer)

        // Set the sampler texture unit to where we have saved the texture.
        // second param is the active texture index
        //GLES20.glUniform1i(u_texture, 0)


        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "a_Color").also { colorHandle ->

            // Set color for drawing the triangle
            GLES20.glUniform4fv(colorHandle, 1, color, 0)
        }

        val a_Normal = GLES20.glGetAttribLocation(mProgram, "a_Normal")

        GLES20.glEnableVertexAttribArray(a_Normal)
        GLES20.glVertexAttribPointer(a_Normal, 3, GLES20.GL_FLOAT, false, 12, vnBuffer)


        val lightHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos").also {
            // Enable a handle
            GLES20.glEnableVertexAttribArray(it)

            GLES20.glUniform3f(it, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
        }

        // get handle to vertex shader's a_Position member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to shape's transformation matrix
            vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

            vMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix")
            GLES20.glUniformMatrix4fv(vMVMatrixHandle, 1, false, mvMatrix, 0)

            // Draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}
