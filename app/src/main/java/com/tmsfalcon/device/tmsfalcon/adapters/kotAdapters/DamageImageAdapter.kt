package com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.BeCareful
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.HolisticViewOfDamage
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.IncludeNoPlate

class DamageImageAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager!!) {
    // Returns total number of pages
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BeCareful.newInstance(0,"Page # 1")!!
            1 -> HolisticViewOfDamage.newInstance(1, "Page # 2")!!
            2 -> IncludeNoPlate.newInstance(2, "Page # 3")!!

            else -> BeCareful.newInstance(0,"Page # 1")!!
        }
    }



    companion object {
        private const val NUM_ITEMS = 3
    }
}