// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import view.DemoComponent
import view.DemoComponentByMvi
import view.GenericComponentByMvi

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column {
            // 自定义组件
            DemoComponent()
            // 符合 mvi 架构自定义组件
            DemoComponentByMvi()
            // 极简的符合 mvi 架构的自定义组件
            GenericComponentByMvi()
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
