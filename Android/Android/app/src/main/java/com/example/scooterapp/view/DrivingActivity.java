package com.example.scooterapp.view;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scooterapp.R;
import com.example.scooterapp.viewmodel.ScooterDataHandler;
import com.example.scooterapp.viewmodel.ScooterObserver;
import com.progress.progressview.ProgressView;

public class DrivingActivity extends AppCompatActivity implements ScooterObserver {

    private Handler powerMeterThread;

    private ScooterDataHandler scooterDataHandler;

    private ProgressView powerMeterLeft;
    private ProgressView powerMeterRight;
    private TextView speedMeter;
    private Button startSendingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.scooterDataHandler = ScooterDataHandler.getInstance();
        this.scooterDataHandler.setScooterObserver(this);

        this.powerMeterLeft = findViewById(R.id.powerMeterLeft);
        this.powerMeterRight = findViewById(R.id.powerMeterRight);
        this.speedMeter = findViewById(R.id.speedMeter);
        this.startSendingButton = findViewById(R.id.btnStart);

        int[] colorList = new int[]{Color.GREEN, Color.YELLOW, Color.RED};
        this.powerMeterLeft.applyGradient(colorList);
        this.powerMeterRight.applyGradient(colorList);

        this.powerMeterThread = Handler.createAsync(Looper.getMainLooper());
    }

    @Override
    public void updateScooterSpeed(double scooterSpeed) {
        int roundedSpeed = (int) Math.round(scooterSpeed);
        String formatted = String.format("%02d", roundedSpeed);
        this.speedMeter.setText(formatted);
    }

    @Override
    public void updateScooterPower(int scooterPower) {
        float mappedPower = scooterPower / 100f;

//        this.powerMeterLeft.post(() -> {
//            this.powerMeterLeft.setProgress(mappedPower);
//            this.powerMeterRight.setProgress(mappedPower);
//        });

        this.powerMeterThread.post(() -> {
            this.powerMeterLeft.setProgress(mappedPower);
            this.powerMeterRight.setProgress(mappedPower);
        });
    }

    public void onElectronicBrakeClicked(View view) {
        this.scooterDataHandler.switchElectronicBrake();
    }

    public void onEmergencyStopClicked(View view) {
        this.scooterDataHandler.emergencyStop();
        this.startSendingButton.setText("STOP");
    }

    public void onStartClick(View view) {
        if (!this.scooterDataHandler.isReceivingPower()) {
            this.startSendingButton.setText("STOP");
            this.scooterDataHandler.willReceiveScooterPower(true);
            this.scooterDataHandler.willReceiveSpeed(true, this);
        } else {
            this.startSendingButton.setText("START");
            this.scooterDataHandler.willReceiveScooterPower(false);
            this.scooterDataHandler.willReceiveSpeed(false, this);
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.powerMeterLeft.setProgress(0);
        this.powerMeterRight.setProgress(0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (this.scooterDataHandler != null) {
            this.scooterDataHandler.willReceiveScooterPower(false);
            this.scooterDataHandler.willReceiveSpeed(false, this);
        }
    }
}