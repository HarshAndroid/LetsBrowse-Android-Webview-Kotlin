package com.harshRajpurohit.letsBrowse.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.harshRajpurohit.letsBrowse.activity.MainActivity
import com.harshRajpurohit.letsBrowse.databinding.TabBinding

class TabAdapter(private val context: Context, private val dialog: AlertDialog): RecyclerView.Adapter<TabAdapter.MyHolder>() {

    class MyHolder(binding: TabBinding) :RecyclerView.ViewHolder(binding.root) {
        val cancelBtn = binding.cancelBtn
        val name = binding.tabName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(TabBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = MainActivity.tabsList[position].name
        holder.root.setOnClickListener {
            MainActivity.myPager.currentItem = position
            dialog.dismiss()
        }

        holder.cancelBtn.setOnClickListener {
            if(MainActivity.tabsList.size == 1 || position == MainActivity.myPager.currentItem)
                Snackbar.make(MainActivity.myPager, "Can't Remove this tab", 3000).show()
            else{
                MainActivity.tabsList.removeAt(position)
                notifyDataSetChanged()
                MainActivity.myPager.adapter?.notifyItemRemoved(position)
            }


        }
    }

    override fun getItemCount(): Int {
        return MainActivity.tabsList.size
    }
}