package com.pradumnkmahanta.uniconnii;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    AlertDialogManager alert;

    private List<PlaceItem> mItems;

    GPSManager userCoordinates;

    ProgressDialog pDialog;

    RecyclerView recyclerView;
    MainActivityAdapter mainActivityAdapter;

    String latitude;
    String longitude;

    public static String KEY_REFERENCE = "Reference";
    public static String KEY_NAME = "Name";
    public static String KEY_ADDRESS = "Address";
    public static String KEY_TYPE = "Type";
    public static String KEY_PHOTOREFERENCE = "PhotoReference";
    public static String KEY_DISTANCE = "Distance";
    public static String KEY_LATITUDE = "Latitude";
    public static String KEY_LONGITUDE = "Longitude";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("UniConn - Nearby Places");

        alert = new AlertDialogManager();
        userCoordinates = new GPSManager(this);

        if (userCoordinates.canGetLocation()) {
            Log.d("Your Location", "latitude:" + userCoordinates.getLatitude() + ", longitude: " + userCoordinates.getLongitude());
        } else {
            alert.showAlertDialog(MainActivity.this, "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            return;
        }

        mItems = new ArrayList<PlaceItem>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlaceItem placeItem = mItems.get(position);
                Toast.makeText(getApplicationContext(), placeItem.placeName + " is selected!", Toast.LENGTH_SHORT).show();
                Intent selectedPlace = new Intent(MainActivity.this, SelectedPlaceDetails.class);
                selectedPlace.putExtra(KEY_NAME, placeItem.placeName);
                selectedPlace.putExtra(KEY_ADDRESS, placeItem.placeVicinity);
                selectedPlace.putExtra(KEY_PHOTOREFERENCE, placeItem.placePhotoRef);
                selectedPlace.putExtra(KEY_LATITUDE, placeItem.placeLat);
                selectedPlace.putExtra(KEY_LONGITUDE, placeItem.placeLng);
                startActivity(selectedPlace);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        latitude = Double.toString(userCoordinates.getLatitude());
        longitude = Double.toString(userCoordinates.getLongitude());
        new LoadPlaces().execute(latitude, longitude);

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:
                new LoadPlaces().execute(latitude, longitude);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class LoadPlaces extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        JSONParser jsonParserDistance = new JSONParser();
        String placeName, placeVicinity, placeType, placeDistance, placeLat, placeLng, placePhotoRef;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(Html.fromHtml("Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<>();
            params.put("location", args[0]+","+args[1]);//TODO:CHANGE
            //params.put("location", "40.6655101,-73.89188969999998");
            params.put("key", AppData.ServerAPI);
            params.put("radius", "1000");
            JSONObject jsonObject = jsonParser.makeHttpRequest(AppData.PLACES_SEARCH_URL, "GET", params);
            try{
                if(jsonObject.getString("status").equals("OK")){
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    Log.d("resultsArray", resultsArray.toString());
                    if(resultsArray.length()>0){
                        for(int i = 0; i < resultsArray.length(); i++){
                            JSONObject jObj = resultsArray.getJSONObject(i);
                            Log.d("resultsObject: " + i, jObj.toString());
                            //LAT AND LNG
                            String geometry = jObj.getString("geometry");
                            JSONObject geometryObject = new JSONObject(geometry);
                            String location = geometryObject.getString("location");
                            JSONObject locationObject = new JSONObject(location);
                            placeLat = locationObject.getString("lat");
                            placeLng  = locationObject.getString("lng");
                            Log.d("Location", placeLat + ", " + placeLng);


                            placeName = jObj.getString("name");
                            Log.d("Name", placeName);

                            placeType = jObj.getString("types");
                            Log.d("Type", placeType);

                            placeVicinity = jObj.getString("vicinity");
                            Log.d("Address", placeVicinity);

                            HashMap<String, String> par = new HashMap<>();
                            par.put("origins", args[0]+","+args[1]);
                            //par.put("origins", "40.6655101,-73.89188969999998");
                            par.put("destinations", placeLat+","+placeLng);//TODO:Change
                            //par.put("destinations", "41.43206,-81.38992");
                            par.put("key", AppData.ServerAPI);
                            par.put("units", "metric");

                            JSONObject jsonObjectDistance = jsonParserDistance.makeHttpRequest(AppData.PLACES_DISTANCE_URL, "GET", par);
                            JSONArray distanceArray = jsonObjectDistance.getJSONArray("rows");
                            JSONObject distanceObject = distanceArray.getJSONObject(0);
                            JSONArray distanceElement = distanceObject.getJSONArray("elements");
                            JSONObject elementObject = distanceElement.getJSONObject(0);

                            if(elementObject.getString("status").equals("OK")){
                                String distance = elementObject.getString("distance");
                                JSONObject finalObject = new JSONObject(distance);
                                placeDistance = finalObject.getString("text");
                            }else{
                                placeDistance = "NA";
                            }
                            Log.d("Distance", placeDistance);

                            boolean hasPhoto;
                            try{
                                JSONArray photoArray = jObj.getJSONArray("photos");
                                Log.d("Photo Array",photoArray.toString());
                                JSONObject photoObject = photoArray.getJSONObject(0);
                                placePhotoRef = photoObject.getString("photo_reference");
                                hasPhoto = true;
                            }catch(Exception e){
                                hasPhoto = false;
                            }

                            if(hasPhoto){
                                JSONArray photoArray = jObj.getJSONArray("photos");
                                Log.d("Photo Array",photoArray.toString());
                                JSONObject photoObject = photoArray.getJSONObject(0);
                                placePhotoRef = photoObject.getString("photo_reference");
                            }else{
                                placePhotoRef = "Not Available";
                            }

                            Log.d("Photo Ref", placePhotoRef);

                            mItems.add(new PlaceItem(placeName, placeVicinity, placeType, placeDistance, placeLat, placeLng, placePhotoRef));

                        }
                    }
                }else if(jsonObject.getString("status").equals("ZERO_RESULTS")){
                    alert.showAlertDialog(MainActivity.this, "Near Places", "Sorry no places found.", false);
                }
            }catch(JSONException je){

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            Log.d("Items", mItems.toString());
            pDialog.dismiss();

            mainActivityAdapter = new MainActivityAdapter(mItems);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mainActivityAdapter);
            mainActivityAdapter.notifyDataSetChanged();
        }
    }
}