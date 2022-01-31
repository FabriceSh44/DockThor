package com.fan.tiptop.dockthor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.fan.tiptop.citiapi.CitiStationStatus
import com.fan.tiptop.dockthor.R
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CitiStationStatusAdapter : Adapter<CitiStationStatusAdapter.CitiStationStatusViewHolder>() {
    var data = listOf<CitiStationStatus>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiStationStatusViewHolder =
        CitiStationStatusViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: CitiStationStatusViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class CitiStationStatusViewHolder(val rootView: CardView) : RecyclerView.ViewHolder(rootView) {
        val address = rootView.findViewById<TextView>(R.id.addressTextView)
        val bike = rootView.findViewById<TextView>(R.id.bikeTextView)
        val electricalBike = rootView.findViewById<TextView>(R.id.electricBikeTextView)
        val parking = rootView.findViewById<TextView>(R.id.parkingTextView)
        companion object{
            fun inflateFrom(parent: ViewGroup):CitiStationStatusViewHolder{
                val layoutInflater=LayoutInflater.from(parent.context)
                val view=layoutInflater.inflate(R.layout.station_status_item,parent,false) as CardView
                return CitiStationStatusViewHolder(view)
            }
        }
        fun bind(item: CitiStationStatus) {
            bike.text = item.numBikeAvailable.toString()
            parking.text = item.numDockAvailable.toString()
            electricalBike.text = item.numEbikeAvailable.toString()
            address.text = item.address
        }

    }


}