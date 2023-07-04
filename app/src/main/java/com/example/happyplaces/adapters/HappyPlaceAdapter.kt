package com.example.happyplaces.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.ItemBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceAdapter(private val items:ArrayList<HappyPlaceModel>):RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

//    private var onClickListener:OnClickListener?=null

    var onItemClick: ((HappyPlaceModel) -> Unit)? = null



    inner class ViewHolder(binding: ItemBinding):RecyclerView.ViewHolder(binding.root) {
        val namee=binding.tvName
        val desc=binding.tvDesc
        val image= binding.civPlaceImage

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
        }

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

//    fun setOnClickListener(onClickListener: OnClickListener) {
//        this.onClickListener = onClickListener
//    }
//
//    interface OnClickListener {
//        fun onClick(position: Int, model: HappyPlaceModel)
//    }
}