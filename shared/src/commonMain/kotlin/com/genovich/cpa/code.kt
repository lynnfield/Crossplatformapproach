package com.genovich.cpa

import com.genovich.components.parallel
import com.genovich.components.updateLoop


class ExampleApp<Item : Any>(
    val createOrRemove: suspend (List<Item>) -> List<Item>,
) : suspend (List<Item>) -> Nothing {
    override suspend fun invoke(items: List<Item>): Nothing {
        updateLoop(items) {
            createOrRemove(it)
        }
    }
}

class CreateOrRemove<Item : Any>(
    val selectAndRemoveItem: suspend (List<Item>) -> List<Item>,
    val createAndAdd: suspend (List<Item>) -> List<Item>,
) : suspend (List<Item>) -> List<Item> {
    override suspend fun invoke(items: List<Item>): List<Item> {
        return parallel(
            { selectAndRemoveItem(items) },
            { createAndAdd(items) }
        )
    }
}

class SelectAndRemoveItem<Item : Any>(
    val selectItem: suspend (List<Item>) -> Item,
    val removeItem: suspend (List<Item>, Item) -> List<Item>,
) : suspend (List<Item>) -> List<Item> {
    override suspend fun invoke(items: List<Item>): List<Item> {
        val item = selectItem(items)

        return removeItem(items, item)
    }
}

fun <Item> removeItem(items: List<Item>, item: Item): List<Item> {
    return items - item
}

class CreateAndAdd<Item : Any>(
    val createItem: suspend () -> Item,
    val addItem: suspend (List<Item>, Item) -> List<Item>,
) : suspend (List<Item>) -> List<Item> {
    override suspend fun invoke(items: List<Item>): List<Item> {
        val item = createItem()

        return addItem(items, item)
    }
}

fun <Item> addItem(items: List<Item>, item: Item): List<Item> {
    return items + item
}