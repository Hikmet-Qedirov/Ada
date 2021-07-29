package com.solidict.ada.ui.fragments.main.video_bottom_sheet_fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentVideoBottomSheetBinding
import com.solidict.ada.util.changeStatusBarColor
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TestVideoBottomSheetFragment"

@AndroidEntryPoint
class VideoBottomSheetFragment : Fragment() {

    private var _binding: FragmentVideoBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageDialog: Dialog

    private var ruleOne = false
    private var ruleTwo = false
    private var ruleThree = false

    private var cameraPermissionBoolean = false
    private var readExternalStoragePermissionBoolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.changeStatusBarColor(R.color.grey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageDialog = Dialog(requireContext())
        messageDialogConfig()
        closeButton()
        checkBoxesConfig()
        iAmReadyButtonConfig()
        enabledIAmReadyButton()
    }


    private fun messageDialogConfig() {
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        messageDialog.setCancelable(false)
        messageDialog.setContentView(R.layout.fragment_dialog)
        messageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yesBtn: Button =
            messageDialog.findViewById(R.id.customAlertDialogOkButton)
        val noBtn: TextView =
            messageDialog.findViewById(R.id.customAlertDialogCancelButton)
        yesBtn.text = getString(R.string.ok)
        noBtn.text = getString(R.string.go_back)
        val content: TextView =
            messageDialog.findViewById(R.id.customAlertDialogText)
        content.text = getString(R.string.permission_dialog_info)
        yesBtn.setOnClickListener {
            requestMultiplePermissions()
            messageDialog.dismiss()
        }
        noBtn.setOnClickListener {
            messageDialog.dismiss()
        }
    }


    private fun checkBoxesConfig() {
        binding.videoBottomSheetFirstRuleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            ruleOne = isChecked
            enabledIAmReadyButton()
        }
        binding.videoBottomSheetSecondRuleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            ruleTwo = isChecked
            enabledIAmReadyButton()
        }
        binding.videoBottomSheetThirdRuleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            ruleThree = isChecked
            enabledIAmReadyButton()
        }
    }

    private fun enabledIAmReadyButton() {
        binding.videoIamReadyButton.isEnabled = ruleOne && ruleTwo && ruleThree
    }

    private fun iAmReadyButtonConfig() {
        binding.videoIamReadyButton.isEnabled = false
        binding.videoIamReadyButton.setOnClickListener {
            iAmReadyButtonClicked()
        }
    }

    private fun closeButton() {
        binding.videoBottomSheetCloseButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun iAmReadyButtonClicked() {
        requestMultiplePermissions()
    }

    // camera permission

    private val requestMultiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionList ->
        permissionList.entries.forEach { permission ->
            if (permission.key == Manifest.permission.CAMERA) {
                cameraPermissionBoolean = permission.value
            }
            if (permission.key == Manifest.permission.READ_EXTERNAL_STORAGE) {
                readExternalStoragePermissionBoolean = permission.value
            }
        }
        if (cameraPermissionBoolean &&
            readExternalStoragePermissionBoolean
        ) {
            Log.d(TAG, "Multi permissions is granted")
            recordVideoStart()
        } else {
            Log.d(TAG, "Multi permissions is denied")
            Snackbar.make(
                binding.root,
                getString(R.string.permission_warn),
                Snackbar.LENGTH_LONG
            ).setAction(R.string.ok) {
                requestMultiplePermissions()
            }
                .show()
        }
    }


    private fun requestMultiplePermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
            -> {
                // Permission is granted
                // Use the camera
                requestMultiplePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                )
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                (activity as Activity),
                Manifest.permission.CAMERA
            ) && ActivityCompat.shouldShowRequestPermissionRationale(
                (activity as Activity),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                // Additional rationale should be displayed
                messageDialog.show()
            }
            else -> {
                // Permission has not been asked yet
                requestMultiplePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    )
                )
            }
        }

    }
//  end camera permission


    // multiple permission


    private fun recordVideoStart() {
        Log.d(TAG, "Camera permission is granted and recordVideoStart")
        findNavController().navigate(
            VideoBottomSheetFragmentDirections.actionVideoBottomSheetFragmentToVideoCompletedFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}