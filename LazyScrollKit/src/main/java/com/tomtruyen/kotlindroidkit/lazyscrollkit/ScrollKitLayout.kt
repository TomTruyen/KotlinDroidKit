package com.tomtruyen.kotlindroidkit.lazyscrollkit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.tomtruyen.kotlindroidkit.lazyscrollkit.extensions.getItemsSize
import com.tomtruyen.kotlindroidkit.lazyscrollkit.extensions.toPx
import com.tomtruyen.kotlindroidkit.lazyscrollkit.extensions.update
import com.tomtruyen.kotlindroidkit.lazyscrollkit.models.ScrollKitScrollDirection
import com.tomtruyen.kotlindroidkit.lazyscrollkit.modifiers.lazyLayoutPointerInput
import com.tomtruyen.kotlindroidkit.lazyscrollkit.providers.ScrollKitLayoutPositionProviderImpl
import com.tomtruyen.kotlindroidkit.lazyscrollkit.providers.rememberItemProvider
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollKitLayout(
    modifier: Modifier = Modifier,
    state: ScrollKitState = rememberLazyLayoutScrollState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    scrollDirection: ScrollKitScrollDirection = ScrollKitScrollDirection.BOTH,
    content: ScrollKitLayoutScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val contentPaddingPx = contentPadding.toPx()

    val itemProvider = rememberItemProvider(content)

    var positionProvider by remember { mutableStateOf<ScrollKitLayoutPositionProviderImpl?>(null) }

    LazyLayout(
        modifier = modifier
            .clipToBounds()
            .lazyLayoutPointerInput(state, scrollDirection),
        itemProvider = itemProvider,
    ) { constraints ->
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())

        positionProvider = positionProvider.update(
            state = state,
            itemProvider = itemProvider,
            layoutDirection = layoutDirection,
            size = size,
            contentPaddingPx = contentPaddingPx,
            scope = scope
        )

        val items = itemProvider.getItems(
            state.translateX.value,
            state.translateY.value,
            contentPaddingPx,
            size,
        )

        val placeables = items.map { (index, bounds) ->
            measure(
                index,
                Constraints.fixed(bounds.width.toInt(), bounds.height.toInt())
            ) to bounds.topLeft
        }

        val itemsSize = itemProvider.getItemsSize(contentPaddingPx)
        val width = min(itemsSize.width.toInt(), constraints.maxWidth)
        val height = min(itemsSize.height.toInt(), constraints.maxHeight)

        layout(width, height) {
            placeables.forEach { (itemPlaceables, position) ->
                itemPlaceables.forEach { placeable ->
                    placeable.placeRelative(
                        x = position.x.toInt(),
                        y = position.y.toInt(),
                    )
                }
            }
        }
    }
}