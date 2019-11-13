package com.example.bluetoothtut;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BLEscanCallback extends ScanCallback {

    private Context context;
    private BLEgattCallback gattCallback;
    private BluetoothGatt bluetoothGatt;
    private Map<String, BluetoothDevice> foundDevices;
    private int count =0;

    public BLEscanCallback(Context context) {
        super();
        this.context = context;
        gattCallback = new BLEgattCallback(context);
        foundDevices = new HashMap<>();
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if(result.getDevice().getName() != null){
            //The count variable is to not update the listview too often... can be removed
            if(count < 3){
                // Scanning done purely by the visible meta data and no real pairing is required
                if(!foundDevices.containsKey(result.getDevice().getAddress())){
                    foundDevices.put(result.getDevice().getAddress(),result.getDevice());

                    // simply to show its working
                    ((MainActivity)context).updateList(foundDevices);
                    Log.e("AAAAAAAAAA"," "+result.getDevice().getAddress().toString());
                }
                count++;
            }else{
                count=0;
                foundDevices.clear();
            }

            bluetoothGatt = result.getDevice().connectGatt(context, false, gattCallback);

        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        //TODO implement batch scan results
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
        Log.e("BlueTooth LE", "SCAN FAILURE!");
    }

}


/*TODO Implement a service that will run in the backgorund if registered tag lsit has at least one element
        when the element that is tracked not found notify user (or print to log for now) using bradcast service.*/
