package com.solidict.ada.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.solidict.ada.R
import com.solidict.ada.databinding.SplashScreenBinding
import com.solidict.ada.util.changeStatusBarColor
import com.solidict.ada.util.hasInternetConnection
import com.solidict.ada.util.showInternetStateConnection
import com.solidict.ada.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SplashScreenFragment"

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private var _binding: SplashScreenBinding ?=null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var connectionDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SplashScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.changeStatusBarColor(R.color.white)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionDialog = Dialog(requireContext())
        connectionDialog.showInternetStateConnection()
        userExistOrNot()
    }

    private fun userExistOrNot() {
        viewModel.userExist.observe(viewLifecycleOwner) { userExist ->
            if (userExist) {
                userCheck()
            } else {
                findNavController().navigate(
                    SplashScreenFragmentDirections.actionSplashScreenFragmentToAuthWelcomeFragment()
                )
            }
        }
    }

    private fun userCheck() {
        if (hasInternetConnection(requireContext())) {
            binding.progressBarSplashScreen.visibility = View.VISIBLE
            observeIsLogInOrNot()
        } else {
            connectionDialog.show()
        }
    }

    private fun observeIsLogInOrNot() {
        Log.d(TAG, "observeIsLogInOrNot::::::::")
        viewModel.userCheck.observe(viewLifecycleOwner) { userCheckResponse ->
            if (userCheckResponse != null) {
                Log.d(TAG, "observeIsLogInOrNot is not null::::::::")
                if (userCheckResponse.isSuccessful) {
                    Log.d(TAG, "observeIsLogInOrNot is successful   ::::::::")
                    val userCompleted = userCheckResponse.body()!!.profileCompleted
                    Log.d(TAG, "observeIsLogInOrNot userCompleted:: $userCompleted")
                    if (userCompleted) {
                        Log.d(TAG, ":: userCompleted :: true")
                        findNavController().popBackStack(R.id.splashScreenFragment, true)
                        findNavController().navigate(R.id.mainFragment)
                    } else {
                        Log.d(TAG, ":: userCompleted :: false")
                        findNavController().navigate(
                            SplashScreenFragmentDirections.actionSplashScreenFragmentToAuthLoginFragment()
                        )
                    }
                } else {
                    connectionDialog.show()
                }
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}