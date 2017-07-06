package com.pradumnkmahanta.nearbyplaces.utilities;

import java.util.ArrayList;

/**
 * Created by Pradumn K Mahanta on 03-02-2017.
 **/

public class PlaceItem {
    public final ArrayList<String> placeName;
    public final ArrayList<String> placeVicinity;
    public final ArrayList<String> placeType;
    public final ArrayList<String> placeDistance;
    public final ArrayList<String> placeLat;
    public final ArrayList<String> placeLng;
    public final ArrayList<String> placePhotoRef;
    public final ArrayList<String> placerating;

    public PlaceItem(ArrayList<String> placeName, ArrayList<String> placeVicinity, ArrayList<String> placeType, ArrayList<String> placeDistance, ArrayList<String> placeLat, ArrayList<String> placeLng, ArrayList<String> placePhotoRef, ArrayList<String> placerating) {
        this.placeName = placeName;
        this.placeVicinity = placeVicinity;
        this.placeType = placeType;
        this.placeDistance = placeDistance;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
        this.placePhotoRef = placePhotoRef;
        this.placerating = placerating;
    }



}
