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

import com.example.wingstestpaytm.Activities.MealDetailActivity;
import com.example.wingstestpaytm.Objects.MealModel;
import com.example.wingstestpaytm.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<MealModel> mealModelList;
    private Activity activity;
    private String restaurantId;

    public MealAdapter(List<MealModel> mealModelList, Activity activity, String restaurantId) {
        this.mealModelList = mealModelList;
        this.activity = activity;
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_meal, viewGroup, false);

        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealAdapter.MealViewHolder mealViewHolder, final int position) {

        String mealNameText = mealModelList.get(position).getName();
          String mealDescText = mealModelList.get(position).getShort_description();
        String mealPriceText = String.valueOf(mealModelList.get(position).getPrice());
          String mealLogoImage = mealModelList.get(position).getImage();

        mealViewHolder.setData(mealNameText, mealDescText, mealPriceText,mealLogoImage);
        mealViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MealDetailActivity.class);
                intent.putExtra("restaurantId", restaurantId);
                intent.putExtra("mealId", mealModelList.get(position).getId());
                intent.putExtra("mealName", mealModelList.get(position).getName());
                intent.putExtra("mealDescription", mealModelList.get(position).getShort_description());
                intent.putExtra("mealPrice", mealModelList.get(position).getPrice());
                intent.putExtra("mealImage", mealModelList.get(position).getImage());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealModelList.size();
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {

        TextView mealName;
        TextView mealDesc;
        TextView mealPrice;
        ImageView mealImage;


        public MealViewHolder(@NonNull View view) {
            super(view);

            mealName = (TextView) view.findViewById(R.id.meal_name);
            mealDesc = (TextView) view.findViewById(R.id.meal_desc);
            mealPrice = (TextView) view.findViewById(R.id.meal_price);
            mealImage = (ImageView) view.findViewById(R.id.meal_image);
        }

        private void setData(String mealNameText, String mealDescText, String mealPriceText, String mealLogoImage) {

            mealName.setText(mealNameText);
            mealDesc.setText(mealDescText);
            mealPrice.setText(mealPriceText);
            Picasso.with(activity.getApplicationContext()).load(mealLogoImage).fit().centerInside().into(mealImage);

        }
    }


//    private Activity activity;
//    private ArrayList<MealModel> mealModelList;
//    private String restaurantId;
//
//    public MealAdapter(Activity activity, ArrayList<MealModel> mealModelList, String restaurantId) {
//        this.activity = activity;
//        this.mealModelList = mealModelList;
//        this.restaurantId = restaurantId;
//    }
//
//    @Override
//    public int getCount() {
//        return mealModelList.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return mealModelList.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//
//        if (view == null) {
//            view = LayoutInflater.from(activity).inflate(R.layout.list_item_meal, null);
//        }
//
//        final MealModel mealModel = mealModelList.get(i);
//
//        TextView mealName = (TextView) view.findViewById(R.id.meal_name);
//        TextView mealDesc = (TextView) view.findViewById(R.id.meal_desc);
//        TextView mealPrice = (TextView) view.findViewById(R.id.meal_price);
//        ImageView mealImage = (ImageView) view.findViewById(R.id.meal_image);
//
//        mealName.setText(mealModel.getName());
//        mealDesc.setText(mealModel.getShort_description());
//        mealPrice.setText("$" + mealModel.getPrice());
//        Picasso.with(activity.getApplicationContext()).load(mealModel.getImage()).fit().centerInside().into(mealImage);
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, MealDetailActivity.class);
//                intent.putExtra("restaurantId", restaurantId);
//                intent.putExtra("mealId", mealModel.getId());
//                intent.putExtra("mealName", mealModel.getName());
//                intent.putExtra("mealDescription", mealModel.getShort_description());
//                intent.putExtra("mealPrice", mealModel.getPrice());
//                intent.putExtra("mealImage", mealModel.getImage());
//                activity.startActivity(intent);
//            }
//        });
//
//        return view;
//    }
}