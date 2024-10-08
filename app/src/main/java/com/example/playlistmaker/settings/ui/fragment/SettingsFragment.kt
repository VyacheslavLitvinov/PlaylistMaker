package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.App
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stateSwitch = viewModel.darkTheme()
        binding.themeSwitcher.isChecked = stateSwitch

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (requireActivity().applicationContext as App).switchTheme(checked)
            viewModel.themeSwitch(checked)
        }

        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        binding.helpButton.setOnClickListener {
            viewModel.supportApp()
        }

        binding.termsButton.setOnClickListener {
            viewModel.termsApp()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}