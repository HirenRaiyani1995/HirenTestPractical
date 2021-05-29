package com.testpractical.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ListDao {
    @get:Query("SELECT * FROM ListRoom")
    val all: List<ListRoom?>?

    @Insert
    fun insert(userSelectRoom: ListRoom?)

    @Query("DELETE FROM ListRoom WHERE email = :email")
    fun deleteId(email: String?)

    @Query("SELECT * FROM ListRoom where email = :email")
    fun emailValidation(email: String?): ListRoom?

    @Query("SELECT * FROM ListRoom ORDER BY name ASC")
    fun getPersonsSortByAscName(): List<ListRoom?>?

    @Query("SELECT * FROM ListRoom where status = :Status")
    fun getSortByStatus(Status : String): List<ListRoom?>?
}