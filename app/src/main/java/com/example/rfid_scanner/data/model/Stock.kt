package com.example.rfid_scanner.data.model

class Stock(
    var code: String,
    name: String? = null,
    var brand: String = "-",
    var vehicleType: String = "-",
    var unit: String = "-",
    val availableStock: Int = 0,
) {
    val isNameNull = name == null
    val name = if (isNameNull) code else name

    var itemQuantity = 0
        private set

    val items = mutableSetOf<String>()

    val getScanQuantity
        get() = items.size

    fun resetItem() {
        items.clear()
        itemQuantity = 0
    }

    fun addItem(tags: String, quantity: Int) {
        if (items.contains(tags)) return
        items.add(tags)
        itemQuantity += quantity
    }

    fun copy() = Stock(code, name, brand, vehicleType, unit, availableStock)
}

class StockRequirement(
    val stock: Stock,
    reqQuantity: Int
) {
    var reqQuantity = reqQuantity
        private set

    fun incQuantity(quantity: Int) { reqQuantity += quantity }

    fun copy() = StockRequirement(stock.copy(), reqQuantity)
}

data class StockId(
    val stock: Stock,
    val id: String,
    val unitCount: Int,
) {
    companion object {
        fun getStockIdFromId(id: String): StockId {
            val arrs = id.split("#Q")
            return StockId(
                stock = Stock(code = arrs[0]),
                id = id,
                unitCount = arrs[1].toInt(),
            )
        }
    }
}