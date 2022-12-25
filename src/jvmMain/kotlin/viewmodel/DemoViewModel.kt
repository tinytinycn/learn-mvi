package viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 符合 MVI 架构的 ViewModel 实现
 *
 * 1. 内部定义状态State
 * 2. 内部创建状态流flow
 * 3. 内部定义事件Intent和事件处理
 * */
class DemoViewModel {
    // 1. 定义"与ViewModel绑定UI界面相关的"UI【状态State】
    data class UiState(val name: String)

    // 2.1 创建一个状态流flow
    private val _uiState = MutableStateFlow(UiState(name = ""))

    // 2.2 将此可变状态流表示为只读状态流。暴露给外部使用。
    val uiState = _uiState.asStateFlow()

    // 3.1 定义"与ViewModel绑定UI界面相关的"UI【事件intent/action】类型
    sealed class UiAction {
        class NameChanged(val name: String) : UiAction() // 当界面组件UI的输入框name的值发生变化时（改变输入框的值name）
        // ...
    }

    // 3.2 定义有几种事件类型的处理过程(一般情况下，是处理状态值的过程)
    fun handleAction(action: UiAction) {
        when (action) {
            is UiAction.NameChanged -> {
                _uiState.value = _uiState.value.copy(
                    name = action.name
                )
            }
            else -> {}
        }
    }
}