package com.tomtruyen.kotlindroidkit.lazyscrollkit.extensions

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection
import com.tomtruyen.kotlindroidkit.lazyscrollkit.ScrollKitState
import com.tomtruyen.kotlindroidkit.lazyscrollkit.providers.ScrollKitItemProvider
import com.tomtruyen.kotlindroidkit.lazyscrollkit.providers.ScrollKitLayoutPositionProviderImpl
import kotlinx.coroutines.CoroutineScope

fun ScrollKitLayoutPositionProviderImpl?.update(
    state: ScrollKitState,
    itemProvider: ScrollKitItemProvider,
    layoutDirection: LayoutDirection,
    size: Size,
    contentPaddingPx: Rect,
    scope: CoroutineScope,
): ScrollKitLayoutPositionProviderImpl =
    if (
        this != null &&
        this.items == itemProvider.items &&
        this.layoutDirection == layoutDirection &&
        this.size == size
    ) {
        this
    } else {
        ScrollKitLayoutPositionProviderImpl(itemProvider.items, layoutDirection, size).also {
            val itemsSize = itemProvider.getItemsSize(contentPaddingPx)
            val bounds = Rect(
                left = 0f,
                top = 0f,
                right = (itemsSize.width - size.width).coerceAtLeast(0f),
                bottom = (itemsSize.height - size.height).coerceAtLeast(0f)
            )
            state.updateBounds(it, bounds, scope)
        }
    }