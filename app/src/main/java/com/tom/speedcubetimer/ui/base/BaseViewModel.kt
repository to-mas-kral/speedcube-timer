package com.tom.speedcubetimer.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed interface State {

    object None : State

    object Loading : State

    class Success(val any: Any? = null) : State

    class Failure(
        val error: Throwable,
        val repeat: () -> Unit,
    ) : State
}


abstract class BaseViewModel : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Default)

    private val _state = MutableStateFlow<State>(State.None)
    val state: StateFlow<State> = _state.asStateFlow()

    protected fun <Result> launch(
        state: MutableStateFlow<State>? = _state,
        block: (suspend CoroutineScope.() -> Result),
    ) = scope.launch(throwableHandler(state, block)) {

        // 1. Show loading
        state?.emit(State.Loading)

        // 2. Process operation
        val result = block() // suspend - last long

        // 3. Success
        state?.emit(State.Success(result))

    }

    private fun <Result> throwableHandler(
        state: MutableStateFlow<State>?,
        block: (suspend CoroutineScope.() -> Result),
    ) = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        state?.tryEmit(
            State.Failure(
                error = throwable,
                repeat = { repeat(state, block) },
            )
        )
    }

    private fun <Result> repeat(
        state: MutableStateFlow<State>?,
        block: (suspend CoroutineScope.() -> Result),
    ) {
        launch(state, block)
    }

}
