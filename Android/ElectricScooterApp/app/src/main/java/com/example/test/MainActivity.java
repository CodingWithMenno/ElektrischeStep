package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String HC06Address = "98:D3:81:FD:41:7B";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.statusTextView = findViewById(R.id.statusText);
    }

    public void findTheScooter(View v) {
        this.statusTextView.setText("Connecting to the scooter...");

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            this.statusTextView.setText("This device does not support bluetooth");
            return;
        }

        if (!this.bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);
            this.statusTextView.setText("Try again");
            return;
        }

        Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String deviceAddress = device.getAddress();
            if (deviceAddress.equals(this.HC06Address)) {
                this.statusTextView.setText("Scooter found :)");
                connectToScooter(device);
                return;
            }
        }

        this.statusTextView.setText("Can't find the scooter :(");
        return;
    }

    private void connectToScooter(BluetoothDevice scooter){
        try {
            try {

                this.bluetoothSocket = scooter.createRfcommSocketToServiceRecord(this.MY_UUID);
                this.bluetoothAdapter.cancelDiscovery();
                this.bluetoothSocket.connect();
                this.inputStream = this.bluetoothSocket.getInputStream();
                this.outputStream = this.bluetoothSocket.getOutputStream();

                this.outputStream.write("CONNECT".getBytes());

                String received = "";

                do {
                    received += Character.toString((char)this.inputStream.read());
                    Thread.sleep(200);
                } while (this.inputStream.available() > 0);

                if (received.contains("OKE")) {
                    this.statusTextView.setText("Connected to the scooter :)");
                } else {
                    this.statusTextView.setText("The scooter did not respond :(");
                    return;
                }

                this.outputStream.write("START".getBytes());

            } catch (IOException e) {
                this.statusTextView.setText("Er ging iets fout tijdens het verbinden");

                try {
                    this.bluetoothSocket.close();
                } catch (IOException f) {
                    f.printStackTrace();
                }

                Thread.sleep(500);
                finish();
            }
        } catch (InterruptedException i) {
            finish();
        }
    }
}