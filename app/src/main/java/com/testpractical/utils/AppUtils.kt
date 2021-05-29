package com.testpractical.utils

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.testpractical.R
import java.util.regex.Pattern


object AppUtils {
    private const val APP_TAG = "Test"

    fun logString(message: String?): Int {
        return Log.i(APP_TAG, message!!)
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }


    @JvmStatic
    fun getText(textView: TextView): String {
        return textView.text.toString().trim { it <= ' ' }
    }

    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    // Check EditText or String is Empty or null etc.
    fun isEmpty(str: String?): Boolean {
        return TextUtils.isEmpty(str)
    }

    @JvmStatic
    fun isConnectedToInternet(context: Context): Boolean {
        val cm = TestPracticleApp.testPracticleApp!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: NetworkInfo? = null
        if (cm != null) {
            netInfo = cm.activeNetworkInfo
        }
        return netInfo != null && netInfo.isConnected && netInfo.isAvailable
    }

    fun showAlertDialog(context: Context, title: String?, message: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.ok)) { dialog, which -> dialog.dismiss() }
        builder.show()
    }
}