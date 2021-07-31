package com.solidict.ada.ui.fragments.main.videos_fragment

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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentVideosBinding
import com.solidict.ada.model.video.Video
import com.solidict.ada.util.SaveDataPreferences
import com.solidict.ada.util.changeStatusBarColor
import com.solidict.ada.util.hasInternetConnection
import com.solidict.ada.util.showLoadingDialogConfig
import com.solidict.ada.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@AndroidEntryPoint
class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var loadingDialog: Dialog
    private lateinit var messageDialog: Dialog
    private lateinit var videoAdapter: VideoAdapter

    @Inject
    lateinit var saveDataPreferences: SaveDataPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = Dialog(requireContext())
        loadingDialog.showLoadingDialogConfig()
        messageDialog = Dialog(requireContext())
        messageDialogConfig()
        mainVideosRecordVideoButton()
        recyclerViewConfig()
        videoListConfig()
    }

    override fun onResume() {
        super.onResume()
        activity?.changeStatusBarColor(R.color.grey)
    }

    private fun recyclerViewConfig() {
        videoAdapter = VideoAdapter()
        binding.mainVideoListReyclerView.apply {
            adapter = videoAdapter
            setHasFixedSize(true)
        }
        videoAdapter.setOnItemClickListener { video ->
            val videoId = video.id
            CoroutineScope(Dispatchers.Main).launch {
                saveDataPreferences.saveVideoId(videoId.toString())
                findNavController().navigate(VideosFragmentDirections.actionVideosFragmentToNavigaitonVideoRecord())
            }
        }
        videoAdapter.setOnFooterItemClickListener {
            mainVideoRecordVideoButtonFunctions()
        }
    }


    private fun videoListConfig() {
        if (hasInternetConnection(requireContext())) {
            loadingDialog.show()
            observeVideoListGet()
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.lost_internet_connection),
                Snackbar.LENGTH_LONG
            )
                .show()
        }
    }

    private fun observeVideoListGet() {
        viewModel.videoListGet.observe(viewLifecycleOwner) { videoResponse ->
            if (videoResponse != null) {
                if (videoResponse.isSuccessful) {
                    loadingDialog.dismiss()
                    val body = videoResponse.body()!!
                    val videos = body.videos
                    bindVideoListDataToView(videos)
                } else {
                    loadingDialog.dismiss()
                    Snackbar.make(binding.root, videoResponse.message(), Snackbar.LENGTH_LONG)
                        .show()
                }
            } else {
                loadingDialog.dismiss()
            }
        }
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
        noBtn.visibility = View.GONE
        yesBtn.text = getString(R.string.ok)
        yesBtn.setOnClickListener {
            messageDialog.dismiss()
        }
    }

    private fun mainVideosRecordVideoButton() {
        binding.mainVideosRecordVideoButton.setOnClickListener {
            binding.mainVideosRecordVideoButton.isEnabled = false
            mainVideoRecordVideoButtonFunctions()
        }
    }

    private fun mainVideoRecordVideoButtonFunctions() {
        if (hasInternetConnection(requireContext())) {
            loadingDialog.show()
            observeCanRecordVideo()
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.lost_internet_connection),
                Snackbar.LENGTH_LONG
            )
                .show()
            binding.mainVideosRecordVideoButton.isEnabled = true
        }
    }

    private fun observeCanRecordVideo() {
        viewModel.videoCanCreateGet.observe(viewLifecycleOwner) { isReportable ->
            if (isReportable != null) {
                if (isReportable.isSuccessful) {
                    loadingDialog.dismiss()
                    binding.mainVideosRecordVideoButton.isEnabled = true
                    val body = isReportable.body()!!
                    if (body.status) {
                        videoRecordFunctions()
                    } else {
                        val content: TextView =
                            messageDialog.findViewById(R.id.customAlertDialogText)
                        content.text = body.description
                        messageDialog.show()
                    }
                } else {
                    loadingDialog.dismiss()
                    binding.mainVideosRecordVideoButton.isEnabled = true
                    Snackbar.make(binding.root, isReportable.message(), Snackbar.LENGTH_LONG)
                        .show()
                }
            } else {
                loadingDialog.dismiss()
            }

        }
    }

    private fun videoRecordFunctions() {
        findNavController().navigate(
            VideosFragmentDirections.actionVideosFragmentToNavigaitonVideoRecord()
        )
    }

    private fun bindVideoListDataToView(videos: List<Video>) {
        if (videos.isEmpty()) {
            binding.mainVideoEmptyVideoList.visibility = View.VISIBLE
            binding.mainVideoListReyclerView.visibility = View.INVISIBLE
        } else {
            binding.mainVideoEmptyVideoList.visibility = View.INVISIBLE
            binding.mainVideoListReyclerView.visibility = View.VISIBLE
            bindDataToRecyclerViewList(videos)
        }
    }

    private fun bindDataToRecyclerViewList(videos: List<Video>) {

        videoAdapter.differ.submitList(videos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}