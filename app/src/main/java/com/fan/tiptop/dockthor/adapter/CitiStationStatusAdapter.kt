package com.fan.tiptop.dockthor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.dockthor.databinding.ElementStationCardBinding

class CitiStationStatusAdapter(
    private val clickListener: (station: CitiStationStatus) -> Unit,
    private val longClickListener: (station: CitiStationStatus) -> Boolean
) : ListAdapter<CitiStationStatus, CitiStationStatusAdapter.CitiStationStatusViewHolder>(
    CitiStationStatusDiffItemCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiStationStatusViewHolder =
        CitiStationStatusViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: CitiStationStatusViewHolder, position: Int) {
        val item: CitiStationStatus = getItem(position)
        holder.bind(item, clickListener, longClickListener)
    }

    class CitiStationStatusViewHolder(val binding: ElementStationCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun inflateFrom(parent: ViewGroup): CitiStationStatusViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ElementStationCardBinding =
                    ElementStationCardBinding.inflate(layoutInflater, parent, false)
                return CitiStationStatusViewHolder(binding)
            }
        }

        fun bind(
            item: CitiStationStatus,
            clickListener: (station: CitiStationStatus) -> Unit,
            longClickListener: (station: CitiStationStatus) -> Boolean
        ) {
            binding.citiStationStatus = item
            binding.root.setOnClickListener {
                clickListener(item)
            }
            binding.root.setOnLongClickListener {
                longClickListener(item)
            }
        }

    }


}
