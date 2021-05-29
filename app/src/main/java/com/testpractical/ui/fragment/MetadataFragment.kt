package com.testpractical.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testpractical.R
import com.testpractical.adapter.UserPostListAdapter
import com.testpractical.databinding.FragmentMetadataBinding
import com.testpractical.model.userPostModel.DataItem
import com.testpractical.model.userPostModel.UserPostResponse
import com.testpractical.retrofit.ApiInterface
import com.testpractical.retrofit.RetrofitService
import com.testpractical.utils.AppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.ArrayList


class MetadataFragment : Fragment() {
    private var binding: FragmentMetadataBinding? = null
    private var apiInterface: ApiInterface? = null
    var userPostList: ArrayList<DataItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMetadataBinding.inflate(inflater, container, false)

        apiInterface = RetrofitService.createService(ApiInterface::class.java)

        return binding!!.root
    }

    /*Display Users Receiving Data*/
    fun displayReceivedData(
        name: String?,
        id: Int,
        gender: String?,
        email: String?,
        status: String?,
        createdAt: String?,
        updatedAt: String?
    ) {
        if(id != null){
           binding!!.llUserInfo.visibility = View.VISIBLE
           binding!!.tvNoUserSelected.visibility = View.GONE
        }else{
            binding!!.llUserInfo.visibility = View.GONE
            binding!!.tvNoUserSelected.visibility = View.VISIBLE
        }

        binding!!.tvName.text = name
        binding!!.tvGender.text = gender
        binding!!.tvEmail.text = email
        binding!!.tvStatus.text = status
        binding!!.tvCreatedDate.text = createdAt
        binding!!.tvUpdatedDate.text = updatedAt

        getPostListAPICall(id)
    }


    private fun getPostListAPICall(id: Int) {
        if (activity?.let { AppUtils.isConnectedToInternet(it) }!!) {
            val call: Call<UserPostResponse>
            call = apiInterface!!.getAllPostListAPICall(id)
            Log.e("--->getPostList", id.toString())
            call.enqueue(object : Callback<UserPostResponse?> {
                override fun onResponse(call: Call<UserPostResponse?>, response: Response<UserPostResponse?>) {
                    val userPostResponse: UserPostResponse
                    if (response.isSuccessful) {
                        userPostResponse = response.body()!!
                        when (userPostResponse.code) {
                            200 -> {
                                userPostList = userPostResponse.data as ArrayList<DataItem>
                                if(userPostList!!.size > 0) {
                                    val userPostListAdapter =
                                        UserPostListAdapter(context, userPostList!!)
                                    val mLayoutManager1: RecyclerView.LayoutManager =
                                        LinearLayoutManager(activity)
                                    binding!!.rvPostList.layoutManager = mLayoutManager1
                                    binding!!.rvPostList.adapter = userPostListAdapter
                                    binding!!.tvNoDataFound.visibility = View.GONE
                                    binding!!.rvPostList.visibility = View.VISIBLE
                                }else{
                                    binding!!.tvNoDataFound.visibility = View.VISIBLE
                                    binding!!.rvPostList.visibility = View.GONE
                                }
                            }
                        }
                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserPostResponse?>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Toast.makeText(activity, getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show()
                    } else {
                        t.printStackTrace()
                        Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Toast.makeText(activity,  getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
}