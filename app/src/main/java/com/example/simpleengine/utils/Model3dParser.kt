package com.example.simpleengine.utils

import com.example.simpleengine.shapes.FaceModel3d
import java.io.File

const val TYPE_VERTEX = "v"
const val TYPE_FACE = "f"
const val TYPE_VERTEX_TEXTURE = "vt"
const val TYPE_VERTEX_NORMAL = "vn"

class ModelParser {
    companion object {
        fun parse(fileName: String): Model3dData {
            val model3dData = Model3dData()
            File(fileName).forEachLine { line ->
                val values = line.split(" ")

                when (values[0]) {
                    TYPE_VERTEX -> {
                        model3dData.vertexList.add(
                            Vector3(
                                values[1].toFloat(),
                                values[2].toFloat(),
                                values[3].toFloat()
                            )
                        )
                    }
                    TYPE_FACE -> {
                        val faceModel3d = FaceModel3d()
                        for (i in 1 until values.size) {
                            val components = values[i].split("/")
                            faceModel3d.vertexIndexList.add(components[0].toInt())
                            faceModel3d.vertexTextureIndexList.add(components[1].toInt())
                        }
                        model3dData.faceList.add(faceModel3d)
                    }
                    TYPE_VERTEX_TEXTURE -> {
                        model3dData.vertexTextureList.add(
                            Vector2(
                                values[1].toFloat(),
                                values[2].toFloat()
                            )
                        )
                    }
                    TYPE_VERTEX_NORMAL -> {
                        model3dData.vertexNormalList.add(
                            Vector3(
                                values[1].toFloat(),
                                values[2].toFloat(),
                                values[3].toFloat()
                            )
                        )
                    }
                }
            }

            return model3dData
        }
    }
}
