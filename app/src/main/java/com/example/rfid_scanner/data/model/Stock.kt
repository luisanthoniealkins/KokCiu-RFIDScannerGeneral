package com.example.rfid_scanner.data.model

class Stock(
    var code: String,
    name: String?,
    var brand: String,
    var vehicleType: String,
    var unit: String,
    val availableStock: Int
) {
    val isNameNull = name == null
    val name = if (isNameNull) code else name

    var itemQuantity = 0
        private set

    val items = mutableSetOf<String>()

    val getScanQuantity
        get() = items.size

    fun resetTags() { items.clear() }

    fun addTags(item: String, quantity: Int) {
        if (items.contains(item)) return
        items.add(item)
        itemQuantity += quantity
    }

}

class StockRequirement(
    val stock: Stock,
    reqQuantity: Int
) {

    var reqQuantity = reqQuantity
        private set

    fun incQuantity(quantity: Int) { reqQuantity += quantity }
}