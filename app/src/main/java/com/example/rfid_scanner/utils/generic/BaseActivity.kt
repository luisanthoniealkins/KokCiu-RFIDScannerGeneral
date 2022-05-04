package com.example.rfid_scanner.utils.generic

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.databinding.ActivityMainBinding
import com.example.rfid_scanner.module.main.MainViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity<VBinding : ViewBinding, ViewModel : BaseViewModel> : AppCompatActivity() {

    private var _binding: VBinding? = null
    protected val binding get() = _binding!!
    protected abstract fun getViewBinding(): VBinding

    private var _viewModel: ViewModel? = null
    protected val viewModel get() = _viewModel!!
    protected abstract fun getViewModelClass(): Class<ViewModel>

    private val disposableContainer = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        _viewModel = ViewModelProvider(this)[getViewModelClass()]
        setContentView(binding.root)
        supportActionBar?.hide()

        setUpViews()
        observeData()
        initEvent()
    }

    open fun setUpViews() {}

    open fun observeData() {}

    open fun initEvent() {}

    fun Disposable.addToContainer() = disposableContainer.add(this)


    override fun onDestroy() {
        disposableContainer.clear()
        super.onDestroy()
    }

    /**
     * Customization start here
     * */

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun reColor(resColorId: Int) : Int{
        return ContextCompat.getColor(this@BaseActivity, resColorId)
    }

    fun <T> LiveData<T>.observeWithOwner(function: (T) -> Unit) {
        this.observe(this@BaseActivity, function)
    }

}