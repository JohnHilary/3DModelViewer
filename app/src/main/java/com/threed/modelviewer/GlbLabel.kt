package com.threed.modelviewer

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class GlbLabel(
    val text: String,
    val nodeIndex: Int,
    val translationX: Float,
    val translationY: Float,
    val translationZ: Float
)

object GlbLabelReader {

    fun readLabels(
        context: Context,
        assetPath: String
    ): List<GlbLabel> {
        val bytes = context.assets.open(assetPath).use {
            it.readBytes()
        }

        val buffer = ByteBuffer.wrap(bytes)
            .order(ByteOrder.LITTLE_ENDIAN)

        val magic = buffer.int
        buffer.int
        buffer.int

        if (magic != 0x46546C67) {
            return emptyList()
        }

        var jsonText = ""

        while (buffer.remaining() >= 8) {
            val chunkLength = buffer.int
            val chunkType = buffer.int

            if (chunkLength > buffer.remaining()) {
                break
            }

            val chunkData = ByteArray(chunkLength)
            buffer.get(chunkData)

            if (chunkType == 0x4E4F534A) {
                jsonText = String(chunkData, Charsets.UTF_8).trim()
                break
            }
        }

        if (jsonText.isEmpty()) {
            return emptyList()
        }

        val root = JSONObject(jsonText)
        val nodes = root.optJSONArray("nodes") ?: JSONArray()

        val labels = mutableListOf<GlbLabel>()

        for (index in 0 until nodes.length()) {
            val node = nodes.optJSONObject(index) ?: continue
            val extras = node.optJSONObject("extras") ?: continue
            val prop = extras.optString("prop", "")

            if (prop.isEmpty()) {
                continue
            }

            val translation = node.optJSONArray("translation")

            val x = translation?.optDouble(0, 0.0)?.toFloat() ?: 0f
            val y = translation?.optDouble(1, 0.0)?.toFloat() ?: 0f
            val z = translation?.optDouble(2, 0.0)?.toFloat() ?: 0f

            labels.add(
                GlbLabel(
                    text = prop,
                    nodeIndex = index,
                    translationX = x,
                    translationY = y,
                    translationZ = z
                )
            )
        }

        return labels
    }
}