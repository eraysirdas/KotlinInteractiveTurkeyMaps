package com.eraysirdas.turkeymapskotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eraysirdas.turkeymapskotlin.databinding.HoneyRowBinding
import com.eraysirdas.turkeymapskotlin.model.CityHoneyModel

class HoneyRecyclerAdapter (private var honeyArrayList : ArrayList<CityHoneyModel>): RecyclerView.Adapter<HoneyRecyclerAdapter.PostHolder>(){
    class PostHolder (val binding : HoneyRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = HoneyRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return honeyArrayList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.honeyRowTv.text = honeyArrayList[position].honeyVerietyName
    }
}