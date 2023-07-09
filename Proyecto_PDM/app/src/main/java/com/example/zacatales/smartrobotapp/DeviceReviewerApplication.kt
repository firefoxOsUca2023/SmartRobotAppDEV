package com.example.zacatales.smartrobotapp

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.bluetooth.repositories.DeviceRepository

@SuppressLint("MissingPermission")
class DeviceReviewerApplication:Application() {

    lateinit var mbluetoothAdapter: BluetoothAdapter
    var list = mutableListOf<PairedDevicesInfo>()
    val device = mutableListOf(
        PairedDevicesInfo("","")
    )



    @SuppressLint("MissingPermission", "WrongConstant")
    fun isBluetoothHeadsetConnected(): Boolean{

        mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return (mbluetoothAdapter!=null && mbluetoothAdapter.isEnabled)
        //&& mbluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET)== BluetoothHeadset.STATE_CONNECTED)
    }

    val deviceRepository: DeviceRepository
            by lazy {
                if(isBluetoothHeadsetConnected()){
                    if(mbluetoothAdapter.bondedDevices.size>0){
                        for(device in mbluetoothAdapter.bondedDevices){
                           //list = mutableListOf(PairedDevicesInfo(device.name,device.address))
                            list.add(PairedDevicesInfo(device.name,device.address))
                        }
                    }
                }
                DeviceRepository(list)
            }
}