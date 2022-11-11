package com.example.rfid_scanner.utils.generic.bottom_sheet

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseBottomSheet<VBinding : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: VBinding? = null
    protected val binding get() = _binding!!
    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VBinding

    private val disposableContainer = CompositeDisposable()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) { // if granted
        } else { // if not granted, provide popup message to explain
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNewViewModels()
        retrieveArgs()
        setUpViews()
        observeData()
        initEvent()
    }

    open fun setupNewViewModels() {}

    open fun retrieveArgs() {}

    open fun setUpViews() {}

    open fun observeData() {}

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

    fun requestPermissionIfNotGranted(permission: String) = when {
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> true
        shouldShowRequestPermissionRationale(permission) -> false
        else -> {
            requestPermissionLauncher.launch(permission)
            false
        }
    }

}