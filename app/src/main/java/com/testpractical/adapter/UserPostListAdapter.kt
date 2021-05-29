package com.testpractical.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.testpractical.databinding.RowItemPostBinding
import com.testpractical.adapter.UserPostListAdapter.MyViewHolder
import com.testpractical.model.userPostModel.DataItem

class UserPostListAdapter(var context: Context?, var allPostList: List<DataItem>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = RowItemPostBinding.inflate(layoutInflater, viewGroup, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val dataItem = allPostList.get(i)
        myViewHolder.binding.tvUserId.text = dataItem.userId.toString()
        myViewHolder.binding.tvTitle.text = dataItem.title
        myViewHolder.binding.tvBody.text = dataItem.body
    }

    override fun getItemCount(): Int {
        return allPostList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(public val binding: RowItemPostBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}