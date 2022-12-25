package view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import viewmodel.DemoViewModel
import viewmodel.GenericViewModel
import viewmodel.UiAction

@Composable
fun DemoComponent() {
    // 状态(State) : name
    var name by remember { mutableStateOf("") }
    Column {
        // 对于 TextField 组件，状态向下(传入)，事件向上(抛出)，name 保存事件导致的最终状态或状态值。
        // 事件(Event) : onValueChange 指定事件动作action或intent
        // TextField 组件传入状态(State) name
        TextField(value = name, onValueChange = { name = it })
        Text(text = "Hello $name")
    }
}

@Composable
fun DemoComponentByMvi(
    // 1. 初始化ViewModel，它内部创建了一个状态流，放入一个初始状态。
    viewModel: DemoViewModel = remember { DemoViewModel() }
) {
    // 2. 捕获状态流中的一个状态 State
    val uiState = viewModel.uiState.collectAsState().value
    Column {
        // 3.1 更新UI界面，
        // 对于状态 State 而言，"外部被捕获状态"通过组件定义的参数，【向下】被传入进组件。
        // 对于事件 Intent(Action) 而言，"内部发生的事件"导致的更新值，通过组件定义的 lambda 参数，【向上】抛出给用户自定义的 ViewModel.<Action>.<XxxChanged>(it) 方法作为入参。
        TextField(value = uiState.name, onValueChange = {
            viewModel.handleAction(DemoViewModel.UiAction.NameChanged(it))
        })
        // 3.2 更新UI界面 text 的值(从状态中获取更新值)
        Text(text = "Hello mvi ${uiState.name}")
    }
}

@Composable
fun GenericComponentByMvi(
    viewModel: GenericViewModel = remember { GenericViewModel() }
) {
    val uiState = viewModel.container.uiState.collectAsState().value
    Column {
        TextField(value = uiState.name, onValueChange = {
            viewModel.handleAction(UiAction.NameChanged(it))
        })
        Text(text = "Hello my mvi ${uiState.name}")
    }
}

