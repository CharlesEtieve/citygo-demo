package com.eurosportdemo.app.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eurosportdemo.app.presentation.fragment.BasketFragment
import com.eurosportdemo.app.presentation.fragment.StoreFragment

class MainPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> StoreFragment()
            else -> BasketFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}