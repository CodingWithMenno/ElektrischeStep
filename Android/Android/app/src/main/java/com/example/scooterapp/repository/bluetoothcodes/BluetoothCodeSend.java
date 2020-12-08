package com.example.scooterapp.repository.bluetoothcodes;

public enum BluetoothCodeSend {
    CONNECT("CONNECT"),
    STARTSENDING("START"),
    STOPSENDING("STOP"),
    EMERGENCYSTOP("QUIT"),
    SWITCHBRAKE("BRAKE");

    private String command;

    BluetoothCodeSend(String command) {
        this.command = command;
    }

    public String value() {
        return command;
    }
}

