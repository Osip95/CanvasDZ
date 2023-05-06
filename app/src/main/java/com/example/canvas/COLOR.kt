package com.example.canvas

import androidx.annotation.ColorRes

enum class COLOR(
    @ColorRes
    val value: Int
) {

    BLACK(R.color.black),
    RED(R.color.red),
    BLUE(R.color.blue),
    GREEN(R.color.green),
    PINK(R.color.pink),
    TEAL(R.color.teal_700),
    PURPLE(R.color.purple_200);


    companion object {
        private val map = values().associateBy(COLOR::value)
        fun from(color: Int) = map[color] ?: BLACK
    }
}