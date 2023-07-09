package com.example.zacatales.smartrobotapp.bluetooth.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.zacatales.smartrobotapp.R
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.databinding.ItemDeviceBinding

class PairedListViewHolder(private val binding: ItemDeviceBinding,
                           private val bluetoothManager: BluetoothManager
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(device: PairedDevicesInfo,clickListener: (PairedDevicesInfo) -> Unit) {
         var buttonPressCount = 0
        binding.nameDevice.text = device.name
            if (device.connect){
                //device.connect=true
                binding.connectDevice.setText(R.string.status_device)
            } else{
            binding.connectDevice.text = ""
            }
        binding.deviceItemDeviceCard.setOnClickListener {
            buttonPressCount++
            if (buttonPressCount == 1) {
                clickListener(device)
            }
            else if (buttonPressCount == 2) {
                bluetoothManager.desconectarDispositivo()
                buttonPressCount = 0
            }
        }
    }
}