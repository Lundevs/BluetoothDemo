package com.example.bluetoothtut;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class BLEscanner {

    final int REQUEST_ENABLE_BT = 1;

    private Context context;
    private BluetoothAdapter BTadapter;
    private BLEscanCallback scanCallback;
    private boolean scanning;


    public BLEscanner(Context context) {
        this.context = context;

        // Initializes Bluetooth adapter.
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BTadapter = bluetoothManager.getAdapter();
    }

    public void startScan(List<String> devicesAdress){
            while(true) {
                if (scanning){
                    break;
                }
                scanning = true;

                // Ensures Bluetooth is available on the device and it is enabled. If not,
                // displays a dialog requesting user permission to enable Bluetooth.
                if (BTadapter == null || !BTadapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    ((AppCompatActivity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                //TODO Check and Request Appropriate Permissions

                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ((AppCompatActivity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

                scanCallback = new BLEscanCallback(context);
                BTadapter.getBluetoothLeScanner().startScan(scanCallback);

                /// USE TO SCAN FOR TAGS WITH DEVICE ADDRESS "63:D0:31:71:81:BF" --- Should be used in the service

                List<ScanFilter> filters = new ArrayList<>();
                for (String adress : devicesAdress) {
                    ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(adress).build();
                    filters.add(filter);

                }
                ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                BTadapter.getBluetoothLeScanner().startScan(filters,settings,scanCallback);
                break;
            }
    }

    public void stopScan(){
        if(BTadapter != null && scanCallback != null && scanning) {
            scanning = false;
            BTadapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }

}
