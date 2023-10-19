@file:OptIn(ExperimentalFoundationApi::class)

package com.tomtruyen.kotlindroidkit.lazyscrollkit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.IntervalList
import androidx.compose.foundation.lazy.layout.MutableIntervalList
import androidx.compose.runtime.Composable
import com.tomtruyen.kotlindroidkit.lazyscrollkit.models.ScrollKitItemConfig

/**
 * Receiver scope which is used by [ScrollKitLayout].
 */
interface ScrollKitLayoutScope {

    /**
     * Adds a [count] of items.
     *
     * @param count The items count.
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    fun items(
        count: Int,
        layoutInfo: (index: Int) -> ScrollKitItemConfig,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable (index: Int) -> Unit
    )

    /**
     * Adds given [items].
     *
     * @param items The items to add to the [ScrollKitLayout].
     * @param layoutInfo The lambda to provide layout information of the single item.
     * @param key A factory of stable and unique keys representing the item. Using the same key
     * for multiple items is not allowed. Type of the key should be saveable via Bundle on Android.
     * If null is passed the position in the list will represent the key.
     * @param contentType A factory of the content types for the item. The item compositions of
     * the same type could be reused more efficiently. Note that null is a valid type and items of
     * such type will be considered compatible.
     * @param itemContent The content displayed by a single item.
     */
    fun <T> items(
        items: List<T>,
        layoutInfo: (item: T) -> ScrollKitItemConfig,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable (item: T) -> Unit
    ): Unit = items(
        count = items.size,
        layoutInfo = { index: Int -> layoutInfo(items[index]) },
        key = if (key != null) { index: Int -> key(items[index]) } else null,
        contentType = { index: Int -> contentType(items[index]) },
        itemContent = { index: Int -> itemContent(items[index]) },
    )
}

/**
 * Implementation of the [ScrollKitLayoutScope] with [IntervalList].
 */
internal class ScrollKitLayoutScopeImpl : ScrollKitLayoutScope {

    private val _intervals = MutableIntervalList<ScrollKitLayoutItemContent>()

    /**
     * Registered items in the [ScrollKitLayout].
     */
    val intervals: IntervalList<ScrollKitLayoutItemContent> = _intervals

    override fun items(
        count: Int,
        layoutInfo: (index: Int) -> ScrollKitItemConfig,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        _intervals.addInterval(
            count,
            ScrollKitLayoutItemContent(layoutInfo, key, contentType, itemContent)
        )
    }
}

class ScrollKitLayoutItemContent(
    val layoutInfo: (index: Int) -> ScrollKitItemConfig,
    val key: ((index: Int) -> Any)?,
    val contentType: (index: Int) -> Any?,
    val item: @Composable (index: Int) -> Unit
)