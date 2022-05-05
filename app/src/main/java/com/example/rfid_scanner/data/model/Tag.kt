package com.example.rfid_scanner.data.model

data class TagEPC(val epc: String)

data class TagEPCQty(val epc: String, var quantity: Int)

class Tag(
    val epc: String,
    val status: String,
    val stockId: String?,
    val stockName: String?,
    val stockUnitCount: Int?
) {

    companion object {
        const val STATUS_STORED = "Gudang"
        const val STATUS_SOLD = "Terjual"
        const val STATUS_BROKEN = "Rusak"
        const val STATUS_LOST = "Hilang"
        const val STATUS_UNKNOWN = "Asing"
    }

    val stockCode
        get() =
            if (stockId != null && stockId.split("#Q").size == 2) stockId.split("#Q")[0]
            else null

}