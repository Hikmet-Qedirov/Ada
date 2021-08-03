package com.solidict.ada.ui.fragments.main.profile_fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentProfileBinding
import com.solidict.ada.model.user.UserResponse
import com.solidict.ada.util.*
import com.solidict.ada.util.Constants.Companion.GENDER_BOY
import com.solidict.ada.util.Constants.Companion.GENDER_GIRL
import com.solidict.ada.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var loadingDialog: Dialog
    private lateinit var messageDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = Dialog(requireContext())
        loadingDialog.showLoadingDialogConfig()
        messageDialog = Dialog(requireContext())
        messageDialogConfig()
        contactUsButton()
        reportButton()
        getUserData()
    }

    override fun onResume() {
        super.onResume()
        activity?.changeStatusBarColor(R.color.grey)
    }

    private fun getUserData() {
        loadingDialog.show()
        if (hasInternetConnection(requireContext())) {
            observeUserData()
        } else {
            loadingDialog.dismiss()
            Snackbar.make(
                binding.root,
                getString(R.string.lost_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun observeUserData() {
        viewModel.userData.observe(viewLifecycleOwner) { userDataResponse ->
            if (userDataResponse !== null) {
                if (userDataResponse.isSuccessful) {
                    val body = userDataResponse.body()!!
                    bindDataToView(body)
                    loadingDialog.dismiss()
                } else {
                    loadingDialog.dismiss()
                    Snackbar.make(
                        binding.root,
                        userDataResponse.message(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun bindDataToView(body: UserResponse) {
        binding.mainProfileReportButton.isEnabled = body.isReportable.status
        if (!body.isReportable.status) {
            binding.mainProfileReportTextView.text = body.isReportable.description
        }
        if (body.child.doctorName.nameValidation()) {
            binding.mainProfileDoctorName.text = body.child.doctorName
        } else {
            binding.doctorNameSection.visibility = View.GONE
        }
        binding.mainProfileFirstLastName.text = body.child.name
        binding.mainProfileExpectedBirthDate.text = body.child.estimatedBirthDate
        binding.mainProfileActualBirthDate.text = body.child.realBirthDate
        binding.mainProfileBirthWeight.text = body.child.grams.toString()
        if (body.child.sexuality == GENDER_GIRL) {
            binding.mainProfileGender.text = getString(R.string.girl)
        }
        if (body.child.sexuality == GENDER_BOY) {
            binding.mainProfileGender.text = getString(R.string.boy)
        }
        if (body.child.sexuality != GENDER_BOY && body.child.sexuality != GENDER_GIRL) {
            binding.mainProfileGender.text = body.child.sexuality

        }
        binding.mainProfileEmailAddress.text = body.email
    }

    private fun reportButton() {
        binding.mainProfileReportButton.setOnClickListener {
            postUserReport()
        }
    }

    private fun postUserReport() {
        loadingDialog.show()
        if (hasInternetConnection(requireContext())) {
            observeVideoReport()
        } else {
            loadingDialog.dismiss()
            Snackbar.make(
                binding.root,
                getString(R.string.lost_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun observeVideoReport() {
        viewModel.videoDemandReportPost.observe(viewLifecycleOwner) { videoReportResponse ->
            if (videoReportResponse != null) {
                if (videoReportResponse.isSuccessful) {
                    loadingDialog.dismiss()
                    messageDialog.show()
                } else {
                    loadingDialog.dismiss()
                    Snackbar.make(
                        binding.root,
                        videoReportResponse.message(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    private fun messageDialogConfig() {
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        messageDialog.setCancelable(false)
        messageDialog.setContentView(R.layout.fragment_dialog)
        messageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val content: TextView = messageDialog.findViewById(R.id.customAlertDialogText)
        val yesBtn: Button = messageDialog.findViewById(R.id.customAlertDialogOkButton)
        val noBtn: TextView =
            messageDialog.findViewById(R.id.customAlertDialogCancelButton)
        noBtn.visibility = View.GONE
        yesBtn.text = getString(R.string.ok)
        content.text = getString(R.string.video_report_content)
        yesBtn.setOnClickListener {
            messageDialog.dismiss()
        }
    }

    private fun contactUsButton() {
        binding.mainProfileContactUsButton.setOnClickListener {
            val intent = contactUsChooserOpen(requireContext())
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(binding.root, getString(R.string.error_chooser), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}