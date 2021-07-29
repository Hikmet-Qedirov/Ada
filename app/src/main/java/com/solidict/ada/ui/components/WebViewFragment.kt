package com.solidict.ada.ui.components

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.solidict.ada.databinding.FragmentWebViewBinding
import com.solidict.ada.util.hasInternetConnection
import com.solidict.ada.util.showInternetStateConnection

class WebViewFragment : Fragment() {

    private val binding by lazy { FragmentWebViewBinding.inflate(layoutInflater) }
    private val args: WebViewFragmentArgs by navArgs()
    private lateinit var connectionDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        connectionDialog = Dialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionDialog.showInternetStateConnection()
        val url = args.webViewUrl
        if (hasInternetConnection(requireContext())) {
            try {
                configurationWebView(url)
            } catch (e: Exception) {
                connectionDialog.show()
                findNavController().popBackStack()
            }
        } else {

            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configurationWebView(argsUrl: String) {
        val webView = binding.webView
        webView.loadUrl(argsUrl)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (url == argsUrl) {
                    binding.webViewProgressBar.visibility = View.VISIBLE
                } else {
                    findNavController().popBackStack()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.webViewProgressBar.visibility = View.INVISIBLE
            }
        }
    }

}
