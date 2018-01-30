package com.example.bgiles7710.bluetoothconsolev2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.content.Intent;
import android.bluetooth.BluetoothServerSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //global variables
    Button btnFPS;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    public UUID TachUUID;
    public byte [] buffer = new byte[256];
    public int bytes;
    public String btMessage = null;
    TextView display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFPS = (Button)findViewById(R.id.btnFPS);
        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.lvConnections);
        display = (TextView) findViewById(R.id.txtDisplay);

    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }

    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    //applies the fps filter
    public void getFPS(View v){

    }

    //establishes bluetooth socket connection with the device.
    public void BTConnect(View v) throws IOException {
        //get device UUID
//        pairedDevices = BA.getBondedDevices();
//        for(BluetoothDevice bt : pairedDevices){
//            if(bt.getName() == "HC-06"){
//                TachUUID = bt.getUuids()[0].getUuid();
//            }
//        }
        TachUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        BluetoothServerSocket BSS = BA.listenUsingInsecureRfcommWithServiceRecord("HC-06", TachUUID);

        try{
            Log.d((String)this.getTitle(),"attempting to get input and output streams");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            DataInputStream mmInStream = new DataInputStream(tmpIn);
            DataOutputStream mmOutStream = new DataOutputStream(tmpOut);

            bytes = mmInStream.read(buffer);
            btMessage = new String(buffer, 0, bytes);

            display.setText(btMessage);
        }
        catch (Exception e){
            Log.d((String)this.getTitle(),"Error getting datastream: " + e);
        }

    }


    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void list(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }

}
