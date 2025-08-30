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
            val exampleApp = ExampleAppAssembly(
                selectItem = { items -> selectItemsFlow.showAndGetResult(items) },
                createItem = { createItemsFlow.showAndGetResult(Unit) },
            )

            exampleApp(emptyList())
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