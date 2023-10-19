package com.tomtruyen.kotlindroidkit.lazyscrollkit.models

data class ScrollKitItemConfig(
    val x: Float,
    val y: Float,
    val width: Value,
    val height: Value,
    val lockOnHorizontalScroll: Boolean = false,
    val lockOnVerticalScroll: Boolean = false,
) {
    sealed interface Value {
        data class Absolute(val value: Float) : Value
        data class MatchParent(val fraction: Float) : Value

        fun resolve(parentSize: Float = 0f): Float {
            return when (this) {
                is Absolute -> value
                is MatchParent -> parentSize * fraction
            }
        }
    }
}
