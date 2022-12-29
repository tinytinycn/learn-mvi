package viewmodel

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

/**
 * 常规的 viewModel
 * */
class NormalCounterViewModel() {
    // 页面上状态由一个或多个flow组成
    private val _count = MutableStateFlow(0)
    private val _input = MutableStateFlow("")

    // 暴露给UI层只有一个flow，UI层需要订阅这个flow
    // BUT，随着业务复杂度提升，combine 函数的入参会越来越多。[1]
    val state = combine(
        _count,
        _input
    ) { count, input ->
        CounterState(
            count = count,
            input = input
        )
    }

    fun increment() {
        _count.value++
    }

    fun input(value: String) {
        _input.value = value
    }
}

/**
 * 为了解决 [1] 处，combine 函数入参的问题。
 * 使用 Composable 可组合函数的方式，来实现 viewModel。
 *
 * 原理：当 count 被改变的时候，就会触发一次 recomposition，
 *      CounterPresenter 就会返回一个新的 CounterState，而这一点特性恰巧和 Flow 非常相似
 * */
@Composable
fun CounterPresenter(
    action: Flow<CounterAction>
): CounterState {
    var count by remember { mutableStateOf(0) }
    var input by remember { mutableStateOf("") }

    // 启动一个 effect 处理事件flow
    LaunchedEffect(action) {
        action.collect() { action ->
            when (action) {
                is CounterAction.Increment -> count++
                is CounterAction.Input -> input = action.value
            }
        }
    }

    return CounterState(
        count = count,
        input = input
    )
}

sealed interface CounterAction {
    object Increment : CounterAction
    class Input(val value: String) : CounterAction
}

data class CounterState(val count: Int, val input: String)
