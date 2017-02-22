package com.pradumnkmahanta.nearbyplaces;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.pradumnkmahanta.nearbyplaces.utilities.AlertDialogManager;
import com.pradumnkmahanta.nearbyplaces.utilities.JSONParser;
import com.pradumnkmahanta.nearbyplaces.utilities.ApplicationData;
import com.pradumnkmahanta.nearbyplaces.utilities.PlaceItem;

/**
 * Created by Pradumn K Mahanta on 02-02-2017.
 **/

class AsyncTaskFetchPlaces extends AsyncTask<String, String, String> {

    private Context activityContext;
    private JSONParser jsonParser = new JSONParser();
    private JSONParser jsonParserDistance = new JSONParser();
    private ProgressDialog progressDialog;
    private AlertDialogManager alert = new AlertDialogManager();
    private List<PlaceItem> mItems = new ArrayList<PlaceItem>();

    ActivityNavigationDrawerAdapter activityNavigationDrawerAdapter;
    private RecyclerView recyclerView;

    public AsyncTaskFetchPlaces(Context activityContext, RecyclerView recyclerView){
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
        String placeName, placeVicinity, placeType, placeDistance, placeLat, placeLng, placePhotoRef;
        HashMap<String, String> params = new HashMap<>();
        params.put("location", args[0]+","+args[1]);
        params.put("key", ApplicationData.ServerAPI);
        params.put("radius", args[2]);
        if(args[3].equals("ALL")){
            Log.i("Places API Request","Searching for all places.");
        }else{
            params.put("type", args[3]);
        }
        JSONObject jsonObject = jsonParser.makeHttpRequest(ApplicationData.PLACES_SEARCH_URL, "GET", params);
        Log.d("Result", jsonObject.toString());

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
                        par.put("key", ApplicationData.ServerAPI);
                        par.put("units", "metric");

                        JSONObject jsonObjectDistance = jsonParserDistance.makeHttpRequest(ApplicationData.PLACES_DISTANCE_URL, "GET", par);
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
                alert.showAlertDialog(activityContext, "Near Places", "Sorry no places found.", false);
            }
        }catch(JSONException je){

        }
        return "";
    }

    protected void onPostExecute(String file_url) {
        if(progressDialog.isShowing()){
            ActivityNavigationDrawer.mItems = mItems;
            activityNavigationDrawerAdapter = new ActivityNavigationDrawerAdapter(mItems);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(activityContext);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(activityNavigationDrawerAdapter);
            activityNavigationDrawerAdapter.notifyDataSetChanged();
            progressDialog.hide();
        }
    }
}