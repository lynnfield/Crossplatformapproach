package com.genovich.cpa

fun <Item : Any> ExampleAppAssembly(
    selectItem: suspend (List<Item>) -> Item,
    createItem: suspend () -> Item,
): ExampleApp<Item> {
    return ExampleApp(
        createOrRemove = CreateOrRemoveAssembly(
            selectItem = selectItem,
            createItem = createItem,
        )
    )
}

fun <Item : Any> CreateOrRemoveAssembly(
    selectItem: suspend (List<Item>) -> Item,
    createItem: suspend () -> Item,
): CreateOrRemove<Item> {
    return CreateOrRemove(
        selectAndRemoveItem = SelectAndRemoveItemAssembly(selectItem = selectItem),
        createAndAdd = CreateAndAddAssembly(createItem = createItem),
    )
}

fun <Item : Any> SelectAndRemoveItemAssembly(
    selectItem: suspend (List<Item>) -> Item,
): SelectAndRemoveItem<Item> {
    return SelectAndRemoveItem(
        selectItem = selectItem,
        removeItem = ::removeItem,
    )
}

fun <Item : Any> CreateAndAddAssembly(
    createItem: suspend () -> Item,
): CreateAndAdd<Item> {
    return CreateAndAdd(
        createItem = createItem,
        addItem = ::addItem,
    )
}