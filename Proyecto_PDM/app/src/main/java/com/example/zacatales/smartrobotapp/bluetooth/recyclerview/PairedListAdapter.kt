package com.example.zacatales.smartrobotapp.bluetooth.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.databinding.ItemDeviceBinding

class PairedListAdapter(private val clickListener: (PairedDevicesInfo) -> Unit)
    : RecyclerView.Adapter<PairedListViewHolder>(){

    private val devices = ArrayList<PairedDevicesInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairedListViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PairedListViewHolder(binding)
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

    private var bluetoothConnected = false

    fun updateBluetoothStatus(connected: Boolean) {
        bluetoothConnected = connected
        notifyDataSetChanged()
    }

}



