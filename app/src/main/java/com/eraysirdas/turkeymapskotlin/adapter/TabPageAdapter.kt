package com.eraysirdas.turkeymapskotlin.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eraysirdas.turkeymapskotlin.fragment.HoneyFragment
import com.eraysirdas.turkeymapskotlin.fragment.PlantFragment

class TabPagerAdapter(fragmentActivity: FragmentActivity, private val cityName: String) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = PlantFragment()
                val bundle = Bundle()
                bundle.putString("cityName", cityName) // Şehir adını Bundle ile gönder
                fragment.arguments = bundle
                fragment
            }

            1 -> {
                val fragment = HoneyFragment()
                val bundle = Bundle()
                bundle.putString("cityName", cityName)
                fragment.arguments = bundle
                fragment
            }
            else -> {
                throw IllegalStateException("Invalid position")
            }
        }
    }


    override fun getItemCount(): Int {
        return 2
    }
}