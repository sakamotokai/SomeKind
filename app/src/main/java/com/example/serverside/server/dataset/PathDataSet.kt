package com.example.serverside.server.dataset

import android.graphics.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class PathDC(
    val moveX:Float,
    val moveY:Float,
    val lineX:Float,
    val lineY:Float,
)

val pathDataSet = listOf(
    PathDC(200f, 200f, 400f, 200f),
    PathDC(400f, 200f, 400f, 200f),
    PathDC(400f, 200f, 400f, 400f),
    PathDC(400f, 400f, 400f, 200f),
)