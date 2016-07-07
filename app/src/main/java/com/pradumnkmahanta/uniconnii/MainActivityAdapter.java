package com.pradumnkmahanta.uniconnii;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    private final List<PlaceItem> mValues;

    public MainActivityAdapter(List<PlaceItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.lbl_name.setText(holder.mItem.placeName);
        holder.lbl_address.setText(holder.mItem.placeVicinity);
        holder.lbl_distance.setText(holder.mItem.placeDistance);
        holder.lbl_type.setText(holder.mItem.placeType);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView lbl_name;
        public final TextView lbl_address;
        public final TextView lbl_type;
        public final TextView lbl_distance;
        public PlaceItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            lbl_name = (TextView) view.findViewById(R.id.name);
            lbl_address = (TextView) view.findViewById(R.id.address);
            lbl_type = (TextView) view.findViewById(R.id.type);
            lbl_distance = (TextView) view.findViewById(R.id.distance);
        }

    }

}