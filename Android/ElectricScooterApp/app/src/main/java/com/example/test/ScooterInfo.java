package com.example.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class ScooterInfo implements Serializable {

    private boolean electronicBrakeOn;
    private boolean isInEcoMode;
    private float power;
    private boolean isReceivingPower;

    private PropertyChangeSupport support;

    public ScooterInfo() {
        this.electronicBrakeOn = false;
        this.power = .0f;
        this.isInEcoMode = true;
        this.isReceivingPower = false;

        this.support = new PropertyChangeSupport(this);
    }

    public boolean isReceivingPower() {
        return isReceivingPower;
    }

    public void setReceivingPower(boolean receivingPower) {
        isReceivingPower = receivingPower;
    }

    public boolean isElectronicBrakeOn() {
        return electronicBrakeOn;
    }

    public void setElectronicBrakeOn(boolean electronicBrakeOn) {
        this.electronicBrakeOn = electronicBrakeOn;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.support.firePropertyChange("power", this.power, power / 100);
        this.power = power;
    }

    public boolean isInEcoMode() {
        return isInEcoMode;
    }

    public void setInEcoMode(boolean inEcoMode) {
        isInEcoMode = inEcoMode;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
}
