package com.testpractical.repository

import androidx.lifecycle.MutableLiveData
import com.testpractical.model.userListModel.GetAllUserResponse
import com.testpractical.retrofit.ApiInterface
import com.testpractical.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListRepository {
    private val apiInterface: ApiInterface?

    /*Fetch All User Data API Call*/
    fun allUserList(): MutableLiveData<GetAllUserResponse?> {
        val userData = MutableLiveData<GetAllUserResponse?>()
        apiInterface!!.getAllUserListAPICall().enqueue(object : Callback<GetAllUserResponse?> {
            override fun onResponse(
                call: Call<GetAllUserResponse?>,
                response: Response<GetAllUserResponse?>
            ) {
                if (response.isSuccessful) {
                    userData.value = response.body()
                }
            }

            override fun onFailure(call: Call<GetAllUserResponse?>, t: Throwable) {
                userData.value = null
            }
        })
        return userData
    }

    companion object {
        private var userListRepository: UserListRepository? = null
        val instance: UserListRepository?
            get() {
                if (userListRepository == null) {
                    userListRepository = UserListRepository()
                }
                return userListRepository
            }
    }

    init {
        apiInterface = RetrofitService.createService(ApiInterface::class.java)
    }
}