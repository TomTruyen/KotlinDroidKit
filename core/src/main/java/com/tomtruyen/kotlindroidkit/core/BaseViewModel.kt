package com.tomtruyen.kotlindroidkit.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel: ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun launchLoading(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: CoroutineScope.() -> Unit
    ) = viewModelScope.launch(dispatcher) {
        _loading.tryEmit(true)
        block()
        _loading.tryEmit(false)
    }
}

