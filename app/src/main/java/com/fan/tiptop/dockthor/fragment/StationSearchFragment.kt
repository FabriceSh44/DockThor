package com.fan.tiptop.dockthor.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fan.tiptop.citiapi.data.CitiStationId
import com.fan.tiptop.citiapi.data.StationInformationModel
import com.fan.tiptop.citiapi.request.CitiRequester
import com.fan.tiptop.dockthor.R
import com.fan.tiptop.dockthor.databinding.FragmentStationSearchBinding
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import com.fan.tiptop.dockthor.network.NetworkManager
import java.util.*
//private const val MAX_RESULTS = 5


class StationSearchFragment : Fragment(), OnQueryTextListener {


    //StationSearchFragment works with view binding
    private var _stationInfoList: List<StationInformationModel>? = null
    private val TAG = "DockThor"
    private var _binding: FragmentStationSearchBinding? = null
    private val binding get() = _binding!!
    private var _currentFavStationId: Set<CitiStationId> = mutableSetOf()
    private var _geocoder: Geocoder? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationSearchBinding.inflate(inflater, container, false)
        val myView = binding.root
        _geocoder = Geocoder(myView.context)

        binding.searchView.setOnQueryTextListener(this)
        NetworkManager.getInstance().stationInformationRequest(
            object : DefaultNetworkManagerListener {
                override fun getResult(result: String) {
                    if (result.isNotEmpty()) {
                        _stationInfoList = processResponse(result)
                        binding.errorTextView.text = ""
                        Log.i(TAG, result)
                    }
                }

                override fun getError(error: String) {
                    if (error.isNotEmpty()) {
                        binding.errorTextView.text = getString(R.string.cant_load_station, error)
                        Log.e(TAG, error)
                    }
                }
            }
        )

        if (!requireArguments().isEmpty) {
            _currentFavStationId =
                StationSearchFragmentArgs.fromBundle(requireArguments()).favStationIds.toSet()
        }
        return myView
    }

    override fun onStart() {
        super.onStart()
        binding.searchView.requestFocus()
    }

    fun processResponse(response: String): List<StationInformationModel> {
        try {
            val requestor = CitiRequester()
            val model = requestor.getStationInformationModel(response)
            return model.data.stations.map { x ->
                StationInformationModel(
                    station_id = CitiStationId(x.station_id),
                    name = "",
                    address = x.name,
                    isFavorite = false,
                    expiryTime = null,
                    capacity = x.capacity,
                    lon = x.lon,
                    lat = x.lat
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return listOf()
        }
    }
//    fun getLocationFromName(name: String):List<Address>?  {
//        _geocoder?.let {
//                return it.getFromLocationName(
//                    name,
//                    MAX_RESULTS,
//                    40.601742,
//                    -74.112110,
//                    40.90197465400506,
//                    -73.8583169222231
//                )
//        }
//        return mutableListOf()
//    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(queryText: String): Boolean {
        if (_stationInfoList != null) {
            removeAllAddress()
            redrawStationAddressTable(filterList(queryText, _stationInfoList!!))
        }
        return false
    }

    private fun filterList(
        queryText: String,
        stationInfoList: List<StationInformationModel>
    ): MutableList<StationInformationModel> {
        val filteredStationList = mutableListOf<StationInformationModel>()
        val splitQueryList: List<String> = queryText.lowercase().split(" ")
        for (station in stationInfoList) {
            var allWordInStationName = true
            for (word in splitQueryList) {
                if (!station.address.lowercase().contains(word)) {
                    allWordInStationName = false
                    break
                }
            }
            if (allWordInStationName && station.station_id !in _currentFavStationId) {
                filteredStationList.add(station)
            }
            if (filteredStationList.size > 20)
                break
        }
        return filteredStationList
    }

    private fun removeAllAddress() {
        val addressTable = binding.stationAddressTableLayout
        val toDelete = LinkedList<View>()
        for (i in 0 until addressTable.childCount) {
            val view = addressTable.getChildAt(i)
            if (view is TableRow) {
                toDelete.add(view)
            }
        }
        for (viewToDelete in toDelete) {
            addressTable.removeView(viewToDelete)
        }
    }

    @SuppressLint("InflateParams")
    private fun redrawStationAddressTable(
        stationSuggestions: List<StationInformationModel>
    ) {
        val addressTable = binding.stationAddressTableLayout
        var i = 0
        for (ssRow in stationSuggestions) {
            val context = addressTable.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row = inflater.inflate(R.layout.element_suggestion_station_row, null) as TableRow
            val addressView: TextView = getTextViewWithStyle(context, ssRow.address)
            row.addView(addressView)
            row.tag = ssRow
            row.setOnClickListener { view -> chooseView(view) }
            addressTable.addView(row, i++)
        }
        addressTable.invalidate()
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
        tv.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        tv.textSize = 24F
        return tv
    }

    private fun chooseView(view: View?) {
        if (view != null) {
            val stationInfo = view.tag as StationInformationModel
            stationInfo.isFavorite=true
            val action = StationSearchFragmentDirections.actionStationSearchFragmentToMainFragment(
                stationInfo
            )
            view.findNavController().navigate(action)
        }
    }
}