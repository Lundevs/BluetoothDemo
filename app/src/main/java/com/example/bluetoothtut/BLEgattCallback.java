package com.example.bluetoothtut;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;
import java.util.UUID;


// "00002902-0000-1000-8000-00805f9b34fb" 2902 uuid
public class BLEgattCallback extends BluetoothGattCallback {

    private Context context;
    BluetoothGattCharacteristic characteristicData;

    public BLEgattCallback(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if(newState == BluetoothGatt.STATE_CONNECTED) {
            gatt.requestMtu(256);
            try {
                Thread.sleep(800);
            }catch (java.lang.InterruptedException e){
                Log.d("interupted","sleep");
            }
            gatt.discoverServices();
            /*
            for (int i = 0; i<5; i++){
                gatt.discoverServices();
                try {
                    Thread.sleep(100);
                }catch (java.lang.InterruptedException e){
                    Log.d("interupted","sleep");
                }
            }
            */
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.e("SERVICE FOUND ","test");
        if(status == BluetoothGatt.GATT_SUCCESS){
            characteristicData = gatt.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")).getCharacteristic(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"));
            if (characteristicData != null){
                gatt.setCharacteristicNotification(characteristicData, true);
                BluetoothGattDescriptor descriptor = characteristicData.getDescriptor(
                        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                Log.e("SERVICE FOUND ","carac found");
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        //if (characteristic.getUuid().equals(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"))){
            sendGatt(characteristic.getStringValue(0));
        //}
        Log.e("CHARACTERISTIC_CHANGED ",gatt.getDevice().getName()+" "+characteristic.getStringValue(0));
    }

    private void sendGatt(String value) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("update-UI-gatt");
        // You can also include some extra data.
        intent.putExtra("message", value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
