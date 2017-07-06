package com.pradumnkmahanta.nearbyplaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.pradumnkmahanta.nearbyplaces.utilities.AlertDialogManager;
import com.pradumnkmahanta.nearbyplaces.utilities.ApplicationData;
import com.pradumnkmahanta.nearbyplaces.utilities.BitmapModel;
import com.pradumnkmahanta.nearbyplaces.utilities.JSONParser;
import com.pradumnkmahanta.nearbyplaces.utilities.PlaceItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pradumn K Mahanta on 02-02-2017.
 **/

class AsyncTaskFetchPlaces extends AsyncTask<String, String, String> {

    private Context activityContext;
    private JSONParser jsonParser = new JSONParser();
    private JSONParser jsonParserDistance = new JSONParser();
    private ProgressDialog progressDialog;
    private AlertDialogManager alert = new AlertDialogManager();
    private PlaceItem mItems;
    private BitmapModel mBitMap;


    ActivityNavigationDrawerAdapter activityNavigationDrawerAdapter;
    private RecyclerView recyclerView;
    private String placeName, placeVicinity, placeType, placeDistance, placeLat, placeLng, rating;

    private ArrayList<String> ArraylistPlaceName = new ArrayList<>();
    private ArrayList<String> ArraylistPlaceVicinity = new ArrayList<>();
    private ArrayList<String> ArraylistPlaceType = new ArrayList<>();
    private ArrayList<String> ArraylistPlaceDistance = new ArrayList<>();
    private ArrayList<String> ArraylistPlaceLat = new ArrayList<>();
    private ArrayList<String> ArraylistPlaceLng = new ArrayList<>();
    private ArrayList<String> ArraylistRating = new ArrayList<>();

    private ArrayList<Bitmap> ArraylistBitMap = new ArrayList<>();

    private String placePhotoRef;
    private String[] arrPhoto;
    private int arrsize;


    public AsyncTaskFetchPlaces(Context activityContext, RecyclerView recyclerView) {
        this.activityContext = activityContext;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activityContext);
        progressDialog.setMessage("Loading Places");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected String doInBackground(String... args) {

        HashMap<String, String> params = new HashMap<>();
        params.put("location", args[0] + "," + args[1]);
        params.put("key", ApplicationData.ServerAPI);
        params.put("radius", args[2]);
        if (args[3].equals("ALL")) {
            Log.i("Places API Request", "Searching for all places.");
        } else {
            params.put("type", args[3]);
        }
        JSONObject jsonObject = jsonParser.makeHttpRequest(ApplicationData.PLACES_SEARCH_URL, "GET", params);
        Log.d("Result", jsonObject.toString());

        try {
            if (jsonObject.getString("status").equals("OK")) {
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                Log.d("resultsArray", resultsArray.toString());
                arrPhoto = new String[resultsArray.length()];

                if (resultsArray.length() > 0) {
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject jObj = resultsArray.getJSONObject(i);
                        Log.d("resultsObject: " + i, jObj.toString());
                        //LAT AND LNG
                        String geometry = jObj.getString("geometry");
                        JSONObject geometryObject = new JSONObject(geometry);
                        String location = geometryObject.getString("location");
                        JSONObject locationObject = new JSONObject(location);
                        placeLat = locationObject.getString("lat");
                        placeLng = locationObject.getString("lng");
                        Log.d("Location", placeLat + ", " + placeLng);

                        placeName = jObj.getString("name");
                        ArraylistPlaceName.add(placeName);


                        Log.d("Name", placeName);


                        rating = jObj.optString("rating");
                        Log.e("rating", rating);
                        ArraylistRating.add(rating);

                        placeType = jObj.getString("types");
                        Log.d("Type", placeType);
                        ArraylistPlaceType.add(placeType);


                        placeVicinity = jObj.getString("vicinity");
                        Log.d("Address", placeVicinity);
                        ArraylistPlaceVicinity.add(placeVicinity);

                        HashMap<String, String> par = new HashMap<>();
                        par.put("origins", args[0] + "," + args[1]);
                        //par.put("origins", "40.6655101,-73.89188969999998");
//                        par.put("destinations", placeLat + "," + placeLng);//TODO:Change

//                        par.put("destinations", placeLat + "," + placeLng);//TODO:Change

                        par.put("destinations", ArraylistPlaceLat.add(placeLat) + "," + ArraylistPlaceLng.add(placeLng));//TODO:Change

                        //par.put("destinations", "41.43206,-81.38992");
                        par.put("key", ApplicationData.ServerAPI);
                        par.put("units", "metric");

                        JSONObject jsonObjectDistance = jsonParserDistance.makeHttpRequest(ApplicationData.PLACES_DISTANCE_URL, "GET", par);
                        JSONArray distanceArray = jsonObjectDistance.getJSONArray("rows");
                        JSONObject distanceObject = distanceArray.getJSONObject(0);
                        JSONArray distanceElement = distanceObject.getJSONArray("elements");
                        JSONObject elementObject = distanceElement.getJSONObject(0);

                        if (elementObject.getString("status").equals("OK")) {
                            String distance = elementObject.getString("distance");
                            JSONObject finalObject = new JSONObject(distance);
                            placeDistance = finalObject.getString("text");
                        } else {
                            placeDistance = "NA";
                        }
                        ArraylistPlaceDistance.add(placeDistance);

                        Log.d("Distance", placeDistance);

                        boolean hasPhoto;
                        try {
                            JSONArray photoArray = jObj.getJSONArray("photos");
                            Log.d("Photo Array", photoArray.toString());
                            JSONObject photoObject = photoArray.getJSONObject(0);
                            placePhotoRef = photoObject.getString("photo_reference");
                            hasPhoto = true;
                        } catch (Exception e) {
                            hasPhoto = false;
                        }

                        if (hasPhoto) {
                            JSONArray photoArray = jObj.getJSONArray("photos");
                            Log.d("Photo Array", photoArray.toString());
                            JSONObject photoObject = photoArray.getJSONObject(0);
                            placePhotoRef = photoObject.getString("photo_reference");
                        } else {
                            placePhotoRef = "Not Available";
                        }

                        final String ref = placePhotoRef;
                        arrPhoto[i] = placePhotoRef;

//                        mItems.add(new PlaceItem(placeName, placeVicinity, placeType, placeDistance, placeLat, placeLng, placePhotoRef, rating));


                        Log.d("Photo Ref", placePhotoRef);


//                        Thread background = new Thread(new Runnable() {
//
//                            // After call for background.start this run method call
//                            public void run() {
//                                try {
//
//                                    LoadImage loadImage = new LoadImage();
//                                    loadImage.execute(ref);
//
//                                } catch (Throwable t) {
//                                    // just end the background thread
//                                    Log.i("Animation", "Thread  exception " + t);
//                                }
//                            }
//                        });
//
//                        background.start();

                    }
                }
            } else if (jsonObject.getString("status").equals("ZERO_RESULTS")) {
                alert.showAlertDialog(activityContext, "Near Places", "Sorry no places found.", false);
            }
        } catch (JSONException je) {

        }
        return "";
    }

    protected void onPostExecute(String file_url) {

//        if (progressDialog.isShowing()) {


        for (int i = 0; i < arrPhoto.length; i++) {
            LoadImage loadImage = new LoadImage();
            loadImage.execute(arrPhoto[i]);


        }

//            activityNavigationDrawerAdapter = new ActivityNavigationDrawerAdapter(mItems);
//            LinearLayoutManager mLayoutManager = new LinearLayoutManager(activityContext);
//            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.setAdapter(activityNavigationDrawerAdapter);
//            activityNavigationDrawerAdapter.notifyDataSetChanged();
//            progressDialog.hide();

//        }

    }


    class LoadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(ApplicationData.PLACES_IMAGE_URL + "&key=" + ApplicationData.ServerAPI + "&photoreference=" + params[0]).openStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException mue) {
                return bitmap;
            } catch (IOException ioe) {
                return bitmap;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmapImage) {
//            if (bitmapImage != null) {
//                imageView.setImageBitmap(bitmapImage);

            mItems = new PlaceItem(ArraylistPlaceName, ArraylistPlaceVicinity, ArraylistPlaceType, ArraylistPlaceDistance, ArraylistPlaceLat, ArraylistPlaceLng, ArraylistRating, ArraylistRating);
//            mItems.add(new PlaceItem(placeName, placeVicinity, placeType, placeDistance, placeLat, placeLng, placePhotoRef, rating, bitmapImage));

            ArraylistBitMap.add(bitmapImage);
            mBitMap = new BitmapModel(ArraylistBitMap);

            arrsize = ArraylistBitMap.size();


            if (arrPhoto.length == arrsize) {

                if (progressDialog.isShowing()) {
                    activityNavigationDrawerAdapter = new ActivityNavigationDrawerAdapter(mItems, mBitMap);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(activityContext);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(activityNavigationDrawerAdapter);
                    activityNavigationDrawerAdapter.notifyDataSetChanged();

                }
                progressDialog.hide();

            }

        }


//            else {
////
////                Toast.makeText(ge, "", Toast.LENGTH_SHORT).show();
////     alert.showAlertDialog(getApplicationContext(), "Place Image", "Sorry failed to retrieve Image.", false);
//            }
//        }

    }
}