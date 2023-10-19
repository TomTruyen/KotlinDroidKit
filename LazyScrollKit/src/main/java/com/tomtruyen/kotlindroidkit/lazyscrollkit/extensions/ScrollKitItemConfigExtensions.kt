package com.tomtruyen.kotlindroidkit.lazyscrollkit.extensions

import androidx.compose.ui.geometry.Rect
import com.tomtruyen.kotlindroidkit.lazyscrollkit.models.ScrollKitItemConfig

fun ScrollKitItemConfig.overlaps(other: Rect): Boolean {
    if (lockOnVerticalScroll && lockOnHorizontalScroll) {
        return true
    }

    val width = width.resolve(other.width)
    val height = height.resolve(other.height)
    fun overlapsHorizontally(): Boolean = x + width > other.left && x < other.right
    fun overlapsVertically(): Boolean = y + height > other.top && y < other.bottom

    return if (lockOnHorizontalScroll) {
        overlapsVertically()
    } else if (lockOnVerticalScroll) {
        overlapsHorizontally()
    } else {
        overlapsHorizontally() && overlapsVertically()
    }
}

fun ScrollKitItemConfig.translate(
    translateX: Float,
    translateY: Float,
    contentPadding: Rect,
    viewport: Rect
): Rect {
    val itemTranslateX = translateX.takeUnless { lockOnHorizontalScroll } ?: 0f
    val itemTranslateY = translateY.takeUnless { lockOnVerticalScroll } ?: 0f
    val newX = this.x - itemTranslateX + contentPadding.left
    val newY = this.y - itemTranslateY + contentPadding.top
    val width = width.resolve(viewport.width - contentPadding.left - contentPadding.right)
    val height = height.resolve(viewport.height - contentPadding.top - contentPadding.bottom)
    return Rect(
        left = newX,
        top = newY,
        right = newX + width,
        bottom = newY + height
    )
}