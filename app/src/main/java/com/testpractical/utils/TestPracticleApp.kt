package com.testpractical.utils

import android.app.Activity
import android.app.Application


class TestPracticleApp : Application() {
    private var activity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        testPracticleApp = this
    }

    companion object {
        @JvmStatic
        var testPracticleApp: TestPracticleApp? = null
            private set
    }

    fun getActivity(): Activity? {
        return activity
    }

    fun setActivity(activity: Activity?) {
        this.activity = activity
    }
}