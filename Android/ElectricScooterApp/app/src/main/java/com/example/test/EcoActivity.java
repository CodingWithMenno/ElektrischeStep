package com.example.test;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.test.bluetoothCodes.BluetoothCodeSend;
import com.progress.progressview.ProgressView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

public class EcoActivity extends AppCompatActivity implements View.OnClickListener, PropertyChangeListener {

    private ScooterInfo scooterInfo;

    private SwitchCompat electricBrakeSwitch;
    private ProgressView ecoPowerMeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        this.scooterInfo = (ScooterInfo) intent.getSerializableExtra("scooter");
        this.scooterInfo.addPropertyChangeListener(this);

        this.electricBrakeSwitch = findViewById(R.id.ecoElectricBrake);
        this.ecoPowerMeter = findViewById(R.id.ecoPowerMeter);
        findViewById(R.id.btnEcoStart).setOnClickListener(this);

        int[] colorList = new int[]{Color.GREEN, Color.YELLOW, Color.RED};
        ecoPowerMeter.applyGradient(colorList);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEcoStart) {
            Button b = findViewById(view.getId());
            if (!this.scooterInfo.isReceivingPower()) {
                b.setText("STOP");
                BluetoothHandler.startReceivingData(this.scooterInfo);
            } else {
                b.setText("START");
                BluetoothHandler.sendData(BluetoothCodeSend.STOPSENDING);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.ecoPowerMeter.setProgress(0);
        }
    }

    public void onElectronicBrakeClicked(View v) {
        this.scooterInfo.setElectronicBrakeOn(this.electricBrakeSwitch.isChecked());
        BluetoothHandler.sendData(BluetoothCodeSend.SWITCHBRAKE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothHandler.sendData(BluetoothCodeSend.STOPSENDING);
        this.scooterInfo.removePropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.ecoPowerMeter.post(() -> {
            this.ecoPowerMeter.setProgress((Float) evt.getNewValue());
        });
    }
}