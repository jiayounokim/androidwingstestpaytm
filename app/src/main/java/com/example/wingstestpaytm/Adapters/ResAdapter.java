package com.example.wingstestpaytm.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wingstestpaytm.Activities.MealListActivity;
import com.example.wingstestpaytm.JsonModelObject.Registration;
import com.example.wingstestpaytm.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResAdapter extends RecyclerView.Adapter<ResAdapter.MyViewHolder> {

    private List<Registration> restaurantModelList;
    private Activity activity;

    public ResAdapter(List<Registration> restaurantModelList, Activity activity) {
        this.restaurantModelList = restaurantModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_restaurant, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        String resLogoImage1 = restaurantModelList.get(position).getLogo();
        final String resNameText1 = restaurantModelList.get(position).getName();
        String resAddressText1 = restaurantModelList.get(position).getAddress();
        final String resIdText1 = String.valueOf(restaurantModelList.get(position).getId());

        myViewHolder.setData(resNameText1, resAddressText1, resLogoImage1);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MealListActivity.class);
                intent.putExtra("restaurantId", resIdText1);
                intent.putExtra("restaurantName", resNameText1);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView resName;
        private TextView resAddress;
        private ImageView resLogo;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            resName = itemView.findViewById(R.id.res_name);
            resAddress = itemView.findViewById(R.id.res_address);
            resLogo = itemView.findViewById(R.id.res_logo);
        }

        private void setData(String resNameText, String resAddressText, String resLogoImage) {
            resName.setText(resNameText);
            resAddress.setText(resAddressText);
            Picasso.with(activity.getApplicationContext()).load(resLogoImage).fit().centerInside().into(resLogo);

        }

    }


}
