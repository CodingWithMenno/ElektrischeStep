package com.example.scooterapp.viewmodel;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scooterapp.repository.BluetoothHandler;
import com.example.scooterapp.repository.BluetoothObserver;
import com.example.scooterapp.repository.GpsHandler;
import com.example.scooterapp.repository.GpsObserver;
import com.example.scooterapp.repository.bluetoothcodes.BluetoothCodeSend;

/**
 * THIS CLASS IS A SINGLETON CLASS
 */
public class ScooterDataHandler implements BluetoothObserver, GpsObserver {

    private static ScooterDataHandler single_instance = null;
    private ScooterObserver scooterObserver;
    private BluetoothHandler bluetoothHandler;
    private GpsHandler gpsHandler;

    // Scooter values
    private boolean isElectronicBrakeOn;
    private float power;
    private boolean isReceivingPower;
    private boolean isConnected;
    private boolean isInEmergencyStop;
    private boolean isReceivingSpeed;

    private ScooterDataHandler() {
        this.isElectronicBrakeOn = false;
        this.power = 0.0f;
        this.isReceivingPower = false;
        this.isConnected = false;
        this.isInEmergencyStop = false;
        this.isReceivingSpeed = false;
    }

    public static ScooterDataHandler getInstance() {
        if (single_instance == null)
            single_instance = new ScooterDataHandler();

        return single_instance;
    }


    public void setScooterObserver(ScooterObserver scooterObserver) {
        this.scooterObserver = scooterObserver;
    }

    @Override
    public void updateBluetooth(int scooterPower) {
        if (this.scooterObserver != null) {
            this.scooterObserver.updateScooterPower(scooterPower);
        }
    }

    @Override
    public void updateGPS(double scooterSpeed) {
        if (this.scooterObserver != null) {
            this.scooterObserver.updateScooterSpeed(scooterSpeed);
        }
    }

    //region methods to manage the scooter

    public int connectToScooter() {
        if (this.bluetoothHandler == null) {
            this.bluetoothHandler = new BluetoothHandler();
        }

        this.bluetoothHandler.setObserver(this);

        int status = this.bluetoothHandler.findTheScooter();

        if (status == 2) {
            this.isConnected = true;
        }

        return status;
    }

    public void willReceiveScooterPower(boolean will) {
        if (!this.isConnected) {
            return;
        }

        if (will) {
            this.bluetoothHandler.startReceivingPower();
            this.isReceivingPower = true;
            this.isInEmergencyStop = false;
        } else {
            this.bluetoothHandler.sendData(BluetoothCodeSend.STOPSENDING);
            this.isReceivingPower = false;
            this.isInEmergencyStop = false;
        }
    }

    public void emergencyStop() {
        if (!this.isConnected) {
            return;
        }

        this.bluetoothHandler.sendData(BluetoothCodeSend.EMERGENCYSTOP);
        this.isInEmergencyStop = true;
        this.isReceivingPower = false;
    }

    public void switchElectronicBrake() {
        if (!this.isConnected) {
            return;
        }

        this.bluetoothHandler.sendData(BluetoothCodeSend.SWITCHBRAKE);
        this.isElectronicBrakeOn = !this.isElectronicBrakeOn;
    }

    public void willReceiveSpeed(boolean will, AppCompatActivity context) {
        if (will) {
            if (this.gpsHandler == null) {
                this.gpsHandler = new GpsHandler(context);
            }

            this.gpsHandler.setObserver(this);
            this.isReceivingSpeed = true;
        } else {
            if (this.gpsHandler == null) {
                return;
            }

            this.gpsHandler.setObserver(null);
            this.isReceivingSpeed = false;
        }
    }

    //endregion


    //region getters for scooter values

    public boolean isElectronicBrakeOn() {
        return isElectronicBrakeOn;
    }

    public float getPower() {
        return power;
    }

    public boolean isReceivingPower() {
        return isReceivingPower;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isInEmergencyStop() {
        return isInEmergencyStop;
    }

    public boolean isReceivingSpeed() {
        return isReceivingSpeed;
    }

    //endregion
}
