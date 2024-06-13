package com.example.serverside.server.dataset

data class PathDC(
    val moveX:Float,
    val moveY:Float,
    val lineX:Float,
    val lineY:Float,
)

val pathDataSet = listOf(
    PathDC(200f, 200f, 200f, 800f),
    PathDC(200f, 400f, 200f, 900f),
    PathDC(900f, 400f, 300f, 400f),
    PathDC(400f, 400f, 400f, 200f),
)