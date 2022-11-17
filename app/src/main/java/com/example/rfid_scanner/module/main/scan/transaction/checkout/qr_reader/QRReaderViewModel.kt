package com.example.rfid_scanner.module.main.scan.transaction.checkout.qr_reader
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Bill
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import kotlinx.coroutines.launch

class QRReaderViewModel : BaseViewModel() {

    companion object {
        const val KEY_BILL = "keyBill"
    }

    data class ErrorMessage(val title: String, val detail: String)
    private val _lvErrorMessage = MutableLiveData<ErrorMessage>()
    val lvErrorMessage : LiveData<ErrorMessage> = _lvErrorMessage

    private val _lvConfiguredBill = MutableLiveData<Bill>()
    val lvConfiguredBill : LiveData<Bill> = _lvConfiguredBill

    private val rBills = mutableListOf<String>()
    private var rCustomerCode : String? = null

    fun registerBills(bills: Array<String>?, customerCode: String) {
        bills?.let { rBills.addAll(it) }
        rCustomerCode = customerCode
    }

    fun validateBarcode(scannedBill: Bill, decodedText: String) {
        if (!scannedBill.isAvailable) {
            _lvErrorMessage.postValue(
                ErrorMessage(
                    title = "Format bon tidak sesuai",
                    detail = "Kode QR\n" +
                            decodedText
                )
            )
            return
        }

        if (rBills.size > 0) {
            if (rCustomerCode != scannedBill.customerCode) {
                _lvErrorMessage.postValue(
                    ErrorMessage(
                        title = "Kode customer tidak sesuai",
                        detail = "Kode Customer harus sama\n" +
                                "Kode sebelum $rCustomerCode\n" +
                                "Kode baru ${scannedBill.customerCode}"
                    )
                )
                return
            }

            if (rBills.contains(scannedBill.billCode)) {
                var rCodes = ""
                rBills.map { rCodes += "$it\n" }
                _lvErrorMessage.postValue(
                    ErrorMessage(
                        title = "Bon sudah terdaftar",
                        detail = "Daftar bon yang telah terdaftar:\n" +
                                rCodes
                    )
                )
                return
            }
        }

        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.VALIDATE_BILL,
                RequestParam.validateBill(bill = scannedBill),
                RequestResult::validateBill
            ).collect { res ->
                if (res.state == MResponse.FINISHED_FAILURE) {
                    _lvErrorMessage.postValue(
                        ErrorMessage(
                            title = res.response?.message ?: emptyString(),
                            detail = res.response?.message ?: emptyString()
                        )
                    )
                } else if (res.state == MResponse.FINISHED_SUCCESS) {
                    scannedBill.customerName = res.response?.data as String
                    _lvConfiguredBill.postValue(scannedBill)
                }
            }
        }
    }

}