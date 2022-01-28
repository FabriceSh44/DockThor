package com.fan.tiptop.dockthor

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.navigation.findNavController
import com.fan.tiptop.citiapi.CitiRequestor
import com.fan.tiptop.citiapi.CitibikeStationInformationModel
import com.fan.tiptop.dockthor.network.NetworkManager
import com.fan.tiptop.dockthor.network.SomeCustomListener
import java.util.*


class StationSearchFragment : Fragment(), OnQueryTextListener {
    private lateinit var _stationInfoList: List<CitibikeStationInformationModel>
    private val TAG = "DockThor"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_station_search, container, false)
        if (myView != null) {
            val editSearch = myView.findViewById<SearchView>(R.id.searchView)
            editSearch.setOnQueryTextListener(this)
        }
        NetworkManager.getInstance().stationInformationRequest(
            object : SomeCustomListener {
                override fun getResult(result: String) {
                    if (!result.isEmpty()) {
                        _stationInfoList = processResponse(result)
                        Log.i(TAG, result)
                    }
                }

                override fun getError(error: String) {
                    if (!error.isEmpty()) {
                        Log.e(TAG, error)
                    }
                }
            }
        )

        return myView
    }

    fun processResponse(response: String): List<CitibikeStationInformationModel> {
        try {
            val requestor = CitiRequestor()
            val model = requestor.getStationInformationModel(response)
            return model.data.stations
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return listOf()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val tableLayout =
            this.requireView().findViewById<TableLayout>(R.id.stationAdressTableLayout)
        removeAllAdress(tableLayout)
        redrawTipTable(tableLayout, filterList(newText, _stationInfoList))
        return false
    }

    private fun filterList(
        adress: String,
        stationInfoList: List<CitibikeStationInformationModel>
    ): MutableList<CitibikeStationInformationModel> {
        val filteredStationList = mutableListOf<CitibikeStationInformationModel>()
        for (station in stationInfoList) {
            if (adress.lowercase() in station.name.lowercase()) {
                filteredStationList.add(station)
                if (filteredStationList.size > 10)
                    break
            }
        }
        return filteredStationList
    }

    private fun removeAllAdress(adressTable: TableLayout) {
        val toDelete = LinkedList<View>()
        for (i in 1 until adressTable.childCount) {
            val view = adressTable.getChildAt(i)
            if (view is TableRow) {
                toDelete.add(view)
            }
        }
        for (viewToDelete in toDelete) {
            adressTable.removeView(viewToDelete)
        }
    }

    private fun redrawTipTable(
        stationAdressTable: TableLayout,
        stationSuggestions: List<CitibikeStationInformationModel>
    ) {
        var i = 1
        for (ssRow in stationSuggestions) {
            val context = stationAdressTable.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row = inflater.inflate(R.layout.partial_suggestion_station_row, null) as TableRow
            val adressView: TextView = getTextViewWithStyle(context, ssRow.name)
            row.addView(adressView)
            row.setTag(ssRow)
            row.setOnClickListener { view -> chooseView(view) }
            stationAdressTable.addView(row, i++)
        }
        stationAdressTable.invalidate()
    }

    private fun getTextViewWithStyle(context: Context, tvStr: String): TextView {
        val tv = TextView(context)
        tv.text = tvStr
        val lp = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        lp.weight = 1f
        lp.setMargins(3, 3, 3, 3)
        tv.layoutParams = lp
        tv.setTextColor(Color.BLACK)
        tv.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        return tv
    }

    private fun chooseView(view: View?) {
        if (view != null) {
            val stationInfo = view.getTag() as CitibikeStationInformationModel
            val action = StationSearchFragmentDirections.actionStationSearchFragmentToMainFragment(stationInfo.station_id.toInt())
            view.findNavController().navigate(action)
        }
    }
}