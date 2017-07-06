package com.pradumnkmahanta.nearbyplaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pradumnkmahanta.nearbyplaces.utilities.BitmapModel;
import com.pradumnkmahanta.nearbyplaces.utilities.PlaceItem;

/**
 *
 **/

public class ActivityNavigationDrawerAdapter extends RecyclerView.Adapter<ActivityNavigationDrawerAdapter.ViewHolder> {
    private final PlaceItem mValues;
    private final BitmapModel bitmaValues;
    ViewHolder viewHolder;
    Context context;

    public ActivityNavigationDrawerAdapter(PlaceItem items, BitmapModel bitmapModel) {
        this.context = context;
        mValues = items;
        bitmaValues=bitmapModel;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_placeitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        viewHolder = holder;

//        holder.mItem = mValues.get(position);
        holder.placeName.setText(mValues.placeName.get(position));
        holder.placeAddress.setText(mValues.placeVicinity.get(position));
        holder.placeDistance.setText(mValues.placeDistance.get(position));
        holder.placeType.setText(mValues.placeType.get(position));
        holder.placeRating.setText(mValues.placerating.get(position));
        holder.imageView.setImageBitmap(bitmaValues.photoBitmap.get(position));

    }

    @Override
    public int getItemCount() {
        return mValues.placeName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView placeName;
        public final TextView placeAddress;
        public final TextView placeType;
        public final TextView placeDistance;
        public final TextView placeRating;
        public PlaceItem mItem;
        public ImageView imageView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            placeName = (TextView) view.findViewById(R.id.placeName);
            placeAddress = (TextView) view.findViewById(R.id.placeAddress);
            placeType = (TextView) view.findViewById(R.id.placeType);
            placeDistance = (TextView) view.findViewById(R.id.placeDistance);
            placeRating = (TextView) view.findViewById(R.id.placeRating);
            imageView = (ImageView) view.findViewById(R.id.imageView2);
        }

    }
}
