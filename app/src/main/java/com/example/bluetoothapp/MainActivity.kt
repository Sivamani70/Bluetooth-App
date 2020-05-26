package com.example.bluetoothapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var hardWareList: ArrayList<String> = mutableListOf<String>() as ArrayList<String>
    private val list: ArrayList<String> = mutableListOf<String>() as ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Action type ", "${intent?.action}")

            if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device?.name
                val deviceHardwareAddress = device?.address
                Log.i("Device", "$device")
                Log.i("Name", "$deviceName")
                Log.i("Hardware", "$deviceHardwareAddress")
                if (!hardWareList.contains(deviceHardwareAddress.toString())) {
                    list.add(deviceName.toString())
                    hardWareList.add(deviceHardwareAddress.toString())
                    bluetoothDevicesList.adapter = adapter
                    adapter.notifyDataSetChanged()

                }


            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == intent?.action) {
                findBluetooth.isEnabled = true
                searchString.text = ""
                Log.i("Bluetooth ", "Search Finished")
                if (list.size == 0) {
                    list.add("No devices Found")
                    adapter.notifyDataSetChanged()
                }

            }


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)


        val permissionCheck: Int =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        Log.i("Permission Check", "$permissionCheck")
        if (permissionCheck == -1) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }

        callBluetooth()

        findBluetooth.setOnClickListener {
            callBluetooth()
            searchString.text = "Searching..."
            findBluetooth.isEnabled = false
            list.clear()
            hardWareList.clear()
            adapter.notifyDataSetChanged()
        }
        bluetoothDevicesList.adapter = adapter

    }

    private fun callBluetooth() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                val requestBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(requestBluetooth, 0)
            } else {
                val filter = IntentFilter()
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
                filter.addAction(BluetoothDevice.ACTION_FOUND)
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                registerReceiver(receiver, filter)
                bluetoothAdapter.startDiscovery()
            }
        } else {
            Log.d("Bluetooth", " Bluetooth is Not supported")
        }

    }


    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }
}
