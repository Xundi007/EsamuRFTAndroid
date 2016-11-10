package hu.esamu.rft.esamurft;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Sanya on 2016. 11. 10.
 * https://developer.android.com/guide/topics/location/strategies.html
 * https://developer.android.com/guide/components/services.html
 */
public class GPSService extends Service {

    // Acquire a reference to the system Location Manager
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Nem bindelünk.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Szolgáltatás indul...", Toast.LENGTH_SHORT).show();
        if (isLocationAvailableAndConnected()) {
            createGPSListener();
            return START_STICKY;
        } else {
            Toast.makeText(this, "NINCS GPS?! A szolgáltatás leáll!", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        return 0;
    }

    private boolean isLocationAvailableAndConnected() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isGPSActive = /*manager.getLastKnownLocation() != null*/true;
        return isGPSEnabled && isGPSActive;
    }

    private void createGPSListener() {
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            Toast.makeText(this, "NINCS GPS engedély?! A szolgáltatás leáll!", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    private void makeUseOfNewLocation(Location location) {
        //Itt hasznosíthatjuk fel a megszerzett koordinátákat. Például elküldeni a szervernek.
        Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.removeUpdates(locationListener);
        Toast.makeText(this, "Szolgáltatás leállt.", Toast.LENGTH_SHORT).show();
    }
}