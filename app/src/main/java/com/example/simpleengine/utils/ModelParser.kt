package com.example.simpleengine.utils

import java.io.File

const val TYPE_VERTEX = "v"
const val TYPE_FACE = "f"

class ModelParser {
    companion object {
        fun parse(fileName: String): Model3dData {
            val modelData = Model3dData()
            File(fileName).forEachLine { line ->
                val values = line.split(" ")

                when (values[0]) {
                    TYPE_VERTEX -> {
                        modelData.vertexList.add(
                            Vector3(
                                values[1].toFloat(),
                                values[2].toFloat(),
                                values[3].toFloat()
                            )
                        )
                    }
                    TYPE_FACE -> {
                        val face = ArrayList<Int>()
                        for (i in 1 until values.size) {
                            val components = values[i].split("/")
                            face.add(components[0].toInt())
                        }
                        modelData.faceList.add(face)
                    }
                }
            }

            return modelData
        }
    }
}
