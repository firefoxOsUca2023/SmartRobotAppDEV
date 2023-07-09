package com.example.zacatales.smartrobotapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothConnectionListener
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothManager
import com.example.zacatales.smartrobotapp.bluetooth.`interface`.BluetoothStateListener
import com.example.zacatales.smartrobotapp.databinding.FragmentControllersBinding
import com.example.zacatales.smartrobotapp.viewmodel.RobotViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class ControllersFragment : Fragment(), BluetoothStateListener {

    private lateinit var binding: FragmentControllersBinding
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothControlListener: BluetoothConnectionListener? = null
    private lateinit var routeButton: FloatingActionButton

    private lateinit var txtCount: TextView
    var count: Int = 0;
    private var scope: CoroutineScope? = null

    private val viewModel: RobotViewModel by activityViewModels()

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
        bluetoothControlListener = null
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

        // Resto del código del fragmento
        // ...
        binding = FragmentControllersBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("MissingPermission", "SuspiciousIndentation", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        (activity as? MainActivity)?.setBluetoothStateListener(this)
        routeButton = binding.actionToRouteControllerFragment
        routeButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_controllersFragment2_to_routeFragment)
        }

        val pressButton = ContextCompat.getColorStateList(requireContext(), R.color.pressColor)
        //val colorStateList2 = ContextCompat.getColorStateList(requireContext(), R.color.btnColor)
        val modeDark = ContextCompat.getColorStateList(requireContext(), R.color.hornPressButton)
        val ColorDefault = ContextCompat.getColorStateList(requireContext(), R.color.white)
        val ColorPresstLight = ContextCompat.getColorStateList(requireContext(), R.color.btnColor)



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


            binding.lightsActionButton.setOnClickListener {
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    buttonPressCount++
                    if (buttonPressCount == 1) {
                        binding.lightsActionButton.setBackgroundTintList(ColorDefault)
                        bluetoothControlListener?.enviarComandoBluetooth("X")
                    } else if (buttonPressCount == 2) {
                        binding.lightsActionButton.setBackgroundTintList(modeDark)
                        bluetoothControlListener?.enviarComandoBluetooth("x")
                        buttonPressCount = 0
                    }
                } else {
                    buttonPressCount2++
                    if (buttonPressCount2 == 1) {
                        binding.lightsActionButton.setBackgroundTintList(pressButton)
                        bluetoothControlListener?.enviarComandoBluetooth("X")
                    } else if (buttonPressCount2 == 2) {
                        binding.lightsActionButton.setBackgroundTintList(ColorPresstLight)
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
                        binding.hornActionButton.setBackgroundTintList(ColorDefault)
                        bluetoothControlListener?.enviarComandoBluetooth("V")
                    }

                    MotionEvent.ACTION_UP -> {
                        // Se soltó el botón
                        binding.hornActionButton.setBackgroundTintList(modeDark)
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
                        binding.hornActionButton.setBackgroundTintList(ColorPresstLight)
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
                        startCounter()
                        isButton1Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("F")
                        binding.upActionButton.setBackgroundTintList(ColorDefault)
                        viewModel.addClick("F")
                        moveForwardRight()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        isButton1Pressed = false
                        binding.upActionButton.setBackgroundTintList(modeDark)
                        moveForwardRight()
                    }
                }
                true
            } else{
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startCounter()
                        isButton1Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("F")
                        binding.upActionButton.setBackgroundTintList(pressButton)
                        viewModel.addClick("F")
                        moveForwardRight()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        isButton1Pressed = false
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        binding.upActionButton.setBackgroundTintList(ColorPresstLight)
                        moveForwardRight()
                    }
                }
                true
            }
        }

        txtCount = binding.counter

        binding.rightActionButton.setOnTouchListener { view, motionEvent ->
            //startCounter()
            //count++
            //txtCount.text = count.toString()
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startCounter()
                        isButton1Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("R")
                        binding.rightActionButton.setBackgroundTintList(ColorDefault)
                        viewModel.addClick("R")
                        moveForwardRight()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        isButton1Pressed = false
                        binding.rightActionButton.setBackgroundTintList(modeDark)
                        moveForwardRight()
                    }
                }
                true
            } else{
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startCounter()
                        isButton1Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("R")
                        binding.rightActionButton.setBackgroundTintList(pressButton)
                        viewModel.addClick("R")
                        moveForwardRight()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        isButton1Pressed = false
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        binding.rightActionButton.setBackgroundTintList(ColorPresstLight)
                        moveForwardRight()
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
                        startCounter()
                        isButton2Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("L")
                        binding.leftActionButton.setBackgroundTintList(ColorDefault)
                        viewModel.addClick("L")
                        moveForwardLeft()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        isButton2Pressed = false
                        binding.leftActionButton.setBackgroundTintList(modeDark)
                        moveForwardLeft()
                    }
                }
                true
            } else{
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startCounter()
                        isButton2Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("L")
                        binding.leftActionButton.setBackgroundTintList(pressButton)
                        viewModel.addClick("L")
                        moveForwardLeft()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        isButton2Pressed = false
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        binding.leftActionButton.setBackgroundTintList(ColorPresstLight)
                        moveForwardLeft()
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
                        startCounter()
                        isButton2Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("B")
                        binding.backActionButton.setBackgroundTintList(ColorDefault)
                        viewModel.addClick("B")
                        moveBackwardsLeft()
                        moveBackwardsRight()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        isButton2Pressed = false
                        binding.backActionButton.setBackgroundTintList(modeDark)
                        moveBackwardsLeft()
                        moveBackwardsRight()
                    }
                }
                true
            } else{
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startCounter()
                        isButton2Pressed = true
                        bluetoothControlListener?.enviarComandoBluetooth("B")
                        binding.backActionButton.setBackgroundTintList(pressButton)
                        viewModel.addClick("B")
                        moveBackwardsLeft()
                        moveBackwardsRight()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopCounter()
                        isButton2Pressed = false
                        bluetoothControlListener?.enviarComandoBluetooth("S")
                        binding.backActionButton.setBackgroundTintList(ColorPresstLight)
                        moveBackwardsLeft()
                        moveBackwardsRight()
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

       /*txtCount = binding.counter
        binding.rightActionButton.setOnClickListener {
            count++
            txtCount.text = count.toString()
        }*/
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

    override fun onBluetoothStateChanged(state: Boolean) {
        if(state){
            Log.i("eee","JEEJJEEJEJ")
        }
        else{
            Log.i("l","LOL")
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
        txtCount.text = count.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope?.cancel() // Cancela el contador cuando se destruye la vista del fragmento
    }
}


