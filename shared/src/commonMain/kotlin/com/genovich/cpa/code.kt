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

suspend fun <Item> SelectItemContext<Item>.selectItem(items: List<Item>): Item {
    return select(items)
}

interface SelectItemContext<Item> {
    suspend fun select(items: List<Item>): Item
}

suspend fun <Item> CreateItemContext<Item>.createItem(): Item {
    return create()
}

interface CreateItemContext<Item> {
    suspend fun create(): Item
}
