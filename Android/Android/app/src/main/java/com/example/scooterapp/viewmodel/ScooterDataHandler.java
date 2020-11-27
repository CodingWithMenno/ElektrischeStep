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
    private boolean isInEcoMode;
    private float power;
    private boolean isReceivingPower;
    private boolean isConnected;
    private boolean isInEmergencyStop;

    private ScooterDataHandler() {
        this.isElectronicBrakeOn = false;
        this.power = 0.0f;
        this.isInEcoMode = true;
        this.isReceivingPower = false;
        this.isConnected = false;
        this.isInEmergencyStop = false;
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
        } else {
            this.bluetoothHandler.sendData(BluetoothCodeSend.STOPSENDING);
            this.isReceivingPower = false;
        }
    }

    public void setDrivingMode() {
        //if the scooter is connected
        //changes the driving mode of the scooter to the given mode (if not already in that mode)
    }

    public void emergencyStop(boolean bool) {
        //if the scooter is connected
        //this will turn the emergency stop of the scooter on or off
    }

    public void setElectronicBrake(boolean bool) {
        //if the scooter is connected
        //this will turn the electronic brake of the scooter on or off
    }

    public void willReceiveSpeed(boolean will, AppCompatActivity context) {
        if (will) {
            if (this.gpsHandler == null) {
                this.gpsHandler = new GpsHandler(context);
            }

            this.gpsHandler.setObserver(this);
        } else {
            this.gpsHandler.setObserver(null);
        }
    }

    //endregion


    //region getters for scooter values

    public boolean isElectronicBrakeOn() {
        return isElectronicBrakeOn;
    }

    public boolean isInEcoMode() {
        return isInEcoMode;
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

    //endregion
}
