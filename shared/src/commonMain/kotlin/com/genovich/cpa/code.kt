package com.genovich.cpa

import com.genovich.components.parallel
import com.genovich.components.updateLoop


suspend fun <Item> exampleApp(items: List<Item>): Nothing {
    updateLoop(items) {
        createOrRemove(it)
    }
}

suspend fun <Item> createOrRemove(items: List<Item>): List<Item> {
    return parallel(
        { selectAndRemoveItem(items) },
        { createAndAdd(items) }
    )
}

suspend fun <Item> selectAndRemoveItem(items: List<Item>): List<Item> {
    val item = selectItem(items)

    return removeItem(items, item)
}

suspend fun <Item> removeItem(items: List<Item>, item: Item): List<Item> {
    return items - item
}

suspend fun <Item> createAndAdd(items: List<Item>): List<Item> {
    val item: Item = createItem()

    return addItem(items, item)
}

suspend fun <Item> addItem(items: List<Item>, item: Item): List<Item> {
    return items + item
}

suspend fun <Item> selectItem(items: List<Item>): Item {
    TODO("Interact with user")
}

suspend fun <Item> createItem(): Item {
    TODO("Interact with user")
}
