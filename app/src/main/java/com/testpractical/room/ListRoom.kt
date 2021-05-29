package com.testpractical.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class ListRoom : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(email = "email")
    var email: String? = null

    @ColumnInfo(gender = "gender")
    var gender: String? = null

    @ColumnInfo(status = "status")
    var status: String? = null

    @ColumnInfo(created_at = "created_at")
    var created_at: String? = null

    @ColumnInfo(updated_at = "updated_at")
    var updated_at: String? = null
}