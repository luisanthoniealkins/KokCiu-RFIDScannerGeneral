package com.example.rfid_scanner.utils.generic.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseDialog<VBinding : ViewBinding> : DialogFragment() {

    private var _binding: VBinding? = null
    protected val binding get() = _binding!!
    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VBinding

    private val disposableContainer = CompositeDisposable()

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
        retrieveArgs()
        setUpViews()
        observeData()
        initEvent()
    }

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

}