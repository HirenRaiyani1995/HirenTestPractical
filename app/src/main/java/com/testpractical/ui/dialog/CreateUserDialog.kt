package com.testpractical.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioGroup
import androidx.appcompat.widget.*
import com.google.android.material.button.MaterialButton
import com.testpractical.R
import java.util.*

class CreateUserDialog(context: Context?) : Dialog(context!!) {

    var et_name: AppCompatEditText? = null
    var et_emailID: AppCompatEditText? = null
    var rb_male: AppCompatRadioButton? = null
    var rb_female: AppCompatRadioButton? = null
    var rb_active: AppCompatRadioButton? = null
    var rb_inactive: AppCompatRadioButton? = null
    var rg_status: RadioGroup? = null
    var rg_gender: RadioGroup? = null
    var btn_createUser: AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_create_user)

        val v = Objects.requireNonNull(window)!!.decorView
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window!!.setGravity(Gravity.CENTER)
        v.setBackgroundResource(R.color.transparent)
        setCanceledOnTouchOutside(false)
        setCancelable(true)

        v.setOnClickListener {
            this.dismiss()
        }

        et_name = findViewById(R.id.et_name)
        et_emailID = findViewById(R.id.et_emailID)
        rb_male = findViewById(R.id.rb_male)
        rb_female = findViewById(R.id.rb_female)
        rb_active = findViewById(R.id.rb_active)
        rb_inactive = findViewById(R.id.rb_inactive)
        rg_gender = findViewById(R.id.rg_gender)
        rg_status = findViewById(R.id.rg_status)
        btn_createUser = findViewById(R.id.btn_createUser)
    }
}