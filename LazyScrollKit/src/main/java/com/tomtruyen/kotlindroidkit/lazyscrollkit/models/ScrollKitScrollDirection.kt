package com.tomtruyen.kotlindroidkit.lazyscrollkit.models

/**
 * Determines which directions are allowed to scroll.
 */
enum class ScrollKitScrollDirection {
    /**
     * Both horizontal and vertical scroll gestures are allowed.
     */
    BOTH,

    /**
     * Only horizontal scroll gestures are allowed, useful for LazyRow-ish layouts.
     */
    HORIZONTAL,

    /**
     * Only vertical scroll gestures are allowed, useful for LazyColumn-ish layouts.
     */
    VERTICAL
}