package com.example.simpleengine.utils

import com.example.simpleengine.shapes.FaceModel3d
import com.example.simpleengine.shapes.Triangle

class Model3dData {
    val vertexList = ArrayList<Vector3>()
    val vertexTextureList = ArrayList<Vector2>()
    val vertexNormalList = ArrayList<Vector3>()

    val faceList = ArrayList<FaceModel3d>()
    val triangles = ArrayList<Triangle>()

    fun buildTriangles(texture: Int): ArrayList<Triangle> {
        val rgba = RGBA(1f, 1f, 0f, 1f)

        faceList.forEach { face ->
            val nTriangles = face.vertexIndexList.size - 2
            for (i in 1..nTriangles)
                triangles.add(
                    Triangle(
                        vertexList[face.vertexIndexList[0]-1],
                        vertexList[face.vertexIndexList[i]-1],
                        vertexList[face.vertexIndexList[i+1]-1],
                        vertexTextureList[face.vertexTextureIndexList[0]-1],
                        vertexTextureList[face.vertexTextureIndexList[i]-1],
                        vertexTextureList[face.vertexTextureIndexList[i+1]-1],
                        vertexNormalList[face.vertexNormalIndexList[0]-1],
                        vertexNormalList[face.vertexNormalIndexList[i]-1],
                        vertexNormalList[face.vertexNormalIndexList[i+1]-1],
                        rgba, texture
                    )
                )
        }
        return triangles
    }

    fun draw(mvpMatrix: FloatArray, mvMatrix: FloatArray, lightPosInEyeSpace: FloatArray) {
        if (triangles.size > 0) {
            triangles.forEach {
                it.draw(mvpMatrix, mvMatrix, lightPosInEyeSpace)
            }
        }
    }
}
