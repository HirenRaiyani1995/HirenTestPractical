package com.testpractical.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.testpractical.R
import com.testpractical.adapter.AllUserListAdapter
import com.testpractical.databinding.FragmentUsersBinding
import com.testpractical.model.userListModel.DataItem
import com.testpractical.model.userListModel.GetAllUserResponse
import com.testpractical.retrofit.ApiInterface
import com.testpractical.retrofit.RestConstant
import com.testpractical.retrofit.RetrofitService
import com.testpractical.room.AppDatabase
import com.testpractical.room.ListRoom
import com.testpractical.ui.dialog.CreateUserDialog
import com.testpractical.utils.AppUtils
import com.testpractical.viewModel.UserViewModel
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


class UsersFragment : Fragment() {
    private var binding: FragmentUsersBinding? = null
    private var allUserList: ArrayList<DataItem> = ArrayList()
    private var allUserListAdapter: AllUserListAdapter? = null
    private var userViewModel: UserViewModel? = null
    private var createUserDialog: CreateUserDialog? = null
    private lateinit var selectedGender: String
    private lateinit var selectedStatus: String

    //For Room Database
    private var alldataList: List<ListRoom>? = null
    private lateinit var listData: ListRoom

    /*For Interface*/
    private var apiInterface: ApiInterface? = null

    /*For Sending Data to another Fragment or Activity*/
    var SM: SendUserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UseRequireInsteadOfGet", "FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        //Initialization
        createUserDialog = CreateUserDialog(context)
        apiInterface = RetrofitService.createService(ApiInterface::class.java)
        listData = ListRoom()
        alldataList = ArrayList()

        binding!!.rgOrder.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_normal -> {
                    /*Get Normal Data*/
                    getList()
                }
                R.id.rb_asc -> {
                    /*Get ASC Ordered data by Name*/
                    getASCList()
                }
                R.id.rb_active -> {
                    /*Get Active User Data*/
                    getActiveUserList()
                }
            }
        })

        /*Check Internet Connection*/
        if (AppUtils.isConnectedToInternet(activity!!)) {
            userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
            userViewModel!!.init()
            userViewModel!!.allUserRepository()!!.observe(
                this,
                { userListResponse: GetAllUserResponse? ->
                    val dataItem = userListResponse!!.data
                    allUserList.clear()
                    allUserList.addAll(dataItem!!)
                    for (i in 0 until allUserList.size) {
                        checkEmailDuplicatation(
                            allUserList.get(i).name!!,
                            allUserList.get(i).email!!,
                            allUserList.get(i).gender!!,
                            allUserList.get(i).status!!,
                            allUserList.get(i).createdAt!!,
                            allUserList.get(i).updatedAt!!
                        )
                    }
                })
        } else {
            /*Get Local Storage data when Internet Connection is not available*/
            getList()
        }


        /*Create User Dialog Button*/
        binding!!.btnAddUser.setOnClickListener {
            createUserDialog!!.show()
            createUserDialog!!.et_emailID!!.setText("")
            createUserDialog!!.et_name!!.setText("")

            /*Create User Button OnClick Method to insert a new user*/
            createUserDialog!!.btn_createUser!!.setOnClickListener {
                if (createUserValidation()) {
                    /*Check Gender is selected Or Not*/
                    if (createUserDialog!!.rg_gender!!.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(
                            activity,
                            getString(R.string.select_gender),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (createUserDialog!!.rb_male!!.isChecked) {
                            selectedGender = "Male"
                        } else {
                            selectedGender = "Female"
                        }
                    }

                    /*Check Status is selected Or Not*/
                    if (createUserDialog!!.rg_status!!.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(
                            activity,
                            getString(R.string.select_status),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (createUserDialog!!.rb_active!!.isChecked) {
                            selectedStatus = "Active"
                        } else {
                            selectedStatus = "InActive"
                        }
                    }

                    if (createUserDialog!!.rb_male!!.isChecked || createUserDialog!!.rb_female!!.isChecked &&
                        createUserDialog!!.rb_active!!.isChecked || createUserDialog!!.rb_inactive!!.isChecked
                    ) {
                        /*Create user API Call*/
                        createUserApiCall()
                    }
                }
            }
        }
        return binding!!.root
    }

    /*Validation for Insert New User Data*/
    private fun createUserValidation(): Boolean {
        return when {
            TextUtils.isEmpty(AppUtils.getText(createUserDialog!!.et_name!!)) -> {
                Toast.makeText(activity, getString(R.string.please_enter_name), Toast.LENGTH_SHORT)
                    .show()
                false
            }
            TextUtils.isEmpty(AppUtils.getText(createUserDialog!!.et_emailID!!)) -> {
                Toast.makeText(activity, getString(R.string.please_enter_email), Toast.LENGTH_SHORT)
                    .show()
                false
            }
            !AppUtils.isEmailValid(AppUtils.getText(createUserDialog!!.et_emailID!!)) -> {
                Toast.makeText(
                    activity,
                    getString(R.string.please_enter_valid_email),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            else -> {
                true
            }
        }
    }

    /*Setup Recycler View here*/
    private fun setupRecyclerView(datalist: List<ListRoom>?) {
        if (datalist!!.size > 0) {
            allUserListAdapter = AllUserListAdapter(context, datalist, this)
            binding!!.rvAllUserList.setLayoutManager(LinearLayoutManager(context))
            binding!!.rvAllUserList.setAdapter(allUserListAdapter)
            binding!!.rvAllUserList.setItemAnimator(DefaultItemAnimator())
            binding!!.rvAllUserList.setNestedScrollingEnabled(true)
            allUserListAdapter!!.notifyDataSetChanged()
            binding!!.rvAllUserList.visibility = View.VISIBLE
            binding!!.tvNoDataFound.visibility = View.GONE
        } else {
            binding!!.rvAllUserList.visibility = View.GONE
            binding!!.tvNoDataFound.visibility = View.VISIBLE
        }
    }

    //Insert Data in Database
    fun insertDataList(
        name: String,
        email: String,
        gender: String,
        status: String,
        created_at: String,
        updated_at: String
    ) {
        @SuppressLint("StaticFieldLeak")
        class InsertData : AsyncTask<Void?, Void?, Void?>() {

            override fun doInBackground(vararg p0: Void?): Void? {
                listData.name = name
                listData.email = email
                listData.status = status
                listData.gender = gender
                listData.created_at = created_at
                listData.updated_at = updated_at

                //adding to database
                AppDatabase.getInstance(context!!)
                    .listDao()
                    ?.insert(listData)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                /*Get All Local Storage data*/
                getList()
            }
        }

        val st = InsertData()
        st.execute()
    }


    /*Get All Stored Data From the Room database*/
    fun getList() {
        @SuppressLint("StaticFieldLeak")
        class GetList : AsyncTask<Void?, Void?, List<ListRoom>?>() {
            override fun doInBackground(vararg p0: Void?): List<ListRoom>? {
                alldataList = AppDatabase
                    .getInstance(context!!)
                    .listDao()
                    ?.all as List<ListRoom>?
                return alldataList
            }

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(datalist: List<ListRoom>?) {
                super.onPostExecute(datalist)
                setupRecyclerView(datalist)
            }
        }

        val gt = GetList()
        gt.execute()
    }


    /*Get ASC Data From the Room database*/
    fun getASCList() {
        @SuppressLint("StaticFieldLeak")
        class GetList : AsyncTask<Void?, Void?, List<ListRoom>?>() {
            override fun doInBackground(vararg p0: Void?): List<ListRoom>? {
                alldataList = AppDatabase
                    .getInstance(context!!)
                    .listDao()
                    ?.getPersonsSortByAscName() as List<ListRoom>?
                return alldataList
            }

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(datalist: List<ListRoom>?) {
                super.onPostExecute(datalist)
                setupRecyclerView(datalist)
            }
        }

        val gt = GetList()
        gt.execute()
    }


    /*Get Active User Data From the Room database*/
    fun getActiveUserList() {
        @SuppressLint("StaticFieldLeak")
        class GetList : AsyncTask<Void?, Void?, List<ListRoom>?>() {
            override fun doInBackground(vararg p0: Void?): List<ListRoom>? {
                alldataList = AppDatabase
                    .getInstance(context!!)
                    .listDao()
                    ?.getSortByStatus("Active") as List<ListRoom>?
                return alldataList
            }

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(datalist: List<ListRoom>?) {
                super.onPostExecute(datalist)
                setupRecyclerView(datalist)
            }
        }

        val gt = GetList()
        gt.execute()
    }


    /*Delete User Data From the Room Database*/
    fun deleteListData(email: String, id: Int) {
        @SuppressLint("StaticFieldLeak")
        class DeleteTask : AsyncTask<Void?, Void?, Void?>() {
            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                getList()
            }

            override fun doInBackground(vararg p0: Void?): Void? {
                try {
                    AppDatabase.getInstance(context!!)
                        .listDao()
                        ?.deleteId(email)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }
        }

        val dt = DeleteTask()
        dt.execute()
    }

    /*Check data duplication using Email ID*/
    fun checkEmailDuplicatation(
        name: String,
        email: String,
        gender: String,
        status: String,
        created_at: String,
        updated_at: String
    ) {
        @SuppressLint("StaticFieldLeak")
        class CheckValidation : AsyncTask<Void?, Void?, ListRoom?>() {
            override fun onPostExecute(data: ListRoom?) {
                super.onPostExecute(data)
                if (data != null) {
                    if (!data.email.equals(email)) {
                        insertDataList(name, email, gender, status, created_at, updated_at)
                    } else {
                        getList()
                    }
                } else {
                    insertDataList(name, email, gender, status, created_at, updated_at)
                }
            }

            override fun doInBackground(vararg p0: Void?): ListRoom? {
                return AppDatabase.getInstance(context!!)
                    .listDao()
                    ?.emailValidation(email)
            }
        }

        val st = CheckValidation()
        st.execute()
    }


    /**
     * Create User Data API Call
     */
    @SuppressLint("UseRequireInsteadOfGet")
    private fun createUserApiCall() {
        try {
            if (AppUtils.isConnectedToInternet(activity!!)) {
                val params = JSONObject()
                try {
                    params.put("name", AppUtils.getText(createUserDialog!!.et_name!!))
                    params.put("email", AppUtils.getText(createUserDialog!!.et_emailID!!))
                    params.put("gender", selectedGender)
                    params.put("status", selectedStatus)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val param = JsonParser.parseString(params.toString()) as JsonObject
                val call: Call<JsonObject>
                call = apiInterface!!.createUserAPICall(param, RestConstant.TOKEN)
                Log.e("--->createUser", param.toString())
                call.enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(
                        call: Call<JsonObject?>,
                        response: Response<JsonObject?>
                    ) {
                        if (response.isSuccessful()) {
                            val json = JSONObject(response.body().toString())
                            val code = json.getString("code")
                            if (code.equals("422")) {
                                Toast.makeText(activity, "Email Already Added", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val jsonData = json.getJSONObject("data")
                                val name = jsonData.getString("name")
                                val email = jsonData.getString("email")
                                val gender = jsonData.getString("gender")
                                val status = jsonData.getString("status")
                                val created_at = jsonData.getString("created_at")
                                val updated_at = jsonData.getString("updated_at")

                                /*Inserted data add in room database*/
                                checkEmailDuplicatation(
                                    name,
                                    email,
                                    gender,
                                    status,
                                    created_at,
                                    updated_at
                                )
                                createUserDialog!!.dismiss()
                                Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        onFailureCall(activity, t)
                    }
                })
            } else {
                /*Inserted data in room database when Internet is not available*/
                checkEmailDuplicatation(
                    AppUtils.getText(createUserDialog!!.et_name!!),
                    AppUtils.getText(createUserDialog!!.et_emailID!!),
                    selectedGender,
                    selectedStatus,
                    "2021-05-28T03:50:04.006+05:30",
                    "2021-05-28T03:50:04.006+05:30"
                )
                createUserDialog!!.dismiss()
                Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onFailureCall(ctx: Context?, t: Throwable) {
        Log.e("-->OnFailure", t.message!!)
        Toast.makeText(ctx, t.message, Toast.LENGTH_SHORT).show()
        try {
            if (t is SocketTimeoutException) {
                Toast.makeText(ctx, getString(R.string.connection_timeout), Toast.LENGTH_SHORT)
                    .show()
            } else {
                t.printStackTrace()
                Toast.makeText(ctx, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /*Delete record Alert Dialog*/
    fun deleteRecord(email: String, id: Int) {
        AlertDialog.Builder(activity)
            .setMessage(getString(R.string.are_you_sure_delete))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, i ->
                dialogInterface.dismiss()
                deleteUserApiCall(email, id)
            }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, i -> dialogInterface.dismiss() }
            .show()
    }

    /**
     * DELETE USER API CALL
     */
    @SuppressLint("UseRequireInsteadOfGet")
    private fun deleteUserApiCall(email: String, id: Int) {
        try {
            if (AppUtils.isConnectedToInternet(activity!!)) {

                val call: Call<JsonObject>
                call = apiInterface!!.deleteUserAPICall(id, RestConstant.TOKEN)
                Log.e("--->deleteUser", id.toString())
                call.enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(
                        call: Call<JsonObject?>,
                        response: Response<JsonObject?>
                    ) {
                        if (response.isSuccessful()) {
                            val json = JSONObject(response.body().toString())
                            val code = json.getString("code")
                            if (code.equals("204")) {
                                Toast.makeText(
                                    activity,
                                    "Delete User Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                /*Deleted data in room database when Internet is not available*/
                                deleteListData(email, id)
                            } else {
                                /*Deleted data from only room database*/
                                deleteListData(email, id)
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        onFailureCall(activity, t)
                    }
                })
            } else {
                /*Deleted data in room database when Internet is not available*/
                deleteListData(email, id)
                Toast.makeText(activity, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*Click On Any Users*/
    @SuppressLint("UseRequireInsteadOfGet")
    fun selectUser(
        id: Int,
        name: String?,
        gender: String?,
        email: String?,
        status: String?,
        createdAt: String?,
        updatedAt: String?
    ) {
        /*Send Data to another Fragment*/
        SM!!.sendData(id, name, gender, email, status, createdAt, updatedAt)
        /*Change Viewpager Fragement on click*/
        val i1 = Intent("USER_CLICK")
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(i1)
    }

    /*Interface For Sending Data to Another Screen*/
    interface SendUserData {
        fun sendData(
            id: Int,
            name: String?,
            gender: String?,
            email: String?,
            status: String?,
            createdAt: String?,
            updatedAt: String?
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            SM = activity as SendUserData?
        } catch (e: ClassCastException) {
            throw ClassCastException("Error in retrieving data. Please try again")
        }
    }
}