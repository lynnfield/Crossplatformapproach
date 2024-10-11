package com.genovich.cpa

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Cross platform approach",
    ) {
        App()
    }
}