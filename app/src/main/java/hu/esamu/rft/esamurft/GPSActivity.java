package hu.esamu.rft.esamurft;

import android.Manifest;
import android.app.ActivityManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Sanya on 2016. 11. 10.
 */
public class GPSActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private LocationManager locationManager;

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        locationManager  = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Manager");
            builder.setMessage("Would you like to enable GPS?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(i, 666);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //No location service, no Activity
                    finish();
                }
            });
            builder.create().show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        mPermissionDenied = !PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Widgets of the user interface.
     * */
    private TextView gps;
    private Button btnStart;
    private Button btnStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        enableMyLocation();

        gps = (TextView) findViewById(R.id.text_gps);
        btnStart = (Button) findViewById(R.id.btn_start_service);
        btnStop = (Button) findViewById(R.id.btn_stop_service);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPermissionDenied == false && !isMyServiceRunning(GPSService.class)) {
                    Intent intent = new Intent(GPSActivity.this, GPSService.class);
                    startService(intent);
                    gps.setText("Service ON");
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPermissionDenied == false) {
                    Intent intent = new Intent(GPSActivity.this, GPSService.class);
                    stopService(intent);
                    gps.setText("Service OFF");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMyServiceRunning(GPSService.class)) {
            gps.setText("SZOLGÁLTATÁS FUT!");
        } else {
            gps.setText("SZOLGÁLTATÁS NEM FUT");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 666) {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "You just escaped the settings, GTFO", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
