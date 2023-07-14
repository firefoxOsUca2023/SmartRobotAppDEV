package com.example.zacatales.smartrobotapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.zacatales.smartrobotapp.bluetooth.DataHolder
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothConnectionListener
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager
import com.example.zacatales.smartrobotapp.databinding.FragmentControllersBinding
import com.example.zacatales.smartrobotapp.model.BotonPresionado
import com.example.zacatales.smartrobotapp.viewmodel.RobotViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class ControllersFragment : Fragment(){

    private lateinit var binding: FragmentControllersBinding
    private var bluetoothControlListener: BluetoothConnectionListener? = null
    private lateinit var routeButton: FloatingActionButton
    private val viewModel: RobotViewModel by activityViewModels()
    private var scope: CoroutineScope? = null
    private var pressStartTime: Long = 0
    private var buttonPressCount = 0
    private var buttonPressCount2 = 0
    var isButton1Pressed = false
    var isButton2Pressed = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BluetoothConnectionListener) {
            bluetoothControlListener = context
        } else {
            throw IllegalStateException("La actividad debe implementar la interfaz BluetoothControlListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        bluetoothControlListener=null
    }


    override fun onDestroy() {
        super.onDestroy()
        bluetoothControlListener?.enviarComandoBluetooth("x")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        binding = FragmentControllersBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission", "SuspiciousIndentation", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routeButton = binding.actionToRouteControllerFragment


        val pressButton = ContextCompat.getColorStateList(requireContext(), R.color.pressColor)
        //val colorStateList2 = ContextCompat.getColorStateList(requireContext(), R.color.btnColor)
        val modeDark = ContextCompat.getColorStateList(requireContext(), R.color.hornPressButton)
        val ColorDefault = ContextCompat.getColorStateList(requireContext(), R.color.white)

        val data = DataHolder.myData

        if(data){
            routeButton.setOnClickListener {
                it.findNavController().navigate(R.id.action_controllersFragment2_to_routeFragment)
                if(viewModel.isClearedFromRoute.value == true) {
                    viewModel.clearClicks()
                }
            }

            binding.lightsActionButton.setOnClickListener {
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    buttonPressCount++
                    if (buttonPressCount == 1) {
                        binding.lightsActionButton.setBackgroundTintList(modeDark)
                        bluetoothControlListener?.enviarComandoBluetooth("X")
                    } else if (buttonPressCount == 2) {
                        binding.lightsActionButton.setBackgroundTintList(ColorDefault)
                        bluetoothControlListener?.enviarComandoBluetooth("x")
                        buttonPressCount = 0
                    }
                } else {
                    buttonPressCount2++
                    if (buttonPressCount2 == 1) {
                        binding.lightsActionButton.setBackgroundTintList(pressButton)
                        bluetoothControlListener?.enviarComandoBluetooth("X")
                    } else if (buttonPressCount2 == 2) {
                        binding.lightsActionButton.setBackgroundTintList(ColorDefault)
                        bluetoothControlListener?.enviarComandoBluetooth("x")
                        buttonPressCount2 = 0
                    }
                }
            }

            binding.hornActionButton.setOnTouchListener { view, motionEvent ->
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // Se presionó el botón
                            binding.hornActionButton.setBackgroundTintList(modeDark)
                            bluetoothControlListener?.enviarComandoBluetooth("V")
                        }

                        MotionEvent.ACTION_UP -> {
                            // Se soltó el botón
                            binding.hornActionButton.setBackgroundTintList(ColorDefault)
                            bluetoothControlListener?.enviarComandoBluetooth("v")
                        }
                    }
                    true
                }
                else{
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // Se presionó el botón
                            binding.hornActionButton.setBackgroundTintList(pressButton)
                            bluetoothControlListener?.enviarComandoBluetooth("V")
                        }

                        MotionEvent.ACTION_UP -> {
                            // Se soltó el botón
                            binding.hornActionButton.setBackgroundTintList(ColorDefault)
                            bluetoothControlListener?.enviarComandoBluetooth("v")
                        }
                    }
                    true
                }
            }

            binding.upActionButton.setOnTouchListener { view, motionEvent ->
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton1Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("F")
                            pressStartTime = System.currentTimeMillis()
                            binding.upActionButton.setBackgroundTintList(modeDark)
                            moveForwardRight()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            isButton1Pressed = false
                            binding.upActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("F",pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveForwardRight()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                } else{
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton1Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("F")
                            binding.upActionButton.setBackgroundTintList(pressButton)
                            pressStartTime = System.currentTimeMillis()
                            moveForwardRight()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            isButton1Pressed = false
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            binding.upActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("F",pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveForwardRight()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                }
            }

            binding.rightActionButton.setOnTouchListener { view, motionEvent ->
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton1Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("R")
                            binding.rightActionButton.setBackgroundTintList(modeDark)
                            pressStartTime = System.currentTimeMillis()
                            moveForwardRight()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            isButton1Pressed = false
                            binding.rightActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("R", pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveForwardRight()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                } else{
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton1Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("R")
                            binding.rightActionButton.setBackgroundTintList(pressButton)
                            pressStartTime = System.currentTimeMillis()
                            moveForwardRight()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            isButton1Pressed = false
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            binding.rightActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("R", pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveForwardRight()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                }
            }

            binding.leftActionButton.setOnTouchListener { view, motionEvent ->
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton2Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("L")
                            binding.leftActionButton.setBackgroundTintList(modeDark)
                            pressStartTime = System.currentTimeMillis()
                            moveForwardLeft()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            isButton2Pressed = false
                            binding.leftActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("L", pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveForwardLeft()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                } else{
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton2Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("L")
                            binding.leftActionButton.setBackgroundTintList(pressButton)
                            pressStartTime = System.currentTimeMillis()
                            moveForwardLeft()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            isButton2Pressed = false
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            binding.leftActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("L", pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveForwardLeft()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                }
            }

            binding.backActionButton.setOnTouchListener { view, motionEvent ->
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton2Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("B")
                            binding.backActionButton.setBackgroundTintList(modeDark)
                            pressStartTime = System.currentTimeMillis()
                            moveBackwardsLeft()
                            moveBackwardsRight()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            isButton2Pressed = false
                            binding.backActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("B", pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveBackwardsLeft()
                            moveBackwardsRight()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                } else{
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isButton2Pressed = true
                            bluetoothControlListener?.enviarComandoBluetooth("B")
                            binding.backActionButton.setBackgroundTintList(pressButton)
                            pressStartTime = System.currentTimeMillis()
                            moveBackwardsLeft()
                            moveBackwardsRight()
                            binding.seekBar.isEnabled = false
                            startCounter()
                        }
                        MotionEvent.ACTION_UP -> {
                            isButton2Pressed = false
                            bluetoothControlListener?.enviarComandoBluetooth("S")
                            binding.backActionButton.setBackgroundTintList(ColorDefault)
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            val botonPresionado = BotonPresionado("B", pressDuration)
                            viewModel.addClick(botonPresionado)
                            moveBackwardsLeft()
                            moveBackwardsRight()
                            binding.seekBar.isEnabled = true
                            stopCounter()
                        }
                    }
                    true
                }
            }

            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    manejarVelocidad(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Inicio del seguimiento del SeekBar (cuando se toca)
                    manejarVelocidad(progress = 10)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Fin del seguimiento del SeekBar (cuando se suelta)
                    manejarVelocidad(progress = 0)
                }
            })
        }
        else{
            binding.actionToRouteControllerFragment.isEnabled = false
            binding.upActionButton.setOnClickListener {
                Toast.makeText(context,getString(R.string.connectToBt),Toast.LENGTH_LONG).show()
            }
            binding.backActionButton.setOnClickListener {
                Toast.makeText(context,getString(R.string.connectToBt),Toast.LENGTH_LONG).show()
            }
            binding.leftActionButton.setOnClickListener {
                Toast.makeText(context,getString(R.string.connectToBt),Toast.LENGTH_LONG).show()
            }
            binding.rightActionButton.setOnClickListener {
                Toast.makeText(context,getString(R.string.connectToBt),Toast.LENGTH_LONG).show()
            }
            binding.hornActionButton.setOnClickListener {
                Toast.makeText(context,getString(R.string.connectToBt),Toast.LENGTH_LONG).show()
            }
            binding.lightsActionButton.setOnClickListener {
                Toast.makeText(context,getString(R.string.connectToBt),Toast.LENGTH_LONG).show()
            }
            binding.seekBar.isEnabled = false
        }

        //    int orientation = yourActivityName.this.getResources().getConfiguration().orientation;
        binding.actionToPreviusControllerFragment.setOnClickListener {
            activity?.apply {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
        binding.actionToBluetoothControllerFragment.setOnClickListener {

            activity?.apply {
                it.findNavController()
                    .navigate(R.id.action_controllersFragment2_to_bluetoothFragment)
            }
        }
    }

    fun moveForwardRight() {
        if (isButton1Pressed && isButton2Pressed) {
            // Ambos botones están presionados
            // Realiza tus validaciones o acciones aquí
            bluetoothControlListener?.enviarComandoBluetooth("I")
        }
    }

    fun moveForwardLeft() {
        if (isButton1Pressed && isButton2Pressed) {
            // Ambos botones están presionados
            // Realiza tus validaciones o acciones aquí
            bluetoothControlListener?.enviarComandoBluetooth("G")
        }
    }

    fun moveBackwardsLeft() {
        if (isButton1Pressed && isButton2Pressed) {
            // Ambos botones están presionados
            // Realiza tus validaciones o acciones aquí
            bluetoothControlListener?.enviarComandoBluetooth("H")
        }
    }

    fun moveBackwardsRight() {
        if (isButton1Pressed && isButton2Pressed) {
            // Ambos botones están presionados
            // Realiza tus validaciones o acciones aquí
            bluetoothControlListener?.enviarComandoBluetooth("J")
        }
    }

    fun manejarVelocidad(progress: Int) {
        when (progress) {
            10 -> {
                bluetoothControlListener?.enviarComandoBluetooth("1")
            }

            20 -> {
                bluetoothControlListener?.enviarComandoBluetooth("2")
            }

            30 -> {
                bluetoothControlListener?.enviarComandoBluetooth("3")
            }

            40 -> {
                bluetoothControlListener?.enviarComandoBluetooth("4")
            }

            50 -> {
                bluetoothControlListener?.enviarComandoBluetooth("5")
            }

            60 -> {
                bluetoothControlListener?.enviarComandoBluetooth("6")
            }

            70 -> {
                bluetoothControlListener?.enviarComandoBluetooth("7")
            }

            80 -> {
                bluetoothControlListener?.enviarComandoBluetooth("8")
            }

            90 -> {
                bluetoothControlListener?.enviarComandoBluetooth("9")
            }

            100 -> {
                bluetoothControlListener?.enviarComandoBluetooth("q")
            }
        }
    }

    private fun startCounter() {
        scope?.cancel() // Cancela el contador anterior si existe

        scope = CoroutineScope(Dispatchers.Main)
        scope?.launch {
            var count = 0
            while (isActive) {
                delay(10) // Espera 1 segundo
                updateCounter(count)
                count++
            }
        }
    }
    private fun stopCounter() {
        scope?.cancel()
    }

    private fun updateCounter(count: Int) {
        binding.counter.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope?.cancel() // Cancela el contador cuando se destruye la vista del fragmento
    }
}