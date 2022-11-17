package com.example.rfid_scanner.utils.generic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment<VBinding : ViewBinding, ViewModel : BaseViewModel> : Fragment() {

    private var _binding: VBinding? = null
    protected val binding get() = _binding!!
    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VBinding

    private var _viewModel: ViewModel? = null
    protected val viewModel get() = _viewModel!!
    protected abstract fun getViewModelClass(): Class<ViewModel>

    private val disposableContainer = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        _viewModel = ViewModelProvider(this)[getViewModelClass()]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNewViewModels()
        retrieveArgs()
        setUpViews()
        observeDataGlobal()
        initEvent()
    }

    open fun setupNewViewModels() {}

    open fun retrieveArgs() {}

    open fun setUpViews() {}

    private fun observeDataGlobal() {
        observeDataBase()
        observeData()
    }

    open fun observeData() {}

    open fun initEvent() {}

    fun Disposable.addToContainer() = disposableContainer.add(this)

    override fun onDestroyView() {
        disposableContainer.clear()
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }

    /**
     * Customization start here
     * */
    private fun observeDataBase() {
        viewModel.lvToastMessage.observeWithOwner {
            it.getContentIfNotHandled()?.let { msg -> showToast(msg) }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun getNavController() = view?.findNavController()

    fun navigateTo(direction: NavDirections) {
        getNavController()?.navigate(direction)
    }

    fun navigateBack() {
        getNavController()?.popBackStack()
    }

    fun gColor(resColorId: Int) : Int{
        return ContextCompat.getColor(requireContext(), resColorId)
    }

    fun <T> LiveData<T>.observeWithOwner(function: (T) -> Unit) {
        this.observe(viewLifecycleOwner, function)
    }



}