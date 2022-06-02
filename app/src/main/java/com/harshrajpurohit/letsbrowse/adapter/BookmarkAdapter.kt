package com.harshrajpurohit.letsbrowse.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.harshrajpurohit.letsbrowse.R
import com.harshrajpurohit.letsbrowse.activity.MainActivity
import com.harshrajpurohit.letsbrowse.activity.changeTab
import com.harshrajpurohit.letsbrowse.activity.checkForInternet
import com.harshrajpurohit.letsbrowse.databinding.BookmarkViewBinding
import com.harshrajpurohit.letsbrowse.databinding.LongBookmarkViewBinding
import com.harshrajpurohit.letsbrowse.fragment.BrowseFragment

class BookmarkAdapter(private val context: Context, private val isActivity: Boolean = false): RecyclerView.Adapter<BookmarkAdapter.MyHolder>() {

    private val colors = context.resources.getIntArray(R.array.myColors)

    class MyHolder(binding: BookmarkViewBinding? = null, bindingL: LongBookmarkViewBinding? = null)
        :RecyclerView.ViewHolder((binding?.root ?: bindingL?.root)!!) {
        val image = (binding?.bookmarkIcon ?: bindingL?.bookmarkIcon)!!
        val name = (binding?.bookmarkName ?: bindingL?.bookmarkName)!!
        val root = (binding?.root ?: bindingL?.root)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        if(isActivity)
            return MyHolder(bindingL = LongBookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
        return MyHolder(binding = BookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        try {
            val icon = BitmapFactory.decodeByteArray(MainActivity.bookmarkList[position].image, 0,
                MainActivity.bookmarkList[position].image!!.size)
            holder.image.background = icon.toDrawable(context.resources)
        }catch (e: Exception){
            holder.image.setBackgroundColor(colors[(colors.indices).random()])
            holder.image.text = MainActivity.bookmarkList[position].name[0].toString()
        }
        holder.name.text = MainActivity.bookmarkList[position].name

        holder.root.setOnClickListener{
            when{
                checkForInternet(context) -> {
                    changeTab(MainActivity.bookmarkList[position].name,
                BrowseFragment(urlNew = MainActivity.bookmarkList[position].url))
                    if(isActivity) (context as Activity).finish()
                }
                else -> Snackbar.make(holder.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return MainActivity.bookmarkList.size
    }
}