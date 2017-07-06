package com.pradumnkmahanta.nearbyplaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.pradumnkmahanta.nearbyplaces.utilities.AlertDialogManager;
import com.pradumnkmahanta.nearbyplaces.utilities.PlaceItem;

import java.util.List;

public class ActivityNavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AlertDialogManager alertDialogManager;

    //PLACES DETAILS
    public static List<PlaceItem> mItems;
    String latitutde, longitude;

    RecyclerView recyclerView;


    //TAGS
    public static String KEY_REFERENCE = "Reference";
    public static String KEY_NAME = "Name";
    public static String KEY_ADDRESS = "Address";
    public static String KEY_TYPE = "Type";
    public static String KEY_PHOTOREFERENCE = "PhotoReference";
    public static String KEY_DISTANCE = "Distance";
    public static String KEY_LATITUDE = "Latitude";
    public static String KEY_LONGITUDE = "Longitude";
    public static String KEY_RATING = "rating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        alertDialogManager = new AlertDialogManager();

        //Navigation Drawer and Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Intent Data
        latitutde = getIntent().getStringExtra("LATITUDE");
        longitude = getIntent().getStringExtra("LONGITUDE");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlaceItem placeItem = mItems.get(position);

                Toast.makeText(getApplicationContext(), placeItem.placeName + " is selected!", Toast.LENGTH_SHORT).show();
                Intent selectedPlace = new Intent(getApplicationContext(), ActivityPlaceDetails.class);
                selectedPlace.putExtra(KEY_NAME, placeItem.placeName);
                selectedPlace.putExtra(KEY_ADDRESS, placeItem.placeVicinity);
                selectedPlace.putExtra(KEY_PHOTOREFERENCE, placeItem.placePhotoRef);
                selectedPlace.putExtra(KEY_LATITUDE, placeItem.placeLat);
                selectedPlace.putExtra(KEY_LONGITUDE, placeItem.placeLng);
                selectedPlace.putExtra(KEY_RATING, placeItem.placerating);
                startActivity(selectedPlace);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //Loading Places
        new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "All");

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ActivityNavigationDrawer.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ActivityNavigationDrawer.ClickListener clickListener) {
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.locate_user) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Handling Navigation Drawer Clicks Here
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.all_places) {
            new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "All");
        } else if (id == R.id.category_restaurant) {
            new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "restaurant");
        }else if (id == R.id.category_cafe) {
            new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "cafe");
        }else if (id == R.id.category_busstation) {
            new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "bus_station");
        }else if (id == R.id.category_bank) {
            new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "bank");
        }else if (id == R.id.category_atm) {
            new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "atm");
        }else if (id == R.id.category_hospitals) {
            new AsyncTaskFetchPlaces(this, recyclerView).execute(latitutde, longitude, ""+1000, "hospital");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
