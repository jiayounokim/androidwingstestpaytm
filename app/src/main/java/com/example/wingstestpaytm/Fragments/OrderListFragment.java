package com.example.wingstestpaytm.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.example.wingstestpaytm.Adapters.OrderAdapter;
import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.ApiServiceBuilder;
import com.example.wingstestpaytm.Objects.OrderModel;
import com.example.wingstestpaytm.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderListFragment extends Fragment {

    public static final String TAG = "loghere";
    private OrderAdapter orderAdapter;
    private ArrayList<OrderModel> orderModelList;
    private RequestQueue queue;
    private RecyclerView recyclerView;

    public OrderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        orderModelList = new ArrayList<>();

        orderAdapter = new OrderAdapter(orderModelList, this.getActivity());


        recyclerView = getActivity().findViewById(R.id.order_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(orderAdapter);
        getReadyOrders();
    }

    private void getReadyOrders() {
        ApiService apiService = ApiServiceBuilder.getService();
        Call<OrderModel> call = apiService.getReponseDriverOrderReady();
        call.enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {

                Log.d("loghere", "onResponse: response.toString()" + response.toString());
                Log.d("loghere", "onResponse: response.body().toString() " + response.body().toString());

                ArrayList<OrderModel> orderModelArrayList = response.body().getOrders();
                Log.d(TAG, "onResponse: orderModelArrayList" + orderModelArrayList);
                try
                {

                for (int i = 0; i < orderModelArrayList.size(); i++) {
                    Log.d(TAG, "onResponse: \n" +
                            orderModelArrayList.get(i).getCustomer().getAddress() +
                            orderModelArrayList.get(i).getRegistration().getName());

                    OrderModel orderModel = new OrderModel(
                            orderModelArrayList.get(i).getId(),
                            orderModelArrayList.get(i).getRegistration().getName(),
                            orderModelArrayList.get(i).getCustomer().getName(),
                            orderModelArrayList.get(i).getAddress(),
                            orderModelArrayList.get(i).getCustomer().getAvatar()
                    );
                    orderModelList.add(orderModel);

                }
                }catch (Exception e){
                    e.printStackTrace();
                }
                orderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {
            }
        });

//        String url = getString(R.string.API_URL) + "/driver/orders/ready/";
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        Log.d("READY ORDER LIST", response.toString());
//
//                        try {
//
//                            JSONArray ordersJSONArray = response.getJSONArray("orders");
//                            for (int i = 0; i < ordersJSONArray.length(); i++) {
//
//                                JSONObject orderObject = ordersJSONArray.getJSONObject(i);
//
//                                OrderModel orderModel = new OrderModel(
//                                        orderObject.getString("id"),
//                                        orderObject.getJSONObject("registration").getString("name"),
//                                        orderObject.getJSONObject("customer").getString("name"),
//                                        orderObject.getString("address"),
//                                        orderObject.getJSONObject("customer").getString("avatar")
//
//                                );
//
//                                orderModelList.add(orderModel);
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        orderAdapter.notifyDataSetChanged();
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }
//        );
//
//        if (queue == null) {
//            queue = Volley.newRequestQueue(getActivity());
//        }
//        queue.add(jsonObjectRequest);

    }

}
