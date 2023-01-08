package org.jeonfeel.moeuibit2.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun AddLifecycleEvent(
    onStartAction: (() -> Unit)? = null,
    onResumeAction: (() -> Unit)? = null,
    onPauseAction: (() -> Unit)? = null,
    onStopAction: (() -> Unit)? = null,
    elseAction: (() -> Unit)? = null
) {
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> onStartAction?.let { it() }
            Lifecycle.Event.ON_RESUME -> onResumeAction?.let { it() }
            Lifecycle.Event.ON_PAUSE -> onPauseAction?.let { it() }
            Lifecycle.Event.ON_STOP -> onStopAction?.let { it() }
            else -> elseAction?.let { it() }
        }
    }
}