package com.pradumnkmahanta.nearbyplaces;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pradumnkmahanta.nearbyplaces.utilities.PlaceItem;

import java.util.List;

/**
 * Created by Pradumn K Mahanta on 02-02-2017.
 **/

public class ActivityNavigationDrawerAdapter extends RecyclerView.Adapter<ActivityNavigationDrawerAdapter.ViewHolder> {
    private final List<PlaceItem> mValues;

    public ActivityNavigationDrawerAdapter(List<PlaceItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_placeitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.placeName.setText(holder.mItem.placeName);
        holder.placeAddress.setText(holder.mItem.placeVicinity);
        holder.placeDistance.setText(holder.mItem.placeDistance);
        holder.placeType.setText(holder.mItem.placeType);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView placeName;
        public final TextView placeAddress;
        public final TextView placeType;
        public final TextView placeDistance;
        public PlaceItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            placeName = (TextView) view.findViewById(R.id.placeName);
            placeAddress = (TextView) view.findViewById(R.id.placeAddress);
            placeType = (TextView) view.findViewById(R.id.placeType);
            placeDistance = (TextView) view.findViewById(R.id.placeDistance);
        }

    }
}
