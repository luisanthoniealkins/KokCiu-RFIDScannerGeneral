package com.example.rfid_scanner.data.repository.helper

class RequestEndPoint {

    companion object {
        const val CHECK_IN_NEW = "checkInNew"
        const val CHECK_IN_OLD = "checkInOld"
        const val VALIDATE_BILL = "validateBill"
        const val GET_ALL_CUSTOMERS = "getAllCustomers"
        const val ADD_CUSTOMER = "addCustomer"
        const val EDIT_CUSTOMER = "editCustomer"
        const val GET_ALL_BRANDS = "getAllBrands"
        const val ADD_BRAND = "addBrand"
        const val EDIT_BRAND = "editBrand"
        const val GET_ALL_VEHICLE_TYPES = "getAllVehicleTypes"
        const val ADD_VEHICLE_TYPE = "addVehicleType"
        const val EDIT_VEHICLE_TYPE = "editVehicleType"
        const val GET_ALL_UNITS = "getAllUnits"
        const val ADD_UNIT = "addUnit"
        const val EDIT_UNIT = "editUnit"
        const val GET_ALL_STOCKS = "getAllStocks"
        const val GET_STOCKS = "getStocks"
        const val ADD_STOCK = "addStock"
        const val EDIT_STOCK = "editStock"
        const val GET_RFIDS = "getRFIDs"
        const val GET_RFID_DETAIL = "getRFIDDetail"
        const val CHECK_OUT = "checkOut"
        const val CLEAR_TAG = "clearTag"
        const val CHECK_OUT_BROKEN = "checkOutBroken"
        const val CHECK_SERVER = "checkServer"
        const val GET_ALL_STOCK_RFIDS = "getAllStockRFIDs"
        const val ADJUST_TAG = "adjustTag"
        const val GET_STOCK_CODE = "getStockCode"
        const val GET_STOCK_DETAIL = "getStockDetail"
        const val GET_STOCK_TRANSACTION = "getStockTransaction"
        const val GET_ALL_TRANSACTIONS = "getAllTransactions"
        const val GET_ALL_TRANSACTIONS_DATES = "getAllTransactionDates"
        const val ADD_STOCK_ID = "addStockId"
        const val GET_ALL_STOCK_IDS = "getAllStockIds"
        const val REUSE_TAG = "reuseTag"
    }

}