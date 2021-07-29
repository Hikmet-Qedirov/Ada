package com.solidict.ada.ui.fragments.main.video_status_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

    // TODO: 7/29/2021 Burada sehife sifirlanmalidi
    // Todo fin nav controller have issue app crash

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.videoStatusOkButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}