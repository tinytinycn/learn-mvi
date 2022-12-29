package view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import moe.tlaster.precompose.molecule.rememberPresenter
import viewmodel.CounterAction
import viewmodel.CounterPresenter
import viewmodel.CounterState
import viewmodel.NormalCounterViewModel

@Composable
fun Counter(
    state: CounterState,
    onIncrement: () -> Unit = {},
    onInput: (String) -> Unit = {}
) {
    Column {
        Text(text = state.count.toString())
        TextField(
            value = state.input,
            onValueChange = onInput
        )
        Button(
            onClick = {
                onIncrement()
            }
        ) {
            Text(text = "Increment")
        }
    }
}

/**
 * 常规使用方式（注入viewModel）
 * */
@Composable
fun CounterComponent(
    viewModel: NormalCounterViewModel = remember { NormalCounterViewModel() }
) {
    val state by viewModel.state.collectAsState(initial = CounterState(0, ""))

    Counter(
        state = state,
        onIncrement = viewModel::increment,
        onInput = viewModel::input
    )
}

/**
 * 新的使用方式（组合Presenter）
 * */
@Composable
fun NewCounterComponent() {
    // 1. 创建一个事件flow
    val channel = remember { Channel<CounterAction>() }
    val flow = remember(channel) { channel.consumeAsFlow() }
    // 2. 创建一个可组合的UI逻辑处理类 Presenter，传入一个事件流参数，抛出一个界面状态结果。
    val state = CounterPresenter(action = flow)
    // 3. 将状态向下传入组件，在组件定义的 lambda 块中，将事件抛给Presenter的事件flow
    Counter(
        state = state,
        onIncrement = {
            channel.trySend(CounterAction.Increment)
        },
        onInput = {
            channel.trySend(CounterAction.Input(it))
        }
    )
}

@Composable
fun rememberAction(): Pair<Channel<CounterAction>, Flow<CounterAction>> {
    // 1. 创建一个事件flow
    val channel = Channel<CounterAction>(Channel.UNLIMITED)
    return remember {
        channel to channel.consumeAsFlow()
    }
}

@Composable
fun rememberPresenter2(flow: Flow<CounterAction>): StateFlow<CounterState> {
    // 2. scope.launchMolecule 返回的是一个 StateFlow<T>
    return remember {
        CoroutineScope(Dispatchers.Main).launchMolecule(clock = RecompositionClock.Immediate) {
            CounterPresenter(action = flow)
        }
    }
}


/**
 * 使用Molecule的作用：将Presenter 可组合函数 和 Compose UI 可组合函数分开执行在不同的 Composition 上
 * 好处是 Presenter 的逻辑执行，不影响 UI 渲染。
 * */
@Composable
fun MoleculeCounterComponent() {
    // 1. 创建一个事件flow
    // 由于 state 发生变化，会让整个 MoleculeCounterComponent 组件 recomposition，因此需要 remember channel。
    val (channel, flow) = rememberAction()
    // 2. 返回的是一个 StateFlow<T>
    // 由于 state 发生变化，会让整个 MoleculeCounterComponent 组件 recomposition，因此需要 remember Presenter（viewModel）
    val presenter = rememberPresenter2(flow)
    // 3. 获取最新状态
    val state by presenter.collectAsState()
    // 4. 将状态向下传入组件
    Counter(
        state = state,
        onIncrement = {
            channel.trySend(CounterAction.Increment)
        },
        onInput = {
            channel.trySend(CounterAction.Input(it))
        }
    )
}

@Composable
fun PreComposeCounterComponent() {
    val (state, channel) = rememberPresenter { CounterPresenter(it) }

    Counter(
        state = state,
        onIncrement = {
            channel.trySend(CounterAction.Increment)
        },
        onInput = {
            channel.trySend(CounterAction.Input(it))
        }
    )
}