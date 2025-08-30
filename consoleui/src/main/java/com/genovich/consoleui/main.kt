package com.genovich.consoleui

import com.genovich.components.OneOf
import com.genovich.cpa.ExampleAppAssembly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    val outputFlow = MutableSharedFlow<String>(1)
    val inputFlow = MutableSharedFlow<OneOf<String, Int>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val exampleApp = ExampleAppAssembly(
        selectItem = { items ->
            outputFlow.emit(
                items.withIndex().joinToString("\n") { (index, item) -> "$index. $item" })
            inputFlow.filterIsInstance<OneOf.Second<Int>>()
                .mapNotNull { items.getOrNull(it.second) }
                .first()
        },
        createItem = {
            inputFlow.filterIsInstance<OneOf.First<String>>()
                .map { it.first }
                .first()
        }
    )

    runBlocking {
        launch(Dispatchers.Default) { exampleApp(emptyList()) }
        outputFlow.collectLatest { text ->
            println("Items:")
            println(text)
            print("Enter item number to delete or item name to add: ")

            val input = readln()

            inputFlow.emit(
                input.toIntOrNull()?.let { OneOf.Second(it) } ?: OneOf.First(input)
            )
        }
    }
}