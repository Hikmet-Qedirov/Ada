package com.solidict.ada.ui.fragments.auth.auth_welcome_fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentAuthWelcomeBinding
import com.solidict.ada.util.*
import com.solidict.ada.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthWelcomeFragment : Fragment() {

    private var _binding: FragmentAuthWelcomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var loadingDialog: Dialog
    private lateinit var connectionDialog: Dialog
    private lateinit var errorDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAuthWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authEditTextConfiguration()
        configurationWebViewButtons()
        loadingDialog = Dialog(requireContext())
        connectionDialog = Dialog(requireContext())
        errorDialog = Dialog(requireContext())
        loadingDialog.showLoadingDialogConfig()
        connectionDialog.showInternetStateConnection()
        goOnButtonConfiguration()
    }

    private fun authEditTextConfiguration() {
        binding.authTextInputEditTextNumberPhone.doOnTextChanged { text, _, _, _ ->
            binding.authTextInputLayoutNumberPhone.error = null
            binding.authGoOnButton.isEnabled = !text.isNullOrBlank()
        }
    }

    private fun goOnButtonConfiguration() {
        binding.authGoOnButton.setOnClickListener {

            binding.authGoOnButton.closeKeyboard()
            val number = binding.authTextInputEditTextNumberPhone.text.toString()
            if (number.phoneNumberValidation(requireContext())) {
                val authNumber = "0090$number"
                binding.authGoOnButton.isEnabled = false
                if (hasInternetConnection(requireContext())) {
                    loadingDialog.show()
                    viewModel.auth(authNumber)
                    observeAuth(authNumber)
                } else {
                    binding.authGoOnButton.isEnabled = true
                    loadingDialog.dismiss()
                    connectionDialog.show()
                }
            } else {
                binding.authGoOnButton.isEnabled = true
                loadingDialog.dismiss()
                binding.authTextInputLayoutNumberPhone.error =
                    getString(R.string.wrong_included_number)
            }
        }
    }

    private fun observeAuth(number: String) {
        viewModel.authResponse.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse !== null) {
                if (authResponse.isSuccessful) {
                    loadingDialog.dismiss()
                    val body = authResponse.body()!!
                    val action =
                        AuthWelcomeFragmentDirections.actionAuthWelcomeFragmentToAuthNumberVerificationFragment(
                            authResponse = body,
                            number = number
                        )
                    findNavController().navigate(action)
                    binding.authGoOnButton.isEnabled = true
                } else {
                    loadingDialog.dismiss()
                    binding.authGoOnButton.isEnabled = true
                    errorDialog.showMessageDialog(authResponse.message())
                    errorDialog.show()
                }
            }
        }
    }

    private fun configurationWebViewButtons() {
        binding.apply {
            authPrivacyAndPolicyButton.setOnClickListener {
                webViewButtonClick(Constants.PRIVARICY_AND_POLICY_URL)
            }
            authAboutMedialDirectorButton.setOnClickListener {
                webViewButtonClick(Constants.ABOUT_MEDIAL_DIRECTOR_URL)
            }
            authTermsOfUseButton.setOnClickListener {
                webViewButtonClick(Constants.TERMS_OF_USE_URL)
            }
        }
    }

    private fun webViewButtonClick(string: String) {
        val action =
            AuthWelcomeFragmentDirections.actionAuthWelcomeFragmentToWebViewFragment(string)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}