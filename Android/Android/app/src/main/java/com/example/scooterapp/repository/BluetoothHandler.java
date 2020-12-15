package com.example.scooterapp.repository;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.scooterapp.repository.bluetoothcodes.BluetoothCodeReceive;
import com.example.scooterapp.repository.bluetoothcodes.BluetoothCodeSend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothHandler {

    private BluetoothObserver observer;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String HC06Address = "98:D3:81:FD:41:7B";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;


    public void setObserver(BluetoothObserver observer) {
        this.observer = observer;
    }


    public void sendData(BluetoothCodeSend code) {
        if (this.outputStream != null) {
            try {

                this.outputStream.write(code.value().getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Receives and sets the power from the scooter to the TextView
     * (Stops when the STOPSENDING OR EMERGENCYSTOP signal are received)
     */
    public void startReceivingPower() {
        if (this.inputStream == null) {
            return;
        }

        Thread receiveThread = new Thread(() -> {
            try {
                sendData(BluetoothCodeSend.STARTSENDING);

                int oldPower = 0;
                int power = 0;
                int difference = 0;
                while (power != BluetoothCodeReceive.STOPSENDING.value() && power != BluetoothCodeReceive.EMERGENCYSTOP.value()) {
                    difference = oldPower - power;
                    if (power <= 100 && (difference > 20 || difference < -20)) {
                        this.observer.updateBluetooth(power);
                    }
                    power = this.inputStream.read();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
    }

    /**
     * @return
     *      0 = device does not support bluetooth
     *      1 = bluetooth needs to be turned on
     *      2 = connected to the scooter
     *      3 = scooter does not respond
     *      4 = scooter not found
     */
    public int findTheScooter() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            return 0;
        }

        if (!this.bluetoothAdapter.isEnabled()) {
            return 1;
        }

        Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String deviceAddress = device.getAddress();
            if (deviceAddress.equals(this.HC06Address)) {
                boolean connected = connectToScooter(device);
                return connected? 2 : 3;
            }
        }

        return 4;
    }

    private boolean connectToScooter(BluetoothDevice scooter){
        try {
            this.bluetoothSocket = scooter.createRfcommSocketToServiceRecord(this.MY_UUID);
            this.bluetoothAdapter.cancelDiscovery();
            this.bluetoothSocket.connect();
            this.inputStream = this.bluetoothSocket.getInputStream();
            this.outputStream = this.bluetoothSocket.getOutputStream();

            sendData(BluetoothCodeSend.CONNECT);
            System.out.println("TEST 1");
            int received = this.inputStream.read();
            System.out.println("TEST 2");
            if (received != BluetoothCodeReceive.CONNECT.value()) {
                return false;
            }

            return true;

        } catch (IOException e) {
            try {
                this.bluetoothSocket.close();
            } catch (IOException f) {
                f.printStackTrace();
            }
        }
        return false;
    }
}
