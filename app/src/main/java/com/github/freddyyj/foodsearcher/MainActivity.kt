package com.github.freddyyj.foodsearcher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.github.freddyyj.foodsearcher.ui.main.PageViewModel
import com.github.freddyyj.foodsearcher.ui.main.ResultFragment
import com.github.freddyyj.foodsearcher.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null && p0.position==1) {
                    val result=sectionsPagerAdapter.getItem(0)
                    val viewModel = ViewModelProviders.of(result).get(PageViewModel::class.java)
                    Log.println(Log.WARN,"uri warn","URI has ${viewModel.recognizedText}")
                    val url= Uri.parse("https://www.google.com/search?q=${viewModel.recognizedText}")
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    startActivity(intent)
                }
            }

        })
    }

}