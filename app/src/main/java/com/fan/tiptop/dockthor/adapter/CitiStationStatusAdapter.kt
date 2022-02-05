package com.fan.tiptop.dockthor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fan.tiptop.citiapi.CitiStationStatus
import com.fan.tiptop.dockthor.databinding.StationStatusItemBinding

class CitiStationStatusAdapter(
    val clickListener: (stationId: Int) -> Unit,
    val longClickListener: (stationId: Int) -> Boolean
) :
    ListAdapter<CitiStationStatus, CitiStationStatusAdapter.CitiStationStatusViewHolder>(
        CitiStationStatusDiffItemCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiStationStatusViewHolder =
        CitiStationStatusViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: CitiStationStatusViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener, longClickListener)
    }

    class CitiStationStatusViewHolder(val binding: StationStatusItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun inflateFrom(parent: ViewGroup): CitiStationStatusViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StationStatusItemBinding.inflate(layoutInflater, parent, false)
                return CitiStationStatusViewHolder(binding)
            }
        }

        fun bind(
            item: CitiStationStatus,
            clickListener: (stationId: Int) -> Unit,
            longClickListener: (stationId: Int) -> Boolean
        ) {
            binding.citiStationStatus = item
            binding.root.setOnClickListener {
                clickListener(item.stationId)
            }
            binding.root.setOnLongClickListener {
                longClickListener(item.stationId)
            }
        }

    }


}