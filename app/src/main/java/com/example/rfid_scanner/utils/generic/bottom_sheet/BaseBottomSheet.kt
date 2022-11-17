package com.example.rfid_scanner.utils.generic.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.helper.LogHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseBottomSheet<VBinding : ViewBinding, ViewModel : BaseViewModel> : BottomSheetDialogFragment() {

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
    }

    override fun onResume() {
        super.onResume()
        setupNewViewModels()
        retrieveArgs()
        setUpViews()
        observeData()
        initEvent()
    }

    open fun setupNewViewModels() {}

    open fun retrieveArgs() {}

    open fun setUpViews() {}

    open fun observeData(){}

    open fun initEvent() {}

    fun Disposable.addToContainer() = disposableContainer.add(this)

    override fun onDestroyView() {
        disposableContainer.clear()
        super.onDestroyView()
        _binding = null
    }

    /**
     * Customization start here
     * */
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun gColor(resColorId: Int) : Int{
        return ContextCompat.getColor(requireContext(), resColorId)
    }

    fun <T> LiveData<T>.observeWithOwner(function: (T) -> Unit) {
        this.observe(viewLifecycleOwner, function)
    }

}