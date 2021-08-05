package com.solidict.ada.ui.fragments.main.video_completed_fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.MediaController
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentVideoRecordBinding
import com.solidict.ada.services.UploadService
import com.solidict.ada.util.Constants.Companion.START_UPLOAD_SERVICE
import com.solidict.ada.util.SaveDataPreferences
import com.solidict.ada.util.changeStatusBarColor
import com.solidict.ada.util.getVideoFile
import com.solidict.ada.util.hasInternetConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

private const val TAG = "TestVideoCompletedFragment"

@AndroidEntryPoint
class VideoCompletedFragment : Fragment() {

    private var _binding: FragmentVideoRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoFile: File

    @Inject
    lateinit var saveDataPreferences: SaveDataPreferences

    private lateinit var messageDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.changeStatusBarColor(R.color.white)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageDialog = Dialog(requireContext())
        messageDialogConfig()
        changeMessageDialogContent(getString(R.string.warn_video_record))
        messageDialog.show()
        buttonsConfig()
    }

    private fun buttonsConfig() {
        binding.videoRecordGoOnButton.setOnClickListener {
            val part = videoFile.absolutePath
            Log.d(TAG, "button config is $part")
            try {
                if (hasInternetConnection(requireContext())) {
                    binding.videoRecordRecordVideoButton.isEnabled = false
                    binding.videoRecordGoOnButton.isEnabled = false
                    val job = Job()
                    CoroutineScope(Dispatchers.Main + job).launch {
                        saveDataPreferences.saveVideoUri(part)
                        Intent(requireContext(), UploadService::class.java).also {
                            it.action = START_UPLOAD_SERVICE
                            requireContext().startService(it)
                            findNavController().navigate(VideoCompletedFragmentDirections.actionVideoCompletedFragmentToVideoStatusFragment())
                        }
                        job.cancel()
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.lost_internet_connection),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } catch (e: IOException) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.retry_error),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        binding.videoRecordRecordVideoButton.setOnClickListener {
            intentLaunchFunction()
        }
    }

    private fun messageDialogConfig() {
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        messageDialog.setCancelable(false)
        messageDialog.setContentView(R.layout.fragment_dialog)
        messageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yesBtn: Button = messageDialog.findViewById(R.id.customAlertDialogOkButton)
        val noBtn: TextView = messageDialog.findViewById(R.id.customAlertDialogCancelButton)
        yesBtn.text = getString(R.string.ok)
        noBtn.text = getString(R.string.go_back)
        yesBtn.setOnClickListener {
            messageDialog.dismiss()
            intentLaunchFunction()
        }
        noBtn.setOnClickListener {
            messageDialog.dismiss()
            findNavController().popBackStack()
        }
    }

    private fun changeMessageDialogContent(string: String) {
        val content: TextView =
            messageDialog.findViewById(R.id.customAlertDialogText)
        content.text = string
    }

    private val recordVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result?.data?.let { resultIntent ->
                    val uri = resultIntent.data!!
                    showVideoPreview(uri)
                }
            } else {
                changeMessageDialogContent(getString(R.string.error_video_record))
                messageDialog.show()
            }
        }

    private fun intentLaunchFunction() {
        videoFile = getVideoFile(requireContext())
        Log.d(TAG, "video file is ::: $videoFile")
        val fileProvider = FileProvider.getUriForFile(
            requireContext(),
            "com.solidict.ada.provider",
            videoFile
        )
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, CAMERA_RECORD_TIME_LIMIT)
            putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        recordVideo.launch(intent)
    }

    private fun showVideoPreview(uri: Uri) {
        binding.imageViewVideo.setVideoURI(uri)
        val mediaController = MediaController(requireContext())
        binding.imageViewVideo.setMediaController(mediaController)
        mediaController.setAnchorView(binding.imageViewVideo)
    }

    // TODO: 7/29/2021 video record size change
    companion object {
        //        private const val CAMERA_RECORD_TIME_LIMIT = 3 * 60
        private const val CAMERA_RECORD_TIME_LIMIT = 10
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
