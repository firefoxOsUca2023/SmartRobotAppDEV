package com.example.zacatales.smartrobotapp.bluetooth.model

data class PairedDevicesInfo (
    val name: String,
    val macAddress:String,
    var connect: Boolean = false,
    //var isSelected: Boolean = false
)