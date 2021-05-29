package com.testpractical.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.testpractical.R
import com.testpractical.databinding.ActivityMainBinding
import com.testpractical.ui.fragment.MetadataFragment
import com.testpractical.ui.fragment.UsersFragment
import java.util.*


class MainActivity : AppCompatActivity(), UsersFragment.SendUserData {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        /*FOR PASS DATA BETWEEN TWO FRAGMENTS*/try {
            // Register receiver
            LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver,
                IntentFilter("USER_CLICK")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(UsersFragment(), "Users")
        adapter.addFragment(MetadataFragment(), "Metadata")
        viewPager.adapter = adapter
    }

    internal class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    /*Change ViewPager*/
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            /*Change Fragment*/
            binding.viewPager.currentItem = 1
        }
    }

    /*Send Data to Second Fragment*/
    override fun sendData(
        id: Int,
        name: String?,
        gender: String?,
        email: String?,
        status: String?,
        createdAt: String?,
        updatedAt: String?
    ) {
        val tag = "android:switcher:" + R.id.viewPager.toString() + ":" + 1
        val metadataFragment: MetadataFragment? =
            supportFragmentManager.findFragmentByTag(tag) as MetadataFragment?
        metadataFragment!!.displayReceivedData(
            name, id, gender,
            email,
            status,
            createdAt,
            updatedAt
        )
    }
}