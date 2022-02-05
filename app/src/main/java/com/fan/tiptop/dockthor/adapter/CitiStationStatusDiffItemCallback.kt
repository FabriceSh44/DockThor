package com.fan.tiptop.dockthor.adapter

import androidx.recyclerview.widget.DiffUtil
import com.fan.tiptop.citiapi.CitiStationStatus

class CitiStationStatusDiffItemCallback : DiffUtil.ItemCallback<CitiStationStatus>() {
    override fun areItemsTheSame(oldItem: CitiStationStatus, newItem: CitiStationStatus): Boolean {
        return oldItem.stationId == newItem.stationId
    }

    override fun areContentsTheSame(
        oldItem: CitiStationStatus,
        newItem: CitiStationStatus
    ): Boolean {
        return oldItem == newItem
    }
}