package com.example.elektrischefiets;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String HC06Address = "98:D3:81:FD:41:7B";

    private TextView progressTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.progressTextBox = findViewById(R.id.ProgressTextView);
    }

    public void onClick(View view) throws InterruptedException {
        this.progressTextBox.setText("Verbinding maken met de step...");

        if (!makeConnectionSure()) {
            this.progressTextBox.setText("Probeer opnieuw");
            return;
        }

        this.progressTextBox.setText("Je bent verbonden met de step :D");
    }

    public boolean makeConnectionSure() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            makeToast("Dit apparaat werkt niet met bluetooth", true);
            return false;
        }

        if (!this.bluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
            return false;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String deviceAddress = device.getAddress();
            if (deviceAddress.equals(this.HC06Address)) {
                connectToScooter();
                return true;
            }
        }

        makeToast("Je telefoon kan de step niet vinden", false);
        return false;
    }

    private void connectToScooter() {
        BluetoothDevice device = this.bluetoothAdapter.getRemoteDevice(this.HC06Address);

        try {
            this.bluetoothSocket = device.createRfcommSocketToServiceRecord(this.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            makeToast("Het maken van de socket is niet gelukt", true);
        }

        this.bluetoothAdapter.cancelDiscovery();

        makeToast("Verbinding aan het maken met de step", false);
        try {
            this.bluetoothSocket.connect();
            makeToast("Je bent verbonden met de step :D", false);
        } catch (IOException e) {
            try {
                this.bluetoothSocket.close();
            } catch (IOException e2) {
                makeToast("Niet verbonden met de step, en kan de socket niet sluiten", true);
            }
        }

        try {
            this.outputStream = this.bluetoothSocket.getOutputStream();
            this.inputStream = this.bluetoothSocket.getInputStream();
        } catch (IOException e) {
            makeToast("Kon de streams niet openen", true);
        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            this.outputStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            makeToast(msg, true);
        }
    }

    private void makeToast(String message, boolean exitAfter) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();

        if (exitAfter) {
            finish();
        }
    }
}