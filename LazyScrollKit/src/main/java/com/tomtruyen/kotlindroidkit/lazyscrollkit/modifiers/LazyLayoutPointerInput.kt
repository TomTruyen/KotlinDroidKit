package com.tomtruyen.kotlindroidkit.lazyscrollkit.modifiers

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import com.tomtruyen.kotlindroidkit.lazyscrollkit.ScrollKitState
import com.tomtruyen.kotlindroidkit.lazyscrollkit.models.ScrollKitScrollDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun Modifier.lazyLayoutPointerInput(
    state: ScrollKitState,
    scrollDirection: ScrollKitScrollDirection,
): Modifier = pointerInput(Unit) {
    val velocityTracker = VelocityTracker()
    coroutineScope {
        when (scrollDirection) {
            ScrollKitScrollDirection.BOTH -> detectDragGestures(
                onDragEnd = { onDragEnd(state, velocityTracker, scrollDirection, this) },
                onDrag = { change, dragAmount ->
                    onDrag(state, change, dragAmount, velocityTracker, this)
                }
            )

            ScrollKitScrollDirection.HORIZONTAL -> detectHorizontalDragGestures(
                onDragEnd = { onDragEnd(state, velocityTracker, scrollDirection, this) },
                onHorizontalDrag = { change, dragAmount ->
                    onDrag(state, change, Offset(dragAmount, 0f), velocityTracker, this)
                }
            )

            ScrollKitScrollDirection.VERTICAL -> detectVerticalDragGestures(
                onDragEnd = { onDragEnd(state, velocityTracker, scrollDirection, this) },
                onVerticalDrag = { change, dragAmount ->
                    onDrag(state, change, Offset(0f, dragAmount), velocityTracker, this)
                }
            )
        }
    }
}

private fun onDrag(
    state: ScrollKitState,
    change: PointerInputChange,
    dragAmount: Offset,
    velocityTracker: VelocityTracker,
    scope: CoroutineScope
) {
    change.consume()
    velocityTracker.addPosition(change.uptimeMillis, change.position)
    scope.launch {
        state.dragBy(dragAmount)
    }
}

private fun onDragEnd(
    state: ScrollKitState,
    velocityTracker: VelocityTracker,
    scrollDirection: ScrollKitScrollDirection,
    scope: CoroutineScope
) {
    var velocity = velocityTracker.calculateVelocity()
    velocity = when (scrollDirection) {
        ScrollKitScrollDirection.BOTH -> velocity
        ScrollKitScrollDirection.HORIZONTAL -> velocity.copy(velocity.x, 0f)
        ScrollKitScrollDirection.VERTICAL -> velocity.copy(0f, velocity.y)
    }
    scope.launch { state.flingBy(velocity) }
}