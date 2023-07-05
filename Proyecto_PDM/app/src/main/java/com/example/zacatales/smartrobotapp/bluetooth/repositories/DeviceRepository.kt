package com.example.zacatales.smartrobotapp.bluetooth.repositories

import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo

class DeviceRepository(private val devices:
MutableList<PairedDevicesInfo>) {
    fun getDevices()= devices

    fun addDevices(device:PairedDevicesInfo)=devices.add(device)
}