package com.pradumnkmahanta.nearbyplaces.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Pradumn K Mahanta on 02-02-2017.
 **/

public class PermissionsHandler {

    private Context applicationContext;
    private static final Integer LOCATION = 0x1;
    private static final Integer GPS_SETTINGS = 0x7;
    private GoogleApiClient googleApiClient;

    public PermissionsHandler(Context context){
        this.applicationContext = context;
        googleApiClient = new GoogleApiClient.Builder(applicationContext)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        askForGPS();
    }

    /***HANDLING PERMISSIONS AND GPS***/
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) applicationContext, permission)) {
                ActivityCompat.requestPermissions((Activity) applicationContext, new String[]{permission}, requestCode);
            }else {
                ActivityCompat.requestPermissions((Activity) applicationContext, new String[]{permission}, requestCode);
            }
        } else {
            Log.i("GPS Permission", "Permission is already granted.");
        }
    }

    private void askForGPS(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult((Activity)applicationContext, GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Permission Handler", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }
    /***HANDLING PERMISSIONS AND GPS***/ //END
}
