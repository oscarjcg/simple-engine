package com.example.simpleengine.components

import com.example.simpleengine.shapes.Line
import com.example.simpleengine.utils.RGBA
import com.example.simpleengine.utils.Vector3

class WorldAxisComponent {
    lateinit var xAxis: Line
    lateinit var yAxis: Line
    lateinit var zAxis: Line

    fun initComponent() {
        val origin = Vector3(0f, 0f, 0f)
        val xCoor = Vector3(10f, 0f, 0f)
        val yCoor = Vector3(0f, 10f, 0f)
        val zCoor = Vector3(0f, 0f, 10f)
        val red = RGBA(1f, 0f, 0f, 1f)
        val green = RGBA(0f, 1f, 0f, 1f)
        val blue = RGBA(0f, 0f, 1f, 1f)
        xAxis = Line(origin, xCoor, red)
        yAxis = Line(origin, yCoor, green)
        zAxis = Line(origin, zCoor, blue)
    }

    fun draw(mvpMatrix: FloatArray, mvMatrix: FloatArray, lightPosInEyeSpace: FloatArray) {
        xAxis.draw(mvpMatrix, mvMatrix, lightPosInEyeSpace)
        yAxis.draw(mvpMatrix, mvMatrix, lightPosInEyeSpace)
        zAxis.draw(mvpMatrix, mvMatrix, lightPosInEyeSpace)
    }
}
