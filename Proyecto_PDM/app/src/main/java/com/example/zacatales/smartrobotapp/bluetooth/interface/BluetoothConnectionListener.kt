package com.example.zacatales.smartrobotapp.bluetooth.`interface`

import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo

interface BluetoothConnectionListener {
    fun onBluetoothConnected(address: String, selectedDevice: PairedDevicesInfo)
    fun enviarComandoBluetooth(comando: String)
    fun onBluetoothConnectionError(error: String)
    fun onBluetoothDisconnected()
    fun State(state: Boolean)
}
