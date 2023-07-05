package com.example.zacatales.smartrobotapp.bluetooth.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.zacatales.smartrobotapp.R
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.databinding.ItemDeviceBinding

class PairedListViewHolder(private val binding: ItemDeviceBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(device: PairedDevicesInfo, clickListener: (PairedDevicesInfo) -> Unit) {
        binding.nameDevice.text = device.name
        if (device.connect){
            binding.connectDevice.setText(R.string.status_device)
        } else {
            binding.connectDevice.text = ""
        }
        binding.deviceItemDeviceCard.setOnClickListener {
            clickListener(device)
        }
    }


}