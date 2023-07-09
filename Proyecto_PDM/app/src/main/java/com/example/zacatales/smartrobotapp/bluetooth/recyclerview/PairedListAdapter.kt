package com.example.zacatales.smartrobotapp.bluetooth.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.databinding.ItemDeviceBinding

class PairedListAdapter(private val clickListener: (PairedDevicesInfo) -> Unit,
private val bluetoothManager: BluetoothManager
)
    : RecyclerView.Adapter<PairedListViewHolder>(){

    private val devices = ArrayList<PairedDevicesInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairedListViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PairedListViewHolder(binding,bluetoothManager)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: PairedListViewHolder, position: Int) {
        var device = devices[position]
        holder.bind(device,clickListener)
    }
    fun setData(devicesList: MutableList<PairedDevicesInfo>){
        devices.clear()
        devices.addAll(devicesList)
    }
}



