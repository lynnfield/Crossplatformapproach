package com.genovich.cpa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genovich.components.UiState
import com.genovich.components.showAndGetResult
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

object Logic

@Composable
@Preview
fun App(
    selectItemsFlow: MutableStateFlow<UiState<List<String>, String>?> = MutableStateFlow(null),
    createItemsFlow: MutableStateFlow<UiState<Unit, String>?> = MutableStateFlow(null),
) {
    MaterialTheme {
        // WARNING: don't do like this!!!
        //  Logic should not be in the composition!
        //  Ideally you should to run the logic outside of App() and provide selectItemsFlow and createItemsFlow as parameters
        //  Logic should be able to "live" longer than the UI
        LaunchedEffect(Logic) {
            val selectAndRemoveContext = object : SelectAndRemoveItemDependencies<String> {
                override suspend fun selectItem(items: List<String>): String {
                    return selectItemsFlow.showAndGetResult(items)
                }

                override suspend fun removeItem(items: List<String>, item: String): List<String> =
                    com.genovich.cpa.removeItem(items, item)
            }
            val createAndAddContext = object : CreateAndAddDependencies<String> {
                override suspend fun createItem(): String {
                    return createItemsFlow.showAndGetResult(Unit)
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

            exampleAppContext.exampleApp(emptyList())
        }

        Column {
            val selectItems by selectItemsFlow.collectAsState()
            selectItems?.also { (items, select) ->
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true,
                ) {
                    items(items.asReversed()) { item ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { select(item) }
                                .padding(16.dp),
                            text = item,
                        )
                    }
                }
            }

            val createItem by createItemsFlow.collectAsState()
            createItem?.also { (_, create) ->
                Row(Modifier.fillMaxWidth()) {
                    var value by remember(create) { mutableStateOf("") }
                    TextField(
                        modifier = Modifier.weight(1f),
                        value = value,
                        onValueChange = { value = it },
                    )
                    Button(
                        onClick = { create(value) },
                    ) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}