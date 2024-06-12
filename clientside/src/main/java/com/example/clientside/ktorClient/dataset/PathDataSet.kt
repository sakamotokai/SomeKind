package com.example.clientside.ktorClient.dataset

import kotlinx.serialization.Serializable

@Serializable
data class PathDC(
    val moveX:Float,
    val moveY:Float,
    val lineX:Float,
    val lineY:Float,
)

/*
fun fromJsonToPathDC(json:String):List<PathDC>{

}*/
