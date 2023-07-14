package com.example.zacatales.smartrobotapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothConnectionListener
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothStateListener
import com.example.zacatales.smartrobotapp.bluetooth.model.PairedDevicesInfo
import com.example.zacatales.smartrobotapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),BluetoothConnectionListener,BluetoothStateListener {
    private lateinit var binding: ActivityMainBinding
    private var btPermissions = false
    private var bluetoothStateListener: BluetoothStateListener? = null
    private lateinit var bluetoothManager: com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager
    private val hideNavigationBarDelayMillis: Long = 2500
    private val handler = Handler()
    private val hideNavigationBarRunnable = Runnable { hideNavigationBar() }


    override fun onResume() {
        super.onResume()
        bluetoothManager = com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager(this,this)
        bluetoothManager.setBluetoothStateListener(this)
        hideNavigationBar()
    }

    override fun onPause() {
        super.onPause()
        bluetoothManager = com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager(this,this)
        bluetoothManager.setBluetoothStateListener(this)
        handler.removeCallbacks(hideNavigationBarRunnable)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.
        setContentView(this,R.layout.activity_main)
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
        if(bluetoothAdapter==null){
            Toast.makeText(this,getString(R.string.notSupportBt),Toast.LENGTH_LONG).show()
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                bluetoothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)

            }else{
                bluetoothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_ADMIN)
            }
        }

        hideNavigationBar()

    }


    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted:Boolean ->
        if(isGranted){
            val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter: BluetoothAdapter?=bluetoothManager.adapter
            btPermissions = true
            if(bluetoothAdapter?.isEnabled==false){
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                btActivityResultLauncher.launch(enableBtIntent)

            }else {
            }
        }else {
            btPermissions=false
        }
    }
    private val btActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result : ActivityResult ->
        if (result.resultCode == RESULT_OK){
            can()
        }
    }

    fun can(){
        Toast.makeText(this,getString(R.string.btOn),Toast.LENGTH_LONG).show()
    }

    override fun onBluetoothConnected(address: String,selectedDevice: PairedDevicesInfo) {
        bluetoothManager.conectarDispositivo(address,selectedDevice)
    }

    override fun enviarComandoBluetooth(comando: String) {
        bluetoothManager.enviarComando(comando)
        Log.i("Comannd","Enviado")
    }
    override fun onBluetoothConnectionError(error: String) {
        Log.i("Error","Falló la conexión")
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(this,error,Toast.LENGTH_LONG).show()
    }

    override fun onBluetoothDisconnected() {
        //showToast("Conexión Bluetooth cerrada")
    }

    override fun State(state: Boolean) {
        if(state){
            runOnUiThread {
                Toast.makeText(this, getString(R.string.connectSucessful), Toast.LENGTH_SHORT).show()
            }

        }
        bluetoothStateListener?.onBluetoothStateChanged(state)

    }

    override fun onBluetoothStateChanged(state: Boolean) {

    }
    fun setBluetoothStateListener(listener: BluetoothStateListener) {
        bluetoothStateListener = listener
    }
    private fun hideNavigationBar() {
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
        handler.removeCallbacks(hideNavigationBarRunnable)
        handler.postDelayed(hideNavigationBarRunnable, hideNavigationBarDelayMillis)
    }
}