package com.harshrajpurohit.letsbrowse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.view.Gravity
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.harshrajpurohit.letsbrowse.databinding.ActivityMainBinding
import com.harshrajpurohit.letsbrowse.databinding.MoreFeaturesBinding
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var printJob: PrintJob? = null

    companion object{
        var tabsList: ArrayList<Fragment> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        tabsList.add(HomeFragment())

        binding.myPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)
        binding.myPager.isUserInputEnabled = false
        initializeView()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {
        var frag:BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem] as BrowseFragment
        }catch (e:Exception){}

        when{
            frag?.binding?.webView?.canGoBack() == true -> frag.binding.webView.goBack()
            binding.myPager.currentItem != 0 ->{
                tabsList.removeAt(binding.myPager.currentItem)
                binding.myPager.adapter?.notifyDataSetChanged()
                binding.myPager.currentItem = tabsList.size - 1

            }
            else -> super.onBackPressed()
        }
    }


    private inner class TabsAdapter(fa: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fa, lc) {
        override fun getItemCount(): Int = tabsList.size

        override fun createFragment(position: Int): Fragment = tabsList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeTab(url: String, fragment: Fragment){
        tabsList.add(fragment)
        binding.myPager.adapter?.notifyDataSetChanged()
        binding.myPager.currentItem = tabsList.size - 1
    }

    fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun initializeView(){
        binding.settingBtn.setOnClickListener {
            var frag:BrowseFragment? = null
            try {
                frag = tabsList[binding.myPager.currentItem] as BrowseFragment
            }catch (e:Exception){}

            val view = layoutInflater.inflate(R.layout.more_features, binding.root, false)
            val dialogBinding = MoreFeaturesBinding.bind(view)

            val dialog = MaterialAlertDialogBuilder(this).setView(view).create()

            dialog.window?.apply {
                attributes.gravity = Gravity.BOTTOM
                attributes.y = 50
                setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
            }
            dialog.show()

            dialogBinding.backBtn.setOnClickListener {
                onBackPressed()
            }

            dialogBinding.forwardBtn.setOnClickListener {
                frag?.apply {
                    if(binding.webView.canGoForward())
                        binding.webView.goForward()
                }
            }

            dialogBinding.saveBtn.setOnClickListener {
                if(frag != null)
                    saveAsPdf(web = frag.binding.webView)
                else Snackbar.make(binding.root, "First Open A WebPage\uD83D\uDE03", 3000).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        printJob?.let {
            when{
                it.isCompleted -> Snackbar.make(binding.root, "Successful -> ${it.info.label}", 4000).show()
                it.isFailed -> Snackbar.make(binding.root, "Failed -> ${it.info.label}", 4000).show()
            }
        }
    }

    private fun saveAsPdf(web: WebView){
        val pm = getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "${URL(web.url).host}_${SimpleDateFormat("HH:mm d_MMM_yy", Locale.ENGLISH)
            .format(Calendar.getInstance().time)}"
        val printAdapter = web.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder()
        printJob = pm.print(jobName, printAdapter, printAttributes.build())
    }
}