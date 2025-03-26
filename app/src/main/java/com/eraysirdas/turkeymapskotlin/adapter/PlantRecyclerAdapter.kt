package com.eraysirdas.turkeymapskotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eraysirdas.turkeymapskotlin.utils.Constant
import com.eraysirdas.turkeymapskotlin.databinding.PlantRowBinding
import com.eraysirdas.turkeymapskotlin.model.CityPlantModel

class PlantRecyclerAdapter(private val plantArrayList : ArrayList<CityPlantModel>) : RecyclerView.Adapter<PlantRecyclerAdapter.PostHolder>() {
    class PostHolder(val binding : PlantRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = PlantRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)

    }

    override fun getItemCount(): Int {
        return plantArrayList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.rowHeadTv.text = plantArrayList[position].plantName
        holder.binding.rowDescriptionTv.text = plantArrayList[position].plantDescription

        val imageUrl = Constant.plantImageBaseUrl +plantArrayList[position].plantImageUrl

        Glide.with(holder.binding.root)
            .load(imageUrl)
            .into(holder.binding.rowImageView)

    }
}