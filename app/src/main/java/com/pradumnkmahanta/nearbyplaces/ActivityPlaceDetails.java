package com.pradumnkmahanta.nearbyplaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pradumnkmahanta.nearbyplaces.utilities.AlertDialogManager;
import com.pradumnkmahanta.nearbyplaces.utilities.ApplicationData;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ActivityPlaceDetails extends AppCompatActivity implements OnMapReadyCallback{

    MapFragment mapFragment;
    GoogleMap googleMap;
    //android.support.v7.app.ActionBar actionBar;

    AlertDialogManager alert;

    ImageView placeImage;
    TextView placeStatus;

    LoadImage loadImage;

    String placeName, placeVicinity, placeLat, placeLng, placePhotoRef, placeDistance, placeType;

    public static String KEY_NAME = "Name";
    public static String KEY_ADDRESS = "Address";
    public static String KEY_PHOTOREFERENCE = "PhotoReference";
    public static String KEY_LATITUDE = "Latitude";
    public static String KEY_LONGITUDE = "Longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        Intent fromMain = getIntent();
        placeVicinity = fromMain.getStringExtra(KEY_ADDRESS);
        placeName = fromMain.getStringExtra(KEY_NAME);
        placeLat = fromMain.getStringExtra(KEY_LATITUDE);
        placeLng = fromMain.getStringExtra(KEY_LONGITUDE);
        placePhotoRef = fromMain.getStringExtra(KEY_PHOTOREFERENCE);
        setTitle(placeName);

        //actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        alert = new AlertDialogManager();

        placeStatus = (TextView) findViewById(R.id.placeStatus);
        placeImage = (ImageView) findViewById(R.id.placeImage);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(placePhotoRef.equals("Not Available")){
            placeStatus.setText("Sorry Image Not Available.\nAddress : " + placeVicinity);
        }else{
            loadImage = new LoadImage();
            loadImage.execute(placePhotoRef);
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {

        }
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng placeLocation = new LatLng(Double.parseDouble(placeLat), Double.parseDouble(placeLng));
        Marker placeMarker = googleMap.addMarker(new MarkerOptions().position(placeLocation).title(placeName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
    }


    class LoadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try{
                InputStream inputStream = new URL(ApplicationData.PLACES_IMAGE_URL + "&key=" + ApplicationData.ServerAPI + "&photoreference=" + params[0]).openStream();
                return BitmapFactory.decodeStream(inputStream);
            }catch(MalformedURLException mue){
                return null;
            }catch (IOException ioe){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmapImage) {
            if(bitmapImage != null){
                placeStatus.setText("Address : " + placeVicinity);
                placeImage.setImageBitmap(bitmapImage);
            }else{
                placeStatus.setText("Sorry failed to retrieve Image.\nAddress : " + placeVicinity);
                alert.showAlertDialog(getApplicationContext(), "Place Image", "Sorry failed to retrieve Image.", false);
            }
            placeStatus.setText(placeVicinity);
        }

    }
}
