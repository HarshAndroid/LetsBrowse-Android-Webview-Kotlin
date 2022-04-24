package com.harshrajpurohit.letsbrowse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harshrajpurohit.letsbrowse.activity.MainActivity
import com.harshrajpurohit.letsbrowse.databinding.BookmarkViewBinding

class BookmarkAdapter(private val context: Context): RecyclerView.Adapter<BookmarkAdapter.MyHolder>() {

    class MyHolder(binding: BookmarkViewBinding):RecyclerView.ViewHolder(binding.root) {
        val image = binding.bookmarkIcon
        val name = binding.bookmarkName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(BookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.image.text = MainActivity.bookmarkList[position].name[0].toString()
        holder.name.text = MainActivity.bookmarkList[position].url
    }

    override fun getItemCount(): Int {
        return MainActivity.bookmarkList.size;
    }
}