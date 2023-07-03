package com.example.happyplaces.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.ItemBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceAdapter(private val items:ArrayList<HappyPlaceModel>):RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemBinding):RecyclerView.ViewHolder(binding.root) {
        val namee=binding.tvName
        val desc=binding.tvDesc
        val image= binding.civPlaceImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context=holder.itemView.context
        val item=items[position]
        holder.namee.text=item.title
        holder.desc.text=item.description
        holder.image.setImageURI(Uri.parse(item.image))
    }

}