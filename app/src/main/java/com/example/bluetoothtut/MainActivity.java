package com.example.bluetoothtut;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    BLEscanner temp;
    public TextView text;
    private ArrayList<String> taglist;
    private ArrayAdapter<String> itemsAdapter;

    private String TRACKED_BLE_ADDRESS = "63:D0:31:71:81:BF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.htxt);
        taglist = new ArrayList<>();

        //Check Permissions
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported!", Toast.LENGTH_SHORT).show();
            finish(); //QUIT THE APP
        }

        temp = new BLEscanner(this);

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, taglist);
        ((ListView) findViewById(R.id.taglist)).setAdapter(itemsAdapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("update-UI-gatt"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            showState(message);
        }
    };

    public void startScan(View v) {
        Log.e("DDDD", "STARTED !");
        temp.startScan(Arrays.asList("3C:71:BF:F1:E4:76"));
    }

    public void stopScan(View v) {
        Log.e("DDDD", "STOPPED !");
        temp.stopScan();
    }

    public void updateList(Map<String, BluetoothDevice> map) {
        taglist.clear();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            taglist.add(map.get(it.next()).getAddress().toString());
        }
        itemsAdapter.notifyDataSetChanged();
    }

    public void showState(String value) {
        text.setText(value);
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}