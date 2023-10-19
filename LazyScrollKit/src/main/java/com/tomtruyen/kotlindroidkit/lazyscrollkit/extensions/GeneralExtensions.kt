package com.tomtruyen.kotlindroidkit.lazyscrollkit.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun PaddingValues.toPx(): Rect {
    val layoutDirection = LocalLayoutDirection.current
    return LocalDensity.current.run {
        Rect(
            calculateLeftPadding(layoutDirection).toPx(),
            calculateTopPadding().toPx(),
            calculateRightPadding(layoutDirection).toPx(),
            calculateBottomPadding().toPx()
        )
    }
}
