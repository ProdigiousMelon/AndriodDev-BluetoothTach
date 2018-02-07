package com.example.bgiles7710.bluetoothconsolev2;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
    BluetoothSocket mmSocket;
    private Set<BluetoothDevice>pairedDevices;
    BluetoothDevice mmDevice;
    ListView lv;
    public UUID TachUUID;
    public byte [] buffer = new byte[256];
    public int bytes;
    public String btMessage = null;
    TextView display;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    OutputStream mmOutputStream;
    InputStream mmInputStream;


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
        try{
            findBT();
            openBT();
        }
        catch (Exception e){
            Log.d((String)this.getTitle(),"Error getting datastream: " + e);
        }

    }

    public void openBT() throws IOException{
        TachUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        //BluetoothServerSocket BSS = BA.listenUsingInsecureRfcommWithServiceRecord("HC-06", TachUUID);
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(TachUUID);
        mmSocket.connect();

        display.setText("Bluetooth Open");
        beginListenForData();
    }
    void findBT()
    {
        BA = BluetoothAdapter.getDefaultAdapter();
        if(BA == null)
        {
            display.setText("No bluetooth adapter available");
        }

        if(!BA.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        pairedDevices = BA.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-06"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        display.setText("Bluetooth Device Found");
    }

    void beginListenForData() throws IOException {
        mmInputStream = mmSocket.getInputStream();
        byte[] buffer = new byte[1024];
        int bytes;

        while(true){
            try{
                bytes = mmInputStream.read(buffer);
                String readMessage = new String(buffer,0,bytes);
                display.setText(readMessage);
            }
            catch(IOException e){
                Log.i("logging", ": from read loop: " + e);
                break;
            }
        }

        /*
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];


        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            display.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
        */
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
