package com.genovich.consoleui

import com.genovich.components.OneOf
import com.genovich.cpa.CreateAndAddDependencies
import com.genovich.cpa.CreateOrRemoveDependencies
import com.genovich.cpa.ExampleAppDependencies
import com.genovich.cpa.SelectAndRemoveItemDependencies
import com.genovich.cpa.createAndAdd
import com.genovich.cpa.createOrRemove
import com.genovich.cpa.exampleApp
import com.genovich.cpa.selectAndRemoveItem
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

    val selectAndRemoveContext = object : SelectAndRemoveItemDependencies<String> {
        override suspend fun selectItem(items: List<String>): String {
            outputFlow.emit(
                items.withIndex().joinToString("\n") { (index, item) -> "$index. $item" })
            return inputFlow.filterIsInstance<OneOf.Second<Int>>()
                .mapNotNull { items.getOrNull(it.second) }
                .first()
        }

        override suspend fun removeItem(items: List<String>, item: String): List<String> =
            com.genovich.cpa.removeItem(items, item)
    }
    val createAndAddContext = object : CreateAndAddDependencies<String> {
        override suspend fun createItem(): String {
            return inputFlow.filterIsInstance<OneOf.First<String>>()
                .map { it.first }
                .first()
        }

        override suspend fun addItem(items: List<String>, item: String): List<String> =
            com.genovich.cpa.addItem(items, item)
    }

    val createOrRemoveContext = object : CreateOrRemoveDependencies<String> {
        override suspend fun selectAndRemoveItem(items: List<String>): List<String> =
            selectAndRemoveContext.selectAndRemoveItem(items)

        override suspend fun createAndAdd(items: List<String>): List<String> =
            createAndAddContext.createAndAdd(items)
    }

    val exampleAppContext = object : ExampleAppDependencies<String> {
        override suspend fun createOrRemove(items: List<String>): List<String> =
            createOrRemoveContext.createOrRemove(items)
    }

    runBlocking {
        launch(Dispatchers.Default) { exampleAppContext.exampleApp(emptyList()) }
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