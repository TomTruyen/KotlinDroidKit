package com.tomtruyen.kotlindroidkit.lazyscrollkit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Velocity
import com.tomtruyen.kotlindroidkit.lazyscrollkit.providers.ScrollKitPositionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Creates a [ScrollKitState] that is remembered across compositions.
 *
 * @param initialOffset The lambda to provide initial offset on the plane.
 * @return Instance of the [ScrollKitState].
 */
@Composable
fun rememberLazyLayoutScrollState(
    initialOffset: ScrollKitPositionProvider.() -> Offset = { Offset.Zero }
): ScrollKitState {
    return remember { ScrollKitState(initialOffset) }
}

/**
 * A state object that can be hoisted to control and observe scrolling.
 *
 * @property initialOffset The lambda to provide initial offset on the plane.
 */
@Stable
class ScrollKitState(
    private val initialOffset: ScrollKitPositionProvider.() -> Offset
) {

    internal lateinit var translateX: Animatable<Float, AnimationVector1D>
    internal lateinit var translateY: Animatable<Float, AnimationVector1D>

    /**
     * The position provider used to get items offsets.
     */
    private lateinit var positionProvider: ScrollKitPositionProvider

    /**
     * Offset on the plane, if null state is not initialised.
     */
    private var translate: Translate? by mutableStateOf(null)
        private set

    /**
     * Updates bounds of the layout and initializes the position provider.
     *
     * @param positionProvider An instance of the position provider.
     * @param maxBounds The max size of the layout.
     */
    internal fun updateBounds(
        positionProvider: ScrollKitPositionProvider,
        maxBounds: Rect,
        coroutineScope: CoroutineScope,
    ) {
        this.positionProvider = positionProvider

        if (!::translateX.isInitialized && !::translateY.isInitialized) {
            val (x, y) = positionProvider.initialOffset()

            translateX = Animatable(x)
            translateY = Animatable(y)

            snapshotFlow { translateX.value }
                .onEach { updateTranslate() }
                .launchIn(coroutineScope)

            snapshotFlow { translateY.value }
                .onEach { updateTranslate() }
                .launchIn(coroutineScope)
        }

        translateX.updateBounds(
            lowerBound = maxBounds.left,
            upperBound = maxBounds.right,
        )
        translateY.updateBounds(
            lowerBound = maxBounds.top,
            upperBound = maxBounds.bottom,
        )

        updateTranslate()
    }

    private fun updateTranslate() {
        if (
            translate == null ||
            translateX.value != translate?.x ||
            translateY.value != translate?.y ||
            translateX.upperBound != translate?.maxX ||
            translateY.upperBound != translate?.maxY
        ) {
            translate = Translate(
                translateX.value,
                translateY.value,
                translateX.upperBound ?: 0f,
                translateY.upperBound ?: 0f
            )
        }
    }

    /**
     * Translates the current offset by the given value.
     *
     * @param value The value to translate by.
     */
    suspend fun dragBy(value: Offset) {
        coroutineScope {
            launch {
                translateX.snapTo(translateX.value - value.x)
            }
            launch {
                translateY.snapTo(translateY.value - value.y)
            }
        }
    }

    /**
     * Animates current offset to the new value.
     *
     * @param x The new offset on the X axis.
     * @param y The new offset on the Y axis.
     */
    suspend fun animateTo(x: Float = translateX.value, y: Float = translateY.value) {
        coroutineScope {
            launch {
                translateX.animateTo(x)
            }
            launch {
                translateY.animateTo(y)
            }
        }
    }

    /**
     * Snaps current offset to the new value.
     *
     * @param x The new offset on the X axis.
     * @param y The new offset on the Y axis.
     */
    suspend fun snapTo(x: Float = translateX.value, y: Float = translateY.value) {
        coroutineScope {
            launch {
                translateX.snapTo(x)
            }
            launch {
                translateY.snapTo(y)
            }
        }
    }

    /**
     * Flings current offset by the given velocity.
     *
     * @param velocity The velocity to fling by.
     */
    suspend fun flingBy(velocity: Velocity) {
        coroutineScope {
            launch {
                translateX.animateDecay(-velocity.x, exponentialDecay())
            }
            launch {
                translateY.animateDecay(-velocity.y, exponentialDecay())
            }
        }
    }

    /**
     * Stops current offset animations.
     */
    suspend fun stopAnimation() {
        coroutineScope {
            launch {
                translateX.stop()
            }
            launch {
                translateY.stop()
            }
        }
    }

    /**
     * Animates current offset to the item with a given index.
     *
     * @param index The global index of the item.
     * @param alignment The alignment to align item inside the [ScrollKitLayout].
     * @param paddingStart An additional start padding to tweak alignment.
     * @param paddingTop An additional top padding to tweak alignment.
     * @param paddingEnd An additional end padding to tweak alignment.
     * @param paddingBottom An additional bottom padding to tweak alignment.
     */
    suspend fun animateTo(
        index: Int,
        alignment: Alignment = Alignment.Center,
        paddingStart: Float = 0f,
        paddingTop: Float = 0f,
        paddingEnd: Float = 0f,
        paddingBottom: Float = 0f,
    ) {
        val offset = positionProvider.getOffset(
            index = index,
            alignment = alignment,
            paddingStart = paddingStart,
            paddingTop = paddingTop,
            paddingEnd = paddingEnd,
            paddingBottom = paddingBottom,
            currentX = translateX.value,
            currentY = translateY.value,
        )
        animateTo(offset.x, offset.y)
    }

    /**
     * Snaps current offset to the item with a given index.
     *
     * @param index The global index of the item.
     * @param alignment The alignment to align item inside the [ScrollKitLayout].
     * @param paddingStart An additional start padding to tweak alignment.
     * @param paddingTop An additional top padding to tweak alignment.
     * @param paddingEnd An additional end padding to tweak alignment.
     * @param paddingBottom An additional bottom padding to tweak alignment.
     */
    suspend fun snapTo(
        index: Int,
        alignment: Alignment = Alignment.Center,
        paddingStart: Float = 0f,
        paddingTop: Float = 0f,
        paddingEnd: Float = 0f,
        paddingBottom: Float = 0f,
    ) {
        val offset = positionProvider.getOffset(
            index = index,
            alignment = alignment,
            paddingStart = paddingStart,
            paddingTop = paddingTop,
            paddingEnd = paddingEnd,
            paddingBottom = paddingBottom,
            currentX = translateX.value,
            currentY = translateY.value
        )
        snapTo(offset.x, offset.y)
    }

    /**
     * Represents the offset on the plane.
     *
     * @property x Offset on the X axis in pixels.
     * @property y Offset on the Y axis in pixels.
     * @property maxX The max offset on on the X axis in pixels.
     * @property maxY The max offset on on the Y axis in pixels.
     */
    data class Translate(
        val x: Float,
        val y: Float,
        val maxX: Float,
        val maxY: Float,
    )
}