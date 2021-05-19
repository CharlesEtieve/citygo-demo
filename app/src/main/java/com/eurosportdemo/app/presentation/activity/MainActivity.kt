package com.eurosportdemo.app.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eurosportdemo.app.databinding.ActivityMainBinding
import com.eurosportdemo.app.presentation.adapter.MainPagerAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager))
    }
}