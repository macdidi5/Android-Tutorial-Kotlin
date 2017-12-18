package net.macdidi.atk

import android.graphics.Color

enum class Colors private constructor(val code: String) {

    LIGHTGREY("#BDBDBD"), BLUE("#33B5E5"), PURPLE("#AA66CC"),
    GREEN("#99CC00"), ORANGE("#FFBB33"), RED("#FF4444");

    fun parseColor(): Int {
        return Color.parseColor(code)
    }

}