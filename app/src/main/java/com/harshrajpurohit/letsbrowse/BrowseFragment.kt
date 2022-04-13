package com.harshrajpurohit.letsbrowse

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.harshrajpurohit.letsbrowse.databinding.FragmentBrowseBinding

class BrowseFragment(private var urlNew: String) : Fragment() {

    lateinit var binding: FragmentBrowseBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        binding = FragmentBrowseBinding.bind(view)


        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()

        val mainRef = requireActivity() as MainActivity

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            webViewClient = object: WebViewClient(){
                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    mainRef.binding.topSearchBar.text = SpannableStringBuilder(url)
                }
            }
            webChromeClient = WebChromeClient()

            when{
                URLUtil.isValidUrl(urlNew) -> loadUrl(urlNew)
                urlNew.contains(".com", ignoreCase = true) -> loadUrl(urlNew)
                else -> loadUrl("https://www.google.com/search?q=$urlNew")
            }
        }
    }
}