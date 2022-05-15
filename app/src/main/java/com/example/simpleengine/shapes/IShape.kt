package com.example.simpleengine.shapes

interface IShape {
    fun draw(mvpMatrix: FloatArray, mvMatrix: FloatArray, lightPosInEyeSpace: FloatArray)
}
