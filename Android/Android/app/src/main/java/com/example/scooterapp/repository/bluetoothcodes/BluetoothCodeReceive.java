package com.example.scooterapp.repository.bluetoothcodes;

public enum BluetoothCodeReceive {
    CONNECT(119),
    STOPSENDING(120),
    EMERGENCYSTOP(121),
    SWITCHBRAKE(124);

    private int command;

    BluetoothCodeReceive(int command) {
        this.command = command;
    }

    public int value() {
        return command;
    }
}
