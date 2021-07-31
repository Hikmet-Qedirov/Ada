package com.solidict.ada.ui.fragments.auth.auth_number_verification_fragment

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentAuthNumberVerificationBinding
import com.solidict.ada.util.*
import com.solidict.ada.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AuthNumberVerificationFragment : Fragment() {
    private var _binding: FragmentAuthNumberVerificationBinding? = null
    private val binding get() = _binding!!
    private val args: AuthNumberVerificationFragmentArgs by navArgs()
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var loadingDialog: Dialog
    private lateinit var connectionDialog: Dialog
    private lateinit var errorDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAuthNumberVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goBackButtonConfiguration()
        loadingDialog = Dialog(requireContext())
        connectionDialog = Dialog(requireContext())
        errorDialog = Dialog(requireContext())
        loadingDialog.showLoadingDialogConfig()
        connectionDialog.showInternetStateConnection()
        observeUserCheck()
        observeAuth()
        verificationGoOnButtonConfiguration()
        resendCodeConfig()
    }

    private fun observeAuth() {
        viewModel.authResponse.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse != null) {
                if (authResponse.isSuccessful) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.success_submit_code_again),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Snackbar.make(
                        binding.root,
                        authResponse.message(),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun resendCodeConfig() {
        binding.authNumberVerificationResendTextView.setOnClickListener {
            verificationResendConfiguration()
            observeAuthConfig()
        }
    }

    private fun observeAuthConfig() {
        if (hasInternetConnection(requireContext())) {
            viewModel.auth(args.number)
        } else {
            connectionDialog.show()
        }

    }

    private fun verificationGoOnButtonConfiguration() {
        binding.authNumberVerificationGoOnButton.setOnClickListener {
            binding.authNumberVerificationGoOnButton.isEnabled = false
            binding.authNumberVerificationGoOnButton.closeKeyboard()
            val verificationCode = binding.authNumberVerificationEditText.text?.toString()
            if (verificationCode != null) {
                if (verificationCode.isNotEmpty()) {
                    observeAuthValidateConfig(verificationCode)
                }
            } else {
                binding.authNumberVerificationGoOnButton.isEnabled = true
                binding.authNumberVerificationTextInputLayout.error =
                    getString(R.string.wrong_included_number)
            }
        }
    }

    private fun observeAuthValidateConfig(code: String) {
        loadingDialog.show()
        if (hasInternetConnection(requireContext())) {
            viewModel.authValidate(args.authResponse.id, code)
            viewModel.authValidate.observe(viewLifecycleOwner) { authValidateResponse ->
                if (authValidateResponse != null) {
                    if (authValidateResponse.isSuccessful) {
                        viewModel.userCheck()
                    } else {
                        loadingDialog.dismiss()
                        binding.authNumberVerificationGoOnButton.isEnabled = true
                        Snackbar.make(binding.root, getString(R.string.retry), Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        } else {
            binding.authNumberVerificationGoOnButton.isEnabled = true
            loadingDialog.dismiss()
            connectionDialog.show()
        }
    }

    private fun observeUserCheck() {
        viewModel.userCheck.observe(viewLifecycleOwner,{userCheckResponse->
            if (userCheckResponse != null) {
                if (userCheckResponse.isSuccessful) {
                    loadingDialog.dismiss()
                    val profileCompleted = userCheckResponse.body()!!.profileCompleted
                    if (profileCompleted) {
                        findNavController().navigate(R.id.mainFragment)
                    } else {
                        findNavController().navigate(AuthNumberVerificationFragmentDirections.actionAuthNumberVerificationFragmentToAuthLoginFragment())
                    }
                    binding.authNumberVerificationGoOnButton.isEnabled = true
                } else {
                    loadingDialog.dismiss()
                    Snackbar.make(binding.root, userCheckResponse.message(), Snackbar.LENGTH_LONG)
                        .show()
                    binding.authNumberVerificationGoOnButton.isEnabled = true
                }
            }
        })
    }

    private fun verificationResendConfiguration() {
        binding.authNumberVerificationTimerTextView.visibility = View.VISIBLE
        binding.authNumberVerificationResendTextView.isEnabled = false
        val timer = object : CountDownTimer(11000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.authNumberVerificationTimerTextView.text =
                    getString(
                        R.string.timer_formatted_time,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                    )
            }

            override fun onFinish() {
                binding.authNumberVerificationResendTextView.isEnabled = true
                binding.authNumberVerificationTimerTextView.visibility = View.INVISIBLE
            }
        }
        timer.start()
    }

    private fun goBackButtonConfiguration() {
        binding.authNumberVerificationGoBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}