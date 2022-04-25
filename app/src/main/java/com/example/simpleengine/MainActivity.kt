package com.example.simpleengine

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simpleengine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var gLView: GLSurfaceView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        gLView = MyGLSurfaceView(this)
        binding.glContainer.addView(gLView)

        setContentView(view)

        setControlsButtonsListeners()
    }

    private fun setControlsButtonsListeners() {
        binding.goForward.setOnClickListener {
            (gLView as MyGLSurfaceView).goForward()
        }
        binding.goBackward.setOnClickListener {
            (gLView as MyGLSurfaceView).goBackward()
        }

        binding.goLeft.setOnClickListener {
            (gLView as MyGLSurfaceView).goLeft()
        }

        binding.goRight.setOnClickListener {
            (gLView as MyGLSurfaceView).goRight()
        }

        binding.lookUp.setOnClickListener {
            (gLView as MyGLSurfaceView).lookUp()
        }
        binding.lookDown.setOnClickListener {
            (gLView as MyGLSurfaceView).lookDown()
        }

        binding.lookLeft.setOnClickListener {
            (gLView as MyGLSurfaceView).lookLeft()
        }

        binding.lookRight.setOnClickListener {
            (gLView as MyGLSurfaceView).lookRight()
        }
    }
}
