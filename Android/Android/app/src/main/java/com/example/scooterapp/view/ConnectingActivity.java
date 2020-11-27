package com.example.scooterapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.scooterapp.R;
import com.example.scooterapp.viewmodel.ScooterDataHandler;

public class ConnectingActivity extends AppCompatActivity {

    private ScooterDataHandler scooterDataHandler;

    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.statusTextView = findViewById(R.id.statusText);

        this.scooterDataHandler = ScooterDataHandler.getInstance();
    }

    public void searchForScooterButtonClicked(View v) {
        int connectStatus = this.scooterDataHandler.connectToScooter();

        switch (connectStatus) {
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
                Intent drivingIntent = new Intent(this, DrivingActivity.class);
                startActivity(drivingIntent);
                break;
            case 3:
                this.statusTextView.setText("Scooter does not respond :(");
                break;
            case 4:
                this.statusTextView.setText("Scooter not found");
                break;
        }
    }
}