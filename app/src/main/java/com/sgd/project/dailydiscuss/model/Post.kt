package com.sgd.project.dailydiscuss.model

data class Post(
    val text: String= "",
    val author: User= User(),
    val timestamp: Long= 0L,
    val likedBy: ArrayList<String> = ArrayList()
)
