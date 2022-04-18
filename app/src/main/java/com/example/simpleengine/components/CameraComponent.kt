package com.example.simpleengine.components

import com.example.simpleengine.utils.Vector3

class CameraComponent {
    private val CAMERA_SPEED_ANGLE = 10f

    var pos = Vector3(1f, 1f, 1f)
    var cameraAngleX = 0f // Left/right
    var cameraAngleY = 90f // Up/down
    var direction = Vector3(0f, 0f, 0f)

    fun calculateCameraDirection() {
        val r = 1
        // Calculate point which the camera is looking at. Consider camera in 0 0 0
        val t = cameraAngleX.toDouble() * Math.PI / 180.0
        val v = cameraAngleY.toDouble() * Math.PI / 180.0
        direction.z = r * Math.cos(t).toFloat() * Math.sin(v).toFloat()
        direction.x = r * Math.sin(t).toFloat() * Math.sin(v).toFloat()
        direction.y = r * Math.cos(v).toFloat()

        // Move point to camera global position
        direction.x += pos.x
        direction.y += pos.y
        direction.z += pos.z

        //Log.e("t v", "${t} ${v}")
        //Log.e("CameraDirection", "${cameraAngleX} ${cameraDirection.x} ${cameraDirection.y} ${cameraDirection.z}")
    }

    fun lookUp() {
        cameraAngleY -= CAMERA_SPEED_ANGLE
    }

    fun lookDown() {
        cameraAngleY += CAMERA_SPEED_ANGLE
    }

    fun lookLeft() {
        cameraAngleX += CAMERA_SPEED_ANGLE
    }

    fun lookRight() {
        cameraAngleX -= CAMERA_SPEED_ANGLE
    }
}
