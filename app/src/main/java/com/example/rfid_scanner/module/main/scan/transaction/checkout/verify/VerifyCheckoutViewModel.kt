package com.example.rfid_scanner.module.main.scan.transaction.checkout.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockRequirement
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.Tag.Companion.isProperTag
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder.Companion.addErrorTag
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder.Companion.addStockTag
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.StockViewHolder
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.StockViewHolder.Companion.addTagsToStock
import com.example.rfid_scanner.module.main.scan.transaction.general.verify.adapter.VerifyTagAdapter
import com.example.rfid_scanner.utils.extension.StringExt.getSimilarStringsTo
import com.example.rfid_scanner.utils.extension.StringExt.isSimilarTo
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import com.example.rfid_scanner.utils.helper.LogHelper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class VerifyCheckoutViewModel : ScanViewModel() {

    companion object {
        const val TAB_STOCK = 0
        const val TAB_TAG = 1
        const val TAB_ERROR = 2
    }

    private val _tagCountStock = MutableLiveData<Int>()
    val tagCountStock : LiveData<Int> = _tagCountStock

    private val _tagCountTag = MutableLiveData<Int>()
    val tagCountTag : LiveData<Int> = _tagCountTag

    private val _tagCountError = MutableLiveData<Int>()
    val tagCountError : LiveData<Int> = _tagCountError

    val stockAdapter = StockViewHolder().getAdapter()
    lateinit var tagAdapter : VerifyTagAdapter
    val errorAdapter = ErrorViewHolder().getAdapter()

    private val channelTags = Channel<List<TagEPC>>()
    private var activeStocks = mutableSetOf<String>()
    private var queriedTags = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { queryTags(it) } }
        }
    }

    /**
     *
     * INGAT FIXXXXX INI JIJIK SEKALI
     */

    fun setStocks(verifyStocks: List<StockRequirement>) {
        val tags = mutableListOf<VerifyTagAdapter.VerifyTagData>()

        verifyStocks.map {
            activeStocks.add(it.stock.code)
            it.stock.items.map { it2 -> tags.add(VerifyTagAdapter.VerifyTagData(Tag(it2, it.stock.name), false)) }
        }

        verifyStocks.map {
            stockAdapter.mapOfOperations[StockViewHolder.addOrUpdateStock]?.let { func ->
                (func as ((StockRequirement) -> Unit))(it.copy())
            }
        }

        tagAdapter = VerifyTagAdapter(tags)
        _tagCountTag.postValue(tagAdapter.itemCount)
        _tagCountStock.postValue(stockAdapter.itemCount)
    }

    private fun queryTags(tags: List<TagEPC>) {
        if (tags.isEmpty()) return
        viewModelScope.launch {
            tags.filter { !queriedTags.contains(it.epc) }.let {
                VolleyRepository.getI().requestAPI(
                    RequestEndPoint.GET_RFIDS,
                    RequestParam.getRFIDS(it),
                    RequestResult::getRFIDs
                ).collect{ res ->
                    res.response?.data?.let { tgs ->
                        val infoTags = tgs as List<Tag>
                        preProcessTags(infoTags)
                    }
                }
            }
        }
    }

    private fun preProcessTags(infoTags: List<Tag>) {
        val newTags = infoTags.filter { !queriedTags.contains(it.epc) }
        newTags.map { queriedTags.add(it.epc) }
        splitTags(newTags)
    }

    private fun splitTags(tags: List<Tag>) {
        tags.map {
            if (it.status == Tag.STATUS_STORED && activeStocks.contains(it.stockCode) && it.epc.isProperTag()) {
                stockAdapter.mapOfOperations[addTagsToStock]?.let { func -> (func as ((Tag) -> Unit))(it) }
                errorAdapter.mapOfOperations[addStockTag]?.let { func -> (func as ((Tag) -> Unit))(it) }
            } else {
                errorAdapter.mapOfOperations[addErrorTag]?.let { func -> (func as ((Tag) -> Unit))(it) }
                _tagCountError.postValue(errorAdapter.itemCount)
            }
        }
        tagAdapter.addData(tags)
        _tagCountTag.postValue(tagAdapter.itemCount)
    }

    override fun resetTags() {
        queriedTags.clear()
        stockAdapter.mapOfOperations[StockViewHolder.clearStockTags]?.let { func -> (func as (() -> Unit))() }
        tagAdapter.resetData()
        errorAdapter.mapOfOperations[ErrorViewHolder.clearErrorTags]?.let { func -> (func as (() -> Unit))() }
        _tagCountTag.postValue(tagAdapter.itemCount)
        _tagCountError.postValue(errorAdapter.itemCount)
    }

    fun getAdapterError(): String? {
        stockAdapter.getError()?.let { return it }
        errorAdapter.getError()?.let { return it }
        if (!tagAdapter.isAllVerified()) return "Terdapat tag yang belum dipindai"
        return null
    }

    fun setShowingOK(b: Boolean) {
        tagAdapter.setShowingOK(b)
        _tagCountTag.postValue(tagAdapter.itemCount)
    }


    // COPY FROM TransCheckoutViewModel
    fun checkUnknownTagsError(): Pair<String, Boolean> {
        val sTags = stockAdapter.dataSet.flatMap { it.stock.items.map { item -> Pair(item, it.stock.name) } }
        val uTags = errorAdapter.getUnknownTags()

        var errorTextPotentialSimilar = ""
        var errorTextUnknown = ""

        uTags.map { uTag ->
            val similarStrings = sTags.filter { it.first.isSimilarTo(uTag) }.joinToString(separator = "\n") { "[${it.second}]\n${it.first}" }
            if (similarStrings.isEmpty()) {
                errorTextUnknown += uTag
            } else {
                errorTextPotentialSimilar += uTag + "\n" +
                        "mirip dengan\n" +
                        similarStrings + "\n\n"
            }
        }

        return if (errorTextUnknown.isNotEmpty()) Pair(errorTextUnknown, false)
        else Pair(errorTextPotentialSimilar, true)
    }

}