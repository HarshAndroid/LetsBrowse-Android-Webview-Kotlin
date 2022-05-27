package com.harshrajpurohit.letsbrowse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.harshrajpurohit.letsbrowse.R
import com.harshrajpurohit.letsbrowse.activity.MainActivity
import com.harshrajpurohit.letsbrowse.databinding.BookmarkViewBinding
import com.harshrajpurohit.letsbrowse.fragment.BrowseFragment

class BookmarkAdapter(private val context: Context): RecyclerView.Adapter<BookmarkAdapter.MyHolder>() {

    private val colors = context.resources.getIntArray(R.array.myColors)

    class MyHolder(binding: BookmarkViewBinding):RecyclerView.ViewHolder(binding.root) {
        val image = binding.bookmarkIcon
        val name = binding.bookmarkName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(BookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.image.setBackgroundColor(colors[(colors.indices).random()])
        holder.image.text = MainActivity.bookmarkList[position].name[0].toString()
        holder.name.text = MainActivity.bookmarkList[position].name

        holder.root.setOnClickListener{
            context as MainActivity
            when{
                context.checkForInternet(context) -> context.changeTab(MainActivity.bookmarkList[position].name,
                BrowseFragment(urlNew = MainActivity.bookmarkList[position].url))
                else -> Snackbar.make(holder.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return MainActivity.bookmarkList.size
    }
}