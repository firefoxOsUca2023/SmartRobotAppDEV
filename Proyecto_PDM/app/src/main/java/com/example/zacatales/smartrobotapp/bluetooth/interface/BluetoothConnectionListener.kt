package com.example.zacatales.smartrobotapp.bluetooth.`interface`

interface BluetoothConnectionListener {
    fun onBluetoothConnected(address: String)
    fun enviarComandoBluetooth(comando: String)
    fun onBluetoothConnectionError(error: String)
    fun onBluetoothDisconnected()
    fun State(state: Boolean)
}
