package com.example.app.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.app.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Legacy View-system screen: inflates XML, wires widgets by id, observes the ViewModel.
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        binding.btnSave.setOnClickListener {
            viewModel.save(
                name = binding.inputName.text?.toString().orEmpty(),
                email = binding.inputEmail.text?.toString().orEmpty(),
                phone = binding.inputPhone.text?.toString().orEmpty(),
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profile.collect { profile ->
                binding.inputName.setText(profile.name)
                binding.inputEmail.setText(profile.email)
                binding.inputPhone.setText(profile.phone)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
