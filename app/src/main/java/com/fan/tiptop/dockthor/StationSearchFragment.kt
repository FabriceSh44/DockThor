package com.fan.tiptop.dockthor

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fan.tiptop.citiapi.CitiRequestor
import com.fan.tiptop.citiapi.CitibikeStationInformationModel
import com.fan.tiptop.dockthor.databinding.FragmentStationSearchBinding
import com.fan.tiptop.dockthor.network.NetworkManager
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import java.util.*


class StationSearchFragment : Fragment(), OnQueryTextListener {
    //StationSearchFragment works with view binding
    private  var _stationInfoList: List<CitibikeStationInformationModel>?=null
    private val TAG = "DockThor"
    private var _binding: FragmentStationSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationSearchBinding.inflate(inflater, container, false)
        val myView = binding.root
        binding.searchView.setOnQueryTextListener(this)
        NetworkManager.getInstance().stationInformationRequest(
            object : DefaultNetworkManagerListener {
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
        if(_stationInfoList!=null) {
            val tableLayout = binding.stationAdressTableLayout
            removeAllAdress(tableLayout)
            redrawTipTable(tableLayout, filterList(newText, _stationInfoList!!))
        }
        return false
    }

    private fun filterList(
        address: String,
        stationInfoList: List<CitibikeStationInformationModel>
    ): MutableList<CitibikeStationInformationModel> {
        val filteredStationList = mutableListOf<CitibikeStationInformationModel>()
        for (station in stationInfoList) {
            if (address.lowercase() in station.name.lowercase()) {
                filteredStationList.add(station)
                if (filteredStationList.size > 10)
                    break
            }
        }
        return filteredStationList
    }

    private fun removeAllAdress(addressTable: TableLayout) {
        val toDelete = LinkedList<View>()
        for (i in 1 until addressTable.childCount) {
            val view = addressTable.getChildAt(i)
            if (view is TableRow) {
                toDelete.add(view)
            }
        }
        for (viewToDelete in toDelete) {
            addressTable.removeView(viewToDelete)
        }
    }

    private fun redrawTipTable(
        stationAdressTable: TableLayout,
        stationSuggestions: List<CitibikeStationInformationModel>
    ) {
        var i = 0
        for (ssRow in stationSuggestions) {
            val context = stationAdressTable.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row = inflater.inflate(R.layout.partial_suggestion_station_row, null) as TableRow
            val addressView: TextView = getTextViewWithStyle(context, ssRow.name)
            row.addView(addressView)
            row.tag = ssRow
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
            val stationInfo = view.tag as CitibikeStationInformationModel
            val action = StationSearchFragmentDirections.actionStationSearchFragmentToMainFragment(
                stationInfo
            )
            view.findNavController().navigate(action)
        }
    }
}