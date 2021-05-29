package com.testpractical.model.userListModel

import com.google.gson.annotations.SerializedName

class GetAllUserResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItem>? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)