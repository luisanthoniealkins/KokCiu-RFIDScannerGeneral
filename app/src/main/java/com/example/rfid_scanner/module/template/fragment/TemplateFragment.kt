package com.example.rfid_scanner.module.template.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rfid_scanner.databinding.FragmentTemplateBinding

class TemplateFragment : Fragment() {

    private lateinit var viewModel: TemplateViewModel
    private var _binding: FragmentTemplateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this)[TemplateViewModel::class.java]
        _binding = FragmentTemplateBinding.inflate(inflater, container, false)

        viewModel.text.observe(viewLifecycleOwner) {
            binding.tvTest.text = this.toString().split("{")[0]
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}