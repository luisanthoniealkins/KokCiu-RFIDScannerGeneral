package com.example.rfid_scanner.module.main.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.utils.constant.Constant.PASSWORD_ADMIN
import com.example.rfid_scanner.utils.constant.Constant.PASSWORD_SUPER_ADMIN

class EntryViewModel : BaseViewModel() {


    private val _isAdminUnlocked = MutableLiveData<Boolean>().apply { postValue(false) }
    val isAdminUnlocked : LiveData<Boolean> = _isAdminUnlocked

    fun submitPassword(password: String?): Boolean {
        val isPasswordCorrect = ((password == PASSWORD_ADMIN) || (password == PASSWORD_SUPER_ADMIN))
        _isAdminUnlocked.postValue(isPasswordCorrect)
        return isPasswordCorrect
    }

}