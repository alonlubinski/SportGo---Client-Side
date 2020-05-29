package com.alon.client.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alon.client.FacilityDetailsActivity;
import com.alon.client.GardenDetailsActivity;
import com.alon.client.R;

import java.util.ArrayList;

public class FacilityRecyclerViewAdapter extends RecyclerView.Adapter<FacilityRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Element> mDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Element facility;
        public TextView facility_LBL_name, facility_LBL_garden, facility_LBL_location, facility_LBL_active;
        public Button facility_BTN_details;
        private Context context;

        public MyViewHolder(View v) {
            super(v);
            context = v.getContext();
            facility_LBL_name = v.findViewById(R.id.facility_row_LBL_name);
            facility_LBL_garden = v.findViewById(R.id.facility_row_LBL_garden);
            facility_LBL_location = v.findViewById(R.id.facility_row_LBL_location);
            facility_LBL_active = v.findViewById(R.id.facility_row_LBL_active);
            facility_BTN_details = v.findViewById(R.id.facility_row_BTN_details);
            facility_BTN_details.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.facility_row_BTN_details:
                    startFacilityDetailsActivity();
                    break;
            }
        }

        // Method that start the facility details activity.
        private void startFacilityDetailsActivity() {
            Intent intent = new Intent(context, FacilityDetailsActivity.class);
            intent.putExtra("name", facility.getName());
            intent.putExtra("location", facility.getLocationUtil().getLat() + ", " + facility.getLocationUtil().getLng());
            intent.putExtra("active", facility.getActive());
            context.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FacilityRecyclerViewAdapter(ArrayList<Element> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FacilityRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_row, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FacilityRecyclerViewAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.facility_LBL_name.setText(mDataset.get(position).getName());
        holder.facility_LBL_location.setText(mDataset.get(position).getLocationUtil().getLat() + ", " + mDataset.get(position).getLocationUtil().getLng());
        holder.facility_LBL_active.setText(mDataset.get(position).getActive().toString());
        holder.facility = mDataset.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
