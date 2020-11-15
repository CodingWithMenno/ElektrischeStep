package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.test.bluetoothCodes.BluetoothCodeSend;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private BluetoothHandler bluetoothHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.bluetoothHandler = new BluetoothHandler();
        this.statusTextView = findViewById(R.id.statusText);
    }

    public void searchForScooterButtonClicked(View v) {
        int bluetoothStatus = this.bluetoothHandler.findTheScooter();

        switch (bluetoothStatus) {
            case 0:
                this.statusTextView.setText("Device not supported");
                break;
            case 1:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
                this.statusTextView.setText("Try again");
                break;
            case 2:
                this.statusTextView.setText("Connected :D");
                Intent ecoIntent = new Intent(this, EcoActivity.class);
                ecoIntent.putExtra("scooter", new ScooterInfo());

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startActivity(ecoIntent);
                break;
            case 3:
                this.statusTextView.setText("Scooter does not respond :(");
                break;
            case 4:
                this.statusTextView.setText("Scooter not found");
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothHandler.sendData(BluetoothCodeSend.STOPSENDING);
    }
}