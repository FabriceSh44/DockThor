package com.fan.tiptop.dockthor.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.database.DockThorDatabase
import com.fan.tiptop.dockthor.R
import com.fan.tiptop.dockthor.adapter.CitiStationStatusAdapter
import com.fan.tiptop.dockthor.databinding.FragmentMainBinding
import com.fan.tiptop.dockthor.logic.MainViewModel
import com.fan.tiptop.dockthor.logic.MainViewModelFactory
import com.fan.tiptop.dockthor.logic.main_swipe.MainSwipeController
import com.fan.tiptop.dockthor.logic.main_swipe.SwipeSide


class MainFragment : Fragment() {
    //Mainfragment works with databinding
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var _mainViewModel: MainViewModel? = null
    private val mainViewModel get() = _mainViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        //set main view model
        val application = requireNotNull(this.activity).application
        val dao = DockThorDatabase.getInstance(application).citibikeStationInformationDao

        val viewModelFactory = MainViewModelFactory(dao)
        _mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val adapter = CitiStationStatusAdapter({ station: CitiStationStatus ->
            actionClick(station)
        },
            { station: CitiStationStatus ->
                Boolean
                actionLongClick(station)
                true
            })
        binding.citibikeStatusList.adapter = adapter

        //this allow swiping on each view holder of the recyler view
        val ith =
            ItemTouchHelper(MainSwipeController { station: CitiStationStatus, swipeSide: SwipeSide ->
                mainViewModel.onSwipedCitiStationStatus(
                    station,
                    swipeSide
                )
            })
        ith.attachToRecyclerView(binding.citibikeStatusList)

        // this connect the model citistation status to the adapter which setup view
        mainViewModel.citiStationStatusLD.observe(
            viewLifecycleOwner
        ) { it?.let { adapter.submitList(it) } }

        //switch fav station behavior
        mainViewModel.navigateToSwitchFavStation.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                val action = MainFragmentDirections.actionMainFragmentToStationSearchFragment(
                    mainViewModel.citiStationStatusLD.value?.map { x -> x.stationId }?.toIntArray()
                        ?: intArrayOf()
                )
                view.findNavController().navigate(action)
                mainViewModel.onAddFavStationNavigated()
            }
        }
        mainViewModel.contextualBarNotVisible.observe(viewLifecycleOwner) { shouldCloseContextualBar ->
            if (shouldCloseContextualBar) {
                _actionMode?.finish()
                _actionMode = null
            } else {
                when (_actionMode) {
                    null -> {
                        _actionMode = activity?.startActionMode(_actionModeCallback)
                    }
                    else -> {}
                }
            }
        }
        mainViewModel.errorToDisplayLD.observe(viewLifecycleOwner) { errorText ->
            if (errorText.isNotEmpty()) {
                Toast.makeText(view.context, errorText, Toast.LENGTH_LONG).show()
            }
        }
        if (!requireArguments().isEmpty) {
            val stationModel = MainFragmentArgs.fromBundle(requireArguments()).stationModel
            if (!mainViewModel.containsInfoModel(stationModel)) {
                mainViewModel.onSetStation(stationModel)
            }
        }
        return view
    }

    private var _actionMode: ActionMode? = null
    private val _actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.contextual_action_bar, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.delete -> {
                    if (_mainViewModel != null) {
                        mainViewModel.onItemDeleted()
                    }
                    true
                }
                else -> false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) {
            mainViewModel.onClearSelectedStation()
        }
    }

    private fun actionClick(station: CitiStationStatus) {
        val navigate = mainViewModel.onActionClick(station)
        if (navigate) {
            val action =
                MainFragmentDirections.actionMainFragmentToEditCitistationStatusFragment(station)
            this.findNavController().navigate(action)
        }
    }

    private fun actionLongClick(station: CitiStationStatus) {
        mainViewModel.onActionLongClick(station)
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.initializeMainViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _mainViewModel = null
    }


}
