package com.testpractical.model.userPostModel

import com.google.gson.annotations.SerializedName

data class UserPostResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)