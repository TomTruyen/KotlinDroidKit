package com.tomtruyen.kotlindroidkit.lazyscrollkit.extensions

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.tomtruyen.kotlindroidkit.lazyscrollkit.providers.ScrollKitItemProvider

fun ScrollKitItemProvider.getItemsSize(contentPaddingPx: Rect): Size =
    size.let {
        Size(
            width = it.width + contentPaddingPx.left + contentPaddingPx.right,
            height = it.height + contentPaddingPx.top + contentPaddingPx.bottom,
        )
    }