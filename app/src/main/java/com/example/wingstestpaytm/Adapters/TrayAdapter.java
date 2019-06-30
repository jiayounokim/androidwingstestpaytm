package com.example.wingstestpaytm.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wingstestpaytm.Objects.Tray;
import com.example.wingstestpaytm.R;

import java.util.List;

public class TrayAdapter extends RecyclerView.Adapter<TrayAdapter.TrayViewHolder> {

    private List<Tray> trayModelList;
    private Activity activity;

    public TrayAdapter(List<Tray> trayModelList, Activity activity) {
        this.trayModelList = trayModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TrayAdapter.TrayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_tray, viewGroup, false);

        return new TrayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrayAdapter.TrayViewHolder trayViewHolder, int position) {
        String mealNameText = trayModelList.get(position).getMealName();
        final String mealQuantityText = String.valueOf(trayModelList.get(position).getMealQuantity());
        String mealSubtotalText = String.valueOf(trayModelList.get(position).getMealPrice() * trayModelList.get(position).getMealQuantity());

        trayViewHolder.setData(mealNameText, mealQuantityText, mealSubtotalText);
        trayViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, " Clicked on: " + mealQuantityText, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return trayModelList.size();
    }

    public class TrayViewHolder extends RecyclerView.ViewHolder {
        private TextView mealName;
        private TextView mealQuantity;
        private TextView mealSubTotal;

        public TrayViewHolder(@NonNull View itemView) {
            super(itemView);

            mealName = (TextView) itemView.findViewById(R.id.tray_meal_name);

            mealQuantity = (TextView) itemView.findViewById(R.id.tray_meal_quantity);

            mealSubTotal = (TextView) itemView.findViewById(R.id.tray_meal_subtotal);

        }

        private void setData(String mealNameText, String mealQuantityText, String mealSubtotalText) {
            mealName.setText(mealNameText);
            mealQuantity.setText(mealQuantityText);
            mealSubTotal.setText(mealSubtotalText);

        }
    }


//    private Activity activity;
//    private ArrayList<Tray> trayList;
//
//    public TrayAdapter(Activity activity, ArrayList<Tray> trayList) {
//        this.activity = activity;
//        this.trayList = trayList;
//    }
//
//    @Override
//    public int getCount() {
//        return trayList.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return trayList.get(i);
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
//            view = LayoutInflater.from(activity).inflate(R.layout.list_item_tray, null);
//        }
//
//        TextView mealName = (TextView) view.findViewById(R.id.tray_meal_name);
//        TextView mealQuantity = (TextView) view.findViewById(R.id.tray_meal_quantity);
//        TextView mealSubTotal = (TextView) view.findViewById(R.id.tray_meal_subtotal);
//
//        Tray tray = trayList.get(i);
//        mealName.setText(tray.getMealName());
//        mealQuantity.setText(tray.getMealQuantity() + "");
//        mealSubTotal.setText("$" + (tray.getMealPrice() * tray.getMealQuantity()));
//
//        return view;
//    }
}
