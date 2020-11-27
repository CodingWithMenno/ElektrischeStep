package com.example.scooterapp.repository;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class GpsHandler implements LocationListener {

    private GpsObserver observer;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationManager locationManager;
    private boolean allowed;

    public GpsHandler(AppCompatActivity context) {
        this.allowed = false;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.allowed = true;
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, this);
        }
    }

    public void setObserver(GpsObserver observer) {
        if (this.allowed) {
            this.observer = observer;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (this.observer != null) {
            this.observer.updateGPS(location.getSpeed());
        }
    }
}
