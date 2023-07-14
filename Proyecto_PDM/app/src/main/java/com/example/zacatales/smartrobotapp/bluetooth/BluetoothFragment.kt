package com.example.zacatales.smartrobotapp.bluetooth

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zacatales.smartrobotapp.ControllersFragmentDirections
import com.example.zacatales.smartrobotapp.MainActivity
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothConnectionListener
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager
import com.example.zacatales.smartrobotapp.bluetooth.recyclerview.PairedListAdapter
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.bluetooth.viewmodel.DeviceViewModel
import com.example.zacatales.smartrobotapp.R
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothStateListener
import com.example.zacatales.smartrobotapp.databinding.FragmentBluetoothBinding

const val REQUEST_ENABLE_BT=1


class BluetoothFragment : Fragment(), BluetoothStateListener,BluetoothConnectionListener{

    private lateinit var adapter: PairedListAdapter
    private lateinit var binding: FragmentBluetoothBinding
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothControlListener: BluetoothConnectionListener? = null

    private val deviceViewModel: DeviceViewModel
            by activityViewModels{
                DeviceViewModel.Factory
            }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BluetoothConnectionListener) {
            bluetoothControlListener = context
        } else {
            throw IllegalStateException("La actividad debe implementar la interfaz BluetoothControlListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        bluetoothControlListener=null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        binding=FragmentBluetoothBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as? MainActivity
        mainActivity?.setBluetoothStateListener(this)

        binding.actionToPrevius.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.actionToControllers.setOnClickListener {
            it.findNavController().navigate(R.id.action_bluetoothFragment_to_controllersFragment2)
        }

        setRecyclerView(view)
    }


    @SuppressLint("MissingPermission")
    private fun setRecyclerView(view: View){
        binding.pairedListRV.layoutManager = LinearLayoutManager(view.context)
        bluetoothManager = BluetoothManager(requireContext(),this)
        adapter = PairedListAdapter({
                selectedDevice ->
            //showSelectedItem(selectedDevice)
            bluetoothControlListener?.onBluetoothConnected(selectedDevice.macAddress,selectedDevice)
        },bluetoothManager)
        binding.pairedListRV.adapter=adapter
        this
        displayDevices()
    }

    private fun displayDevices(){
        adapter.setData(deviceViewModel.getDevices())
        adapter.notifyDataSetChanged()
    }

    override fun onBluetoothStateChanged(state: Boolean) {
        if(state){
            Log.i("e","SI MAN")
            //binding.actionToControllers.setBackgroundTintList(pressButton)
            activity?.runOnUiThread {
                displayDevices()
            }
            //findNavController().navigate(action)

        }
        else{
            displayDevices()
        }
        DataHolder.myData = state
        //BluetoothFragmentDirections.actionBluetoothFragmentToControllersFragment2(state)
    }

    override fun onBluetoothConnected(address: String, selectedDevice: PairedDevicesInfo) {
    }

    override fun enviarComandoBluetooth(comando: String) {

    }

    override fun onBluetoothConnectionError(error: String) {
    }

    override fun onBluetoothDisconnected() {
        //Log.i("a","DESCONECTADO")
        displayDevices()
        DataHolder.myData = false
    }
    override fun State(state: Boolean) {
    }


}

object DataHolder {
    var myData: Boolean = false
}





