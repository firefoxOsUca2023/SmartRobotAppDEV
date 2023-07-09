package com.example.zacatales.smartrobotapp.bluetooth.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.DeviceReviewerApplication
import com.example.zacatales.smartrobotapp.bluetooth.repositories.DeviceRepository


class DeviceViewModel(private val repository: DeviceRepository): ViewModel() {
    var name = MutableLiveData("")
    var macAddress = MutableLiveData("")
    var status = MutableLiveData("")
    var connect = MutableLiveData("")


    fun getDevices() = repository.getDevices()

    fun setSelectedDevice(device: PairedDevicesInfo) {
        name.value = device.name
        macAddress.value = device.macAddress
    }


    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[
                        ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as DeviceReviewerApplication
                DeviceViewModel(app.deviceRepository)
            }
        }
        const val INACTIVE = ""

    }
}