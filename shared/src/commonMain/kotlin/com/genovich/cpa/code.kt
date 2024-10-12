package com.genovich.cpa

import com.genovich.components.parallel
import com.genovich.components.updateLoop


suspend fun <Item> ExampleAppDependencies<Item>.exampleApp(items: List<Item>): Nothing {
    updateLoop(items) {
        createOrRemove(it)
    }
}

interface ExampleAppDependencies<Item> {
    suspend fun createOrRemove(items: List<Item>): List<Item>
}

suspend fun <Item> CreateOrRemoveDependencies<Item>.createOrRemove(items: List<Item>): List<Item> {
    return parallel(
        { selectAndRemoveItem(items) },
        { createAndAdd(items) }
    )
}

interface CreateOrRemoveDependencies<Item> {
    suspend fun selectAndRemoveItem(items: List<Item>): List<Item>
    suspend fun createAndAdd(items: List<Item>): List<Item>
}

suspend fun <Item> SelectAndRemoveItemDependencies<Item>.selectAndRemoveItem(items: List<Item>): List<Item> {
    val item = selectItem(items)

    return removeItem(items, item)
}

interface SelectAndRemoveItemDependencies<Item> {
    suspend fun selectItem(items: List<Item>): Item
    suspend fun removeItem(items: List<Item>, item: Item): List<Item>
}

suspend fun <Item> removeItem(items: List<Item>, item: Item): List<Item> {
    return items - item
}

suspend fun <Item> CreateAndAddDependencies<Item>.createAndAdd(items: List<Item>): List<Item> {
    val item = createItem()

    return addItem(items, item)
}

interface CreateAndAddDependencies<Item> {
    suspend fun createItem(): Item
    suspend fun addItem(items: List<Item>, item: Item): List<Item>
}

suspend fun <Item> addItem(items: List<Item>, item: Item): List<Item> {
    return items + item
}

suspend fun <Item> SelectItemDependencies<Item>.selectItem(items: List<Item>): Item {
    return select(items)
}

interface SelectItemDependencies<Item> {
    suspend fun select(items: List<Item>): Item
}

suspend fun <Item> CreateItemDependencies<Item>.createItem(): Item {
    return create()
}

interface CreateItemDependencies<Item> {
    suspend fun create(): Item
}
