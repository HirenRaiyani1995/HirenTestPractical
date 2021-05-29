package com.testpractical.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.testpractical.adapter.AllUserListAdapter.MyViewHolder
import com.testpractical.databinding.RowItemUserBinding
import com.testpractical.room.ListRoom
import com.testpractical.ui.fragment.UsersFragment

class AllUserListAdapter(var context: Context?, var allUserList: List<ListRoom>,val activity: UsersFragment) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = RowItemUserBinding.inflate(layoutInflater, viewGroup, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val dataItem = allUserList.get(i)
        myViewHolder.binding.tvName.text = dataItem.name
        myViewHolder.binding.tvEmail.text = dataItem.email
        myViewHolder.binding.tvGender.text = dataItem.gender

        /*Button Delete OnCLick Method*/
        myViewHolder.binding.btnDelete.setOnClickListener {
            activity.deleteRecord(dataItem.email!!,dataItem.id)
        }

        /*Select Perticular user Onclick Method*/
        myViewHolder.binding.rowItem.setOnClickListener{
            activity.selectUser(dataItem.id,dataItem.name,dataItem.gender,dataItem.email,dataItem.status,dataItem.created_at,dataItem.updated_at)
        }
    }

    override fun getItemCount(): Int {
        return allUserList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(public val binding: RowItemUserBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}