package com.eraysirdas.turkeymapskotlin.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.eraysirdas.turkeymapskotlin.utils.Constant
import com.eraysirdas.turkeymapskotlin.model.MapsModel
import com.eraysirdas.turkeymapskotlin.adapter.TabPagerAdapter
import com.eraysirdas.turkeymapskotlin.databinding.ActivityDetailsBinding
import com.google.android.material.tabs.TabLayoutMediator

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var mapsModel: MapsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mapsModel = (intent.getSerializableExtra("model") as MapsModel?)!! //parcelable ile güncelle

        binding.areaTv.text = "Bölge : ${mapsModel.area}"
        binding.districtCountsTv.text="İlçe Sayısı: ${mapsModel.districtCount}"
        binding.hiveCountTv.text="Kovan Sayısı : ${mapsModel.hiveCount}"
        binding.producerCountTv.text="Üretici Sayısı : ${mapsModel.produceCount}"

        val imageUrl = Constant.cityImageBaseUrl + mapsModel.cityMapImage

        Glide.with(this)
            .load(imageUrl)
            .into(binding.imageView)

        supportActionBar?.title=mapsModel.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tabPagerAdapter  = TabPagerAdapter(this,mapsModel.name!!)
        binding.viewPager.adapter = tabPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.setText("Bitkiler")
                1 -> tab.setText("Ballar")
            }
        }.attach()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}