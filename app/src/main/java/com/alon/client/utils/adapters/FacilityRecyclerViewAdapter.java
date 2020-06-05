package com.alon.client.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alon.client.FacilityDetailsActivity;
import com.alon.client.R;
import com.alon.client.utils.Converter;
import com.alon.client.utils.elementUtils.Element;
import com.alon.client.utils.elementUtils.FacilityStatus;
import com.alon.client.utils.elementUtils.FacilityType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FacilityRecyclerViewAdapter extends RecyclerView.Adapter<FacilityRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Element> mDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Element facility;
        public TextView facility_LBL_name, facility_LBL_address, facility_LBL_type, facility_LBL_status;
        public Button facility_BTN_details;
        private Context context;

        public MyViewHolder(View v) {
            super(v);
            context = v.getContext();
            facility_LBL_name = v.findViewById(R.id.facility_row_LBL_name);
            facility_LBL_address = v.findViewById(R.id.facility_row_LBL_address);
            facility_LBL_type = v.findViewById(R.id.facility_row_LBL_type);
            facility_LBL_status = v.findViewById(R.id.facility_row_LBL_status);
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
            intent.putExtra("id", facility.getId());
            intent.putExtra("name", facility.getName());
            intent.putExtra("location", facility.getLocationUtil().getLat() + ", " + facility.getLocationUtil().getLng());
            intent.putExtra("active", facility.getActive());
            intent.putExtra("description", facility.getElementAttributes().get("description").toString());
            intent.putExtra("type", facility.getElementAttributes().get("type").toString());
            intent.putExtra("status", facility.getElementAttributes().get("status").toString());
            intent.putExtra("mus_group", facility.getElementAttributes().get("mus_group").toString());
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
        String address = convertToAddress(holder.context, mDataset.get(position).getLocationUtil().getLat(), mDataset.get(position).getLocationUtil().getLng());
        if(address != null) {
            holder.facility_LBL_address.setText(address);
        } else {
            holder.facility_LBL_address.setText("Address not available");
        }
        holder.facility_LBL_type.setText(Converter.convertFacilityType(FacilityType.valueOf(mDataset.get(position).getElementAttributes().get("type").toString())));
        holder.facility_LBL_status.setText(Converter.convertFacilityStatus(FacilityStatus.valueOf(mDataset.get(position).getElementAttributes().get("status").toString())));
        holder.facility = mDataset.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Method that convert location to address.
    private String convertToAddress(Context context, double lat, double lng){
        String address = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(!addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return address;
    }
}
