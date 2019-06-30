package com.example.wingstestpaytm.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wingstestpaytm.Activities.DriverMainActivity;
import com.example.wingstestpaytm.Fragments.DeliveryFragment;
import com.example.wingstestpaytm.Objects.OrderModel;
import com.example.wingstestpaytm.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderModel> orderModelList;
    private Activity activity;
    private RequestQueue queue;

    public OrderAdapter(List<OrderModel> orderModelList, Activity activity) {
        this.orderModelList = orderModelList;
        this.activity = activity;
    }

    //    private Activity activity;
//    private ArrayList<OrderModel> orderModelList;
//    private RequestQueue queue;
//
//    public OrderAdapter(Activity activity, ArrayList<OrderModel> orderModelList) {
//        this.activity = activity;
//        this.orderModelList = orderModelList;
//    }
//
//    @Override
//    public int getCount() {
//        return orderModelList.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return orderModelList.get(i);
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
//            view = LayoutInflater.from(activity).inflate(R.layout.list_item_order, null);
//        }
//
//        final OrderModel orderModel = orderModelList.get(i);
//
//        TextView restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
//        TextView customerName = (TextView) view.findViewById(R.id.customer_name);
//        TextView customerAddress = (TextView) view.findViewById(R.id.customer_address);
//        ImageView customerImage = (ImageView) view.findViewById(R.id.customer_image);
//
//        restaurantName.setText(orderModel.getRestaurantName());
//        customerName.setText(orderModel.getCustomerName());
//        customerAddress.setText(orderModel.getCustomerAddress());
//        Picasso.with(activity.getApplicationContext()).load(orderModel.getCustomerImage()).fit().centerInside().into(customerImage);
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Show an alert
//                AlertDialog.Builder builder = new AlertDialog.Builder((activity));
//                builder.setTitle("Pick this orderModel?");
//                builder.setMessage("Would you like to take this orderModel?");
//                builder.setPositiveButton("Cancel", null);
//                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(activity.getApplicationContext(), "ORDER PICKED", Toast.LENGTH_SHORT).show();
//
//                        pickOrder(orderModel.getId());
//                    }
//                });
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        });
//
//        return view;
//    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_order, viewGroup, false);

        return new OrderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, final int position) {


        String restaurantNameText = orderModelList.get(position).getRestaurantName();
        String customerNameText = orderModelList.get(position).getCustomerName();
        String customerAddressText = orderModelList.get(position).getCustomerAddress();
        String customerImageImage = orderModelList.get(position).getCustomerImage();
        orderViewHolder.setData(restaurantNameText, customerNameText, customerAddressText, customerImageImage);

        orderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show an alert
                AlertDialog.Builder builder = new AlertDialog.Builder((activity));
                builder.setTitle("Pick this orderModel?");
                builder.setMessage("Would you like to take this orderModel?");
                builder.setPositiveButton("Cancel", null);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(activity.getApplicationContext(), "ORDER PICKED", Toast.LENGTH_SHORT).show();

                        pickOrder(orderModelList.get(position).getId());
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName;
        TextView customerName;
        TextView customerAddress;
        ImageView customerImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
            customerName = (TextView) itemView.findViewById(R.id.customer_name);
            customerAddress = (TextView) itemView.findViewById(R.id.customer_address);
            customerImage = (ImageView) itemView.findViewById(R.id.customer_image);
        }

        private void setData(String restaurantNameText, String customerNameText, String customerAddressText, String customerImageImage) {
            restaurantName.setText(restaurantNameText);
            customerName.setText(customerNameText);
            customerAddress.setText(customerAddressText);
            Picasso.with(activity.getApplicationContext()).load(customerImageImage).fit().centerInside().into(customerImage);

        }
    }

    private void pickOrder(final String orderId) {
        String url = activity.getString(R.string.API_URL) + "/driver/order/pick/";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Execute code
                        Log.d("ORDER PICKED", response.toString());

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").equals("success")) {
                                FragmentTransaction transaction = ((DriverMainActivity) activity).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, new DeliveryFragment()).commit();
                            } else {
                                Toast.makeText(activity, jsonObj.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final SharedPreferences sharedPref = activity.getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", sharedPref.getString("token", ""));
                params.put("order_id", orderId);

                return params;
            }
        };

        postRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        if (queue == null) {
            queue = Volley.newRequestQueue(activity);
        }
        queue.add(postRequest);
    }


}
