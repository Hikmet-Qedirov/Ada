package com.solidict.ada.ui.fragments.auth.auth_login_fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentAuthLoginBinding
import com.solidict.ada.util.*
import com.solidict.ada.util.Constants.Companion.ACTUAL_DATE_PICKER_TAG
import com.solidict.ada.util.Constants.Companion.DATE_FORMAT_DATE_PICKER
import com.solidict.ada.util.Constants.Companion.EXPECTED_DATE_PICKER_TAG
import com.solidict.ada.util.Constants.Companion.GENDER_BOY
import com.solidict.ada.util.Constants.Companion.GENDER_GIRL
import com.solidict.ada.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TestAuthLoginFragment"

@AndroidEntryPoint
class AuthLoginFragment : Fragment() {

    private var _binding: FragmentAuthLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var loadingDialog: Dialog
    private lateinit var connectionDialog: Dialog
    private lateinit var errorDialog: Dialog
    private lateinit var warnDialog: Dialog
    private lateinit var expectedDatePicker: MaterialDatePicker<Long>
    private lateinit var actualDatePicker: MaterialDatePicker<Long>

    private var childFirstLastName = ""
    private var childExpectedDate = ""
    private var childActualDate = ""
    private var childBirthWeight = 0
    private var childGender = ""
    private var childReferringDoctorName = ""
    private var childEmailAddress = ""
    private var privacyTermsOfUseClicked = false
    private var payingThisFee = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAuthLoginBinding.inflate(inflater, container, false)
        expectedDatePickerConfig()
        actualDatePickerConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = Dialog(requireContext())
        errorDialog = Dialog(requireContext())
        warnDialog = Dialog(requireContext())
        connectionDialog = Dialog(requireContext())
        loadingDialog.showLoadingDialogConfig()
        connectionDialog.showInternetStateConnection()
        checkInputWarnConfig()
        firstLastNameConfig()
        expectedDatePickerTextInputEditText()
        actualDatePickerConfigTextInputEditText()
        birthWeightConfig()
        genderConfig()
        doctorNameConfig()
        emailAddressConfig()
        checkBoxConfig()
        webViewConfig()
        goOnButtonConfiguration()
        connectionDialog()
        observeUserPost()
    }

    private fun connectionDialog() {
        warnDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.fragment_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val content: TextView = warnDialog.findViewById(R.id.customAlertDialogText)
        val yesBtn: Button = warnDialog.findViewById(R.id.customAlertDialogOkButton)
        val noBtn: TextView = warnDialog.findViewById(R.id.customAlertDialogCancelButton)
        content.text = getString(R.string.attention_notice)
        yesBtn.text = getString(R.string.my_information_is_correct)
        noBtn.text = getString(R.string.go_back)
        yesBtn.setOnClickListener {
            yesBtn.isEnabled = false
            Log.d(TAG, "try userPost:: ")
            try {
                if (hasInternetConnection(requireContext())) {
                    loadingDialog.show()
                    viewModel.userPost(
                        privacyContract = privacyTermsOfUseClicked,
                        reportContract = payingThisFee,
                        childDoctorName = childReferringDoctorName,
                        childEstimatedBirthDate = childExpectedDate,
                        childGender = childGender,
                        childGrams = childBirthWeight,
                        childName = childFirstLastName,
                        childRealBirthDate = childActualDate,
                        email = childEmailAddress
                    )
                } else {
                    connectionDialog.show()
                }
            } catch (e: Exception) {
                Log.d(TAG, "catch userPost:: ")
                Log.d(TAG, "catch exception e::$e ")

                Snackbar.make(
                    binding.root,
                    getString(R.string.retry_error),
                    Snackbar.LENGTH_LONG
                ).show()
                warnDialog.dismiss()
            }
        }
        noBtn.setOnClickListener {
            warnDialog.dismiss()
            binding.authLoginGoOnButton.isEnabled = true
        }
    }

    private fun firstLastNameConfig() {
        binding.authLoginNameTextInputEditText.doOnTextChanged { _, _, _, _ ->
            binding.authLoginNameTextInputLayout.error = null
            binding.authLoginGoOnButton.isEnabled = true
        }
    }

    private fun expectedDatePickerConfig() {
        expectedDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.expected_date_of_birth))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        expectedDatePicker.addOnPositiveButtonClickListener {
            val selectedDate =
                SimpleDateFormat(DATE_FORMAT_DATE_PICKER, Locale.getDefault()).format(it)
            childExpectedDate = selectedDate
            binding.authLoginExpectedDateOfBirthTextInputLayout.hint = childExpectedDate
        }
        expectedDatePicker.addOnNegativeButtonClickListener {
            Snackbar.make(
                binding.root,
                getString(R.string.warn_date_picker),
                Snackbar.LENGTH_LONG
            )
                .show()
        }
    }

    private fun expectedDatePickerTextInputEditText() {
        binding.authLoginExpectedDateOfBirthTextInputEditText.setOnClickListener {
            binding.authLoginExpectedDateOfBirthTextInputLayout.error = null
            binding.authLoginGoOnButton.isEnabled = true
            expectedDatePicker.show(childFragmentManager, EXPECTED_DATE_PICKER_TAG)
        }
    }

    private fun actualDatePickerConfig() {
        actualDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.actual_date_of_birth))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        actualDatePicker.addOnPositiveButtonClickListener {
            if (it > MaterialDatePicker.todayInUtcMilliseconds()) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.wrong_date_picker),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            } else {
                val selectedDate =
                    SimpleDateFormat(DATE_FORMAT_DATE_PICKER, Locale.getDefault()).format(it)
                childActualDate = selectedDate
                binding.authLoginActualDateOfBirthTextInputLayout.hint = childActualDate
            }
        }
        actualDatePicker.addOnNegativeButtonClickListener {
            Snackbar.make(
                binding.root,
                getString(R.string.warn_date_picker),
                Snackbar.LENGTH_LONG
            )
                .show()
        }
    }

    private fun actualDatePickerConfigTextInputEditText() {
        binding.authLoginActualDateOfBirthTextInputEditText.setOnClickListener {
            binding.authLoginActualDateOfBirthTextInputLayout.error = null
            binding.authLoginGoOnButton.isEnabled = true
            actualDatePicker.show(childFragmentManager, ACTUAL_DATE_PICKER_TAG)
        }
    }

    private fun birthWeightConfig() {
        binding.authLoginBirthWeightTextInputEditText.doOnTextChanged { _, _, _, _ ->
            binding.authLoginGoOnButton.isEnabled = true
            binding.authLoginBirthWeightTextInputLayout.error = null
        }
    }

    private fun genderConfig() {
        if (binding.authLoginGenderBoy.isChecked) {
            childGender = GENDER_BOY
        }
        if (binding.authLoginGenderGirl.isChecked) {
            childGender = GENDER_GIRL
        }
        binding.authLoginGender.setOnCheckedChangeListener { _, _ ->
            binding.authLoginGoOnButton.isEnabled = true
            if (binding.authLoginGenderBoy.isChecked) {
                childGender = GENDER_BOY
            }
            if (binding.authLoginGenderGirl.isChecked) {
                childGender = GENDER_GIRL
            }
        }
    }

    private fun doctorNameConfig() {
        binding.authLoginReferringDoctorNameTextInputEditText.doOnTextChanged { _, _, _, _ ->
            binding.authLoginReferringDoctorNameTextInputLayout.error = null
            binding.authLoginGoOnButton.isEnabled = true
        }
    }

    private fun emailAddressConfig() {
        binding.authLoginRequiredEmailAddressTextInputEditText.doOnTextChanged { _, _, _, _ ->
            binding.authLoginGoOnButton.isEnabled = true
            binding.authLoginRequiredEmailAddressTextInputLayout.error = null
        }
    }

    private fun webViewConfig() {
        binding.authLoginPrivacyPolicyAndTermsOfUseButton.setOnClickListener {
            binding.authLoginGoOnButton.isEnabled = true
            val action = AuthLoginFragmentDirections
                .actionAuthLoginFragmentToWebViewFragment(Constants.PRIVARICY_POLICY_AND_TERMS_OF_USE)
            findNavController().navigate(action)
        }
    }

    private fun checkBoxConfig() {
        binding.authLoginPrivacyPolicyAndTermsOfUseCheckBox.setOnCheckedChangeListener { _, _ ->
            binding.authLoginPrivacyPolicyAndTermsOfUseCheckBox.error = null
            privacyTermsOfUseClicked = binding.authLoginPrivacyPolicyAndTermsOfUseCheckBox.isChecked
            binding.authLoginGoOnButton.isEnabled = true
        }
        binding.authLoginPayingCheckBox.setOnCheckedChangeListener { _, _ ->
            binding.authLoginPayingCheckBox.error = null
            payingThisFee = binding.authLoginPayingCheckBox.isChecked
            binding.authLoginGoOnButton.isEnabled = true
        }
    }

    private fun goOnButtonConfiguration() {
        binding.authLoginGoOnButton.setOnClickListener {
            binding.authLoginGoOnButton.isEnabled = false
            val name = binding.authLoginNameTextInputEditText.text?.toString()
            val birthWeight = binding.authLoginBirthWeightTextInputEditText.text?.toString()
            val doctorName = binding.authLoginReferringDoctorNameTextInputEditText.text?.toString()
            val emailAddress =
                binding.authLoginRequiredEmailAddressTextInputEditText.text?.toString()

            if (name !== null) {
                if (name.isNotEmpty() && name.nameValidation()) {
                    childFirstLastName = name
                } else {
                    binding.authLoginNameTextInputLayout.error = getString(R.string.wrong_name)
                }
            } else {
                binding.authLoginNameTextInputLayout.error = getString(R.string.wrong_name)
            }
            if (childExpectedDate.trim().isEmpty()) {
                binding.authLoginExpectedDateOfBirthTextInputLayout.error =
                    getString(R.string.wrong_birgth_date)
            }
            if (childActualDate.trim().isEmpty()) {
                binding.authLoginActualDateOfBirthTextInputLayout.error =
                    getString(R.string.wrong_birgth_date)
            }
            if (birthWeight !== null) {
                if (birthWeight.isNotEmpty() && birthWeight.toInt().birthWeightValidation()) {
                    childBirthWeight = birthWeight.toInt()
                } else {
                    binding.authLoginBirthWeightTextInputLayout.error =
                        getString(R.string.wrong_weight)
                }
            } else {
                binding.authLoginBirthWeightTextInputLayout.error = getString(R.string.wrong_weight)
            }
            if (doctorName !== null) {
                if (doctorName.isNotEmpty()) {
                    if (doctorName.nameValidation()) {
                        childReferringDoctorName = doctorName
                    } else {
                        binding.authLoginReferringDoctorNameTextInputLayout.error =
                            getString(R.string.wrong_doctor_name)
                    }
                }
            }
            if (emailAddress !== null) {
                if (emailAddress.isNotEmpty() && emailAddress.emailValidation()) {
                    childEmailAddress = emailAddress
                } else {
                    binding.authLoginRequiredEmailAddressTextInputLayout.error =
                        getString(R.string.wrong_included_email)
                }
            } else {
                binding.authLoginRequiredEmailAddressTextInputLayout.error =
                    getString(R.string.wrong_included_email)
            }
            if (!privacyTermsOfUseClicked) {
                binding.authLoginPrivacyPolicyAndTermsOfUseCheckBox.error =
                    getString(R.string.error_check_box)
                binding.authLoginPrivacyPolicyAndTermsOfUseCheckBox.requestFocus()
            }
            if (!payingThisFee) {
                binding.authLoginPayingCheckBox.error = getString(R.string.error_check_box)
            }
            checkInputsConfig()
        }
    }

    private fun checkInputsConfig() {
        if (
            binding.authLoginNameTextInputLayout.error == null
            && binding.authLoginExpectedDateOfBirthTextInputLayout.error == null
            && binding.authLoginActualDateOfBirthTextInputLayout.error == null
            && binding.authLoginBirthWeightTextInputLayout.error == null
            && binding.authLoginReferringDoctorNameTextInputLayout.error == null
            && binding.authLoginRequiredEmailAddressTextInputLayout.error == null
            && binding.authLoginPrivacyPolicyAndTermsOfUseCheckBox.error == null
            && binding.authLoginPayingCheckBox.error == null
        ) {
            warnDialog.show()
        } else {
            errorDialog.show()
        }
    }

    private fun checkInputWarnConfig() {
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        errorDialog.setCancelable(false)
        errorDialog.setContentView(R.layout.fragment_dialog)
        errorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val content: TextView = errorDialog.findViewById(R.id.customAlertDialogText)
        val yesBtn: Button = errorDialog.findViewById(R.id.customAlertDialogOkButton)
        val noBtn: TextView = errorDialog.findViewById(R.id.customAlertDialogCancelButton)
        noBtn.visibility = View.GONE
        yesBtn.text = getString(R.string.ok)
        content.text = getString(R.string.warn_log_in)
        yesBtn.setOnClickListener {
            errorDialog.dismiss()
        }
    }

    private fun observeUserPost() {
        viewModel.userPost.observe(viewLifecycleOwner) { userResponse ->
            if (userResponse !== null) {
                if (userResponse.isSuccessful) {
                    Log.d(TAG, "user Response isSuccessful")
                    findNavController().navigate(
                        AuthLoginFragmentDirections.actionAuthLoginFragmentToMainFragment2()
                    )
                    warnDialog.dismiss()
                    loadingDialog.dismiss()
                    binding.authLoginGoOnButton.isEnabled = true
                } else {
                    binding.authLoginGoOnButton.isEnabled = true
                    Log.d(TAG, "user Response error")
                    Snackbar.make(
                        binding.root,
                        userResponse.message(),
                        Snackbar.LENGTH_LONG
                    ).show()
                    warnDialog.dismiss()
                    loadingDialog.dismiss()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}