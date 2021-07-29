package com.solidict.ada.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.solidict.ada.R
import com.solidict.ada.databinding.FragmentMainBinding
import com.solidict.ada.util.changeStatusBarColor

private const val TAG = "TestMainFragment"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.changeStatusBarColor(R.color.grey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomConfig()
    }

    private fun bottomConfig() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemReselectedListener {
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "bottom navigation destination is ::: ${destination.label}")
            when (destination.id) {
                R.id.videosFragment -> showBottomNavigation()
                R.id.profileFragment -> showBottomNavigation()
                else -> hideBottomNavigation()
            }
        }
    }

    private fun showBottomNavigation() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}