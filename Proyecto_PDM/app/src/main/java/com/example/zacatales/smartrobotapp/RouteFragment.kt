package com.example.zacatales.smartrobotapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.zacatales.smartrobotapp.databinding.FragmentRouteBinding
import com.example.zacatales.smartrobotapp.view.RouteView
import com.example.zacatales.smartrobotapp.viewmodel.RobotViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RouteFragment : Fragment() {

    private val viewModel: RobotViewModel by activityViewModels()
    private lateinit var routeView: RouteView
    private lateinit var binding: FragmentRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        binding = FragmentRouteBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routeView = view.findViewById(R.id.routeView)

        viewModel.clicks.observe(viewLifecycleOwner, { clicks ->
            routeView.setRouteList(clicks)

        })

        binding.actionToPreviusRouteFragment.setOnClickListener {
            activity?.apply {
                //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                activity?.apply {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
            }
        }
        binding.actionDeleteRoute.setOnClickListener {
            activity?.apply {
                //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                routeView.clearRoute()
                // Aquí indicamos que se han borrado los datos de RouteView
                viewModel.setClicksClearedFromRoute()
                // Limpiamos la lista de clicks en el ViewModel
                viewModel.clearClicks()
            }
        }
    }
}