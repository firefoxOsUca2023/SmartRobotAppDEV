package com.example.zacatales.smartrobotapp.bluetooth.`interface`




import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.example.zacatales.smartrobotapp.R
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.bluetooth.recyclerview.PairedListAdapter
import com.example.zacatales.smartrobotapp.bluetooth.viewmodel.DeviceViewModel
import java.io.IOException
import java.io.OutputStream
import java.util.*


class BluetoothManager(private val context: Context, private var listener: BluetoothConnectionListener) {

    fun setBluetoothStateListener(listener: BluetoothConnectionListener) {
        this@BluetoothManager.listener = listener
    }

    @SuppressLint("MissingPermission")
    fun conectarDispositivo(address: String,selectedDevice: PairedDevicesInfo) {
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(address)
        var state: Boolean
        state = true

        // Verificar si el dispositivo Bluetooth es válido
        if (device == null) {
            // El dispositivo no está disponible
            return
        }
        // Establecer la conexión Bluetooth en un hilo separado
        Thread {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream
                if (outputStream != null) {
                    // La conexión ha sido establecida, realiza operaciones de lectura/escritura aquí
                    selectedDevice.connect = true
                    //updateDeviceConnectionState(selectedDevice)
                    listener.State(state)
                }
            } catch (e: IOException) {
                val error = context.getString(R.string.errorNotConnection)
                selectedDevice.connect = false
                listener.onBluetoothConnectionError(error)
            }
        }.start()
    }
    fun enviarComando(comando: String) {
        if(bluetoothSocket==null && outputStream==null){
            //val error = "Conectese a un dispositivo bluetooth"
            //listener.onBluetoothConnectionError(error)
        }
        else{
            try {
                outputStream?.write(comando.toByteArray())
                outputStream?.flush()
            } catch (e: IOException) {
                //val error = "Todavia no se ha conectado a ningún dispostivo"
                //listener.onBluetoothConnectionError(error)
                ///listener.onBluetoothConnectionError(error)
            }
        }
    }
    fun desconectarDispositivo() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
            listener.onBluetoothDisconnected()
        } catch (e: IOException) {
            // Ocurrió un error al desconectar el dispositivo
            val error = "Error al desconectar"
            //listener.onBluetoothConnectionError(error)
        }
    }
    companion object {
        private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        private var bluetoothSocket: BluetoothSocket? = null
        private var outputStream: OutputStream? = null
    }
}