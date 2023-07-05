package com.example.zacatales.smartrobotapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RobotViewModel : ViewModel() {
    // MutableLiveData que va a guardar los clicks
    private val _clicks = MutableLiveData<List<String>>()
    // LiveData para exponer los datos de forma segura
    val clicks: LiveData<List<String>> get() = _clicks
    private val clickList = ArrayList<String>()

    // Función para agregar un click a la lista
    fun addClick(click: String) {
        clickList.add(click)
        _clicks.value = clickList
    }
}


