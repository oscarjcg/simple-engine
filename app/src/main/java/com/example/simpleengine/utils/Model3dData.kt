package com.example.simpleengine.utils

import com.example.simpleengine.shapes.Triangle

class Model3dData {
    val vertexList = ArrayList<Vector3>()
    val faceList = ArrayList<ArrayList<Int>>()
    val triangules = ArrayList<Triangle>()

    fun buildTriangles(): ArrayList<Triangle> {
        val rgba = RGBA(0f, 1f, 0f, 1f)
        faceList.forEach { face ->

            val nTriangules = face.size - 2
            for (i in 1..nTriangules)
                triangules.add(
                    Triangle(
                        vertexList[face[0]-1],
                        vertexList[face[i]-1],
                        vertexList[face[i+1]-1],
                        rgba)
                )
        }
        return triangules
    }

    fun draw(mvpMatrix: FloatArray) {
        if (triangules.size > 0) {
            triangules.forEach {
                it.draw(mvpMatrix)
            }
        }
    }
}
