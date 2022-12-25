package viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class GenericViewModel {

    val container = Container<UiState, OtherDetails>(CoroutineScope(Dispatchers.Default), initialState = UiState(""))

    fun handleAction(action: UiAction) = container.intent {
        when (action) {
            is UiAction.NameChanged -> reduce {
                copy(
                    name = action.name
                )
            }
            else -> {}
        }
    }

    fun onItemClicked(name: String) = container.intent {
        postSideEffect(OtherDetails(name))
    }
}

// 3. container
class Container<STATE, SIDE_EFFECT>(
    private val scope: CoroutineScope,
    initialState: STATE
) {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<STATE> = _uiState

    private val _sideEffect = Channel<SIDE_EFFECT>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun intent(transform: suspend Container<STATE, SIDE_EFFECT>.() -> Unit) {
        scope.launch {
            this@Container.transform()
        }
    }

    suspend fun reduce(reducer: STATE.() -> STATE) =
        withContext(SINGLE_THREAD) {
            _uiState.value = _uiState.value.reducer()
        }

    suspend fun postSideEffect(event: SIDE_EFFECT) =
        _sideEffect.send(event)

    companion object {
        private val SINGLE_THREAD = newSingleThreadContext("mvi")
    }
}

// 1. 状态State
data class UiState(val name: String)

data class OtherDetails(val name: String)

// 2. 事件
sealed class UiAction {
    class NameChanged(val name: String) : UiAction() // 当界面组件UI的输入框name的值发生变化时（改变输入框的值name）
    // ...
}