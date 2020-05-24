package com.alon.client.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.alon.client.GardenDetailsActivity;
import com.alon.client.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Element> mDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView element_LBL_name, element_LBL_location, element_LBL_active;
        public Button element_BTN_details;
        private Context context;

        public MyViewHolder(View v) {
            super(v);
            context = v.getContext();
            element_LBL_name = v.findViewById(R.id.element_row_LBL_name);
            element_LBL_location = v.findViewById(R.id.element_row_LBL_location);
            element_LBL_active = v.findViewById(R.id.element_row_LBL_active);
            element_BTN_details = v.findViewById(R.id.element_row_BTN_details);
            element_BTN_details.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.element_row_BTN_details:
                    startGardenDetailsActivity();
                    break;
            }
        }

        // Method that start the garden details activity.
        private void startGardenDetailsActivity() {
            Intent intent = new Intent(context, GardenDetailsActivity.class);
            context.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(ArrayList<Element> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_row, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.element_LBL_name.setText(mDataset.get(position).getName());
        holder.element_LBL_location.setText(mDataset.get(position).getLocation().getLat() + ", " + mDataset.get(position).getLocation().getLng());
        holder.element_LBL_active.setText(mDataset.get(position).getActive().toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
