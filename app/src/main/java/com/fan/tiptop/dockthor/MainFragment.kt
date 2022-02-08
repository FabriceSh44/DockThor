package com.fan.tiptop.dockthor

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fan.tiptop.citiapi.DockThorDatabase
import com.fan.tiptop.dockthor.adapter.CitiStationStatusAdapter
import com.fan.tiptop.dockthor.databinding.FragmentMainBinding
import com.fan.tiptop.dockthor.model.MainViewModel
import com.fan.tiptop.dockthor.model.MainViewModelFactory


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


        val adapter = CitiStationStatusAdapter({ stationId: Int ->
            setContextualBar(stationId)
        }, { stationId: Int ->
            Boolean
            setContextualBar(stationId)
            true
        }
        )
        binding.citibikeStatusList.adapter = adapter
        binding.citibikeStatusList.layoutManager = GridLayoutManager(view.context, 2)

        // this connect the model citistation status to the adapter which setup view
        mainViewModel.citiStationStatus.observe(
            viewLifecycleOwner
        ) { it?.let { adapter.submitList(it) } }

        //switch fav station behavior
        mainViewModel.navigateToSwitchFavStation.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                view.findNavController().navigate(R.id.action_mainFragment_to_stationSearchFragment)
                mainViewModel.onAddFavStationNavigated()
            }
        }
        mainViewModel.errorToDisplay.observe(viewLifecycleOwner) { errorText ->
            if (errorText.isNotEmpty()) {
                Toast.makeText(view.context,errorText,Toast.LENGTH_LONG).show()
            }
        }
        if (!requireArguments().isEmpty) {
            mainViewModel.setStation(MainFragmentArgs.fromBundle(requireArguments()).stationModel)
        }
        return view
    }

    private var _actionMode: ActionMode? = null
    private val _actionModeCallback= object : ActionMode.Callback {
        // Called when the action mode is created; startActionMode() was called
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.contextual_action_bar, menu)
            return true
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.delete -> {
                    mainViewModel.onItemDeleted()
                    mode.finish() // Action picked, so close the CAB
                    true
                }
                else -> false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) {
            _actionMode = null
        }
    }
    private fun setContextualBar(stationId: Int) {
        mainViewModel.addSelectedStationId(stationId)
        when (_actionMode) {
            null -> {
                _actionMode = activity?.startActionMode(_actionModeCallback)
                true
            }
            else -> false
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.refreshBikeStation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _mainViewModel = null
    }


}
