package com.solidict.ada.ui.fragments.main.video_status_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentVideoStatusBinding

class VideoStatusFragment : Fragment() {

    private var _binding: FragmentVideoStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.videoStatusOkButton.setOnClickListener {
//            findNavController().navigate(VideoStatusFragmentDirections.actionVideoStatusFragmentToNavigationMain())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}