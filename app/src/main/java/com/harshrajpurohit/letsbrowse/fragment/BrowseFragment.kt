package com.harshrajpurohit.letsbrowse.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.view.*
import android.webkit.*
import androidx.fragment.app.Fragment
import com.harshrajpurohit.letsbrowse.R
import com.harshrajpurohit.letsbrowse.activity.MainActivity
import com.harshrajpurohit.letsbrowse.activity.changeTab
import com.harshrajpurohit.letsbrowse.databinding.FragmentBrowseBinding
import java.io.ByteArrayOutputStream

class BrowseFragment(private var urlNew: String) : Fragment() {

    lateinit var binding: FragmentBrowseBinding
    var webIcon: Bitmap? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        binding = FragmentBrowseBinding.bind(view)
        registerForContextMenu(binding.webView)

        binding.webView.apply {
            when{
                URLUtil.isValidUrl(urlNew) -> loadUrl(urlNew)
                urlNew.contains(".com", ignoreCase = true) -> loadUrl(urlNew)
                else -> loadUrl("https://www.google.com/search?q=$urlNew")
            }
        }

        return view
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        MainActivity.tabsList[MainActivity.myPager.currentItem].name = binding.webView.url.toString()
        MainActivity.tabsBtn.text = MainActivity.tabsList.size.toString()

        //for downloading file using external download manager
        binding.webView.setDownloadListener { url, _, _, _, _ -> startActivity(Intent(Intent.ACTION_VIEW).setData(
            Uri.parse(url))) }

        val mainRef = requireActivity() as MainActivity

        mainRef.binding.refreshBtn.visibility = View.VISIBLE
        mainRef.binding.refreshBtn.setOnClickListener {
            binding.webView.reload()
        }

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            webViewClient = object: WebViewClient(){

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    if(MainActivity.isDesktopSite)
                        view?.evaluateJavascript("document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                                " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));", null)
                }

                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    mainRef.binding.topSearchBar.text = SpannableStringBuilder(url)
                    MainActivity.tabsList[MainActivity.myPager.currentItem].name = url.toString()
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    mainRef.binding.progressBar.progress = 0
                    mainRef.binding.progressBar.visibility = View.VISIBLE
                    if(url!!.contains("you", ignoreCase = false)) mainRef.binding.root.transitionToEnd()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    mainRef.binding.progressBar.visibility = View.GONE
                    binding.webView.zoomOut()
                }
            }
            webChromeClient = object: WebChromeClient(){
                //for setting icon to our search bar
                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                    super.onReceivedIcon(view, icon)
                    try{
                        mainRef.binding.webIcon.setImageBitmap(icon)
                        webIcon = icon
                        MainActivity.bookmarkIndex = mainRef.isBookmarked(view?.url!!)
                        if(MainActivity.bookmarkIndex != -1){
                            val array = ByteArrayOutputStream()
                            icon!!.compress(Bitmap.CompressFormat.PNG, 100, array)
                            MainActivity.bookmarkList[MainActivity.bookmarkIndex].image = array.toByteArray()
                        }
                    }catch (e: Exception){}
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    binding.webView.visibility = View.GONE
                    binding.customView.visibility = View.VISIBLE
                    binding.customView.addView(view)
                    mainRef.binding.root.transitionToEnd()
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    binding.webView.visibility = View.VISIBLE
                    binding.customView.visibility = View.GONE

                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    mainRef.binding.progressBar.progress = newProgress
                }
            }



            binding.webView.setOnTouchListener { _, motionEvent ->
                mainRef.binding.root.onTouchEvent(motionEvent)
                return@setOnTouchListener false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).saveBookmarks()
        //for clearing all webview data
        binding.webView.apply {
            clearMatches()
            clearHistory()
            clearFormData()
            clearSslPreferences()
            clearCache(true)

            CookieManager.getInstance().removeAllCookies(null)
            WebStorage.getInstance().deleteAllData()
        }

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val result = binding.webView.hitTestResult
        when(result.type){
            WebView.HitTestResult.IMAGE_TYPE -> {
                menu.add("View Image")
                menu.add("Save Image")
                menu.add("Share Link")
                menu.add("Close")
            }
            WebView.HitTestResult.SRC_ANCHOR_TYPE, WebView.HitTestResult.ANCHOR_TYPE-> {
                menu.add("Open in New Tab")
                menu.add("Open Tab in Background")
                menu.add("Share Link")
                menu.add("Close")
            }
            WebView.HitTestResult.EDIT_TEXT_TYPE, WebView.HitTestResult.UNKNOWN_TYPE -> {}
            else ->{
                menu.add("Open in New Tab")
                menu.add("Open Tab in Background")
                menu.add("Share Link")
                menu.add("Close")
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        val message = Handler().obtainMessage()
        binding.webView.requestFocusNodeHref(message)
        val url = message.data.getString("url")

        when(item.title){
            "Open in New Tab" -> {
                changeTab(url.toString(), BrowseFragment(url.toString()))
            }
            "Open Tab in Background" ->{
                changeTab(url.toString(), BrowseFragment(url.toString()), isBackground = true)
            }
            "Close" -> {}
        }

        return super.onContextItemSelected(item)
    }
}