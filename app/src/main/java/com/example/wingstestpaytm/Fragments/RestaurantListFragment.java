package com.example.wingstestpaytm.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wingstestpaytm.Adapters.ResAdapter;
import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.ApiServiceBuilder;
import com.example.wingstestpaytm.JsonModelObject.Registration;
import com.example.wingstestpaytm.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantListFragment extends Fragment {
    public static final String TAG = "loghere";


    private RecyclerView recyclerView;
    private ArrayList<Registration> restaurantModelArrayList;
    //    private RestaurantAdapter adapter;
    private ResAdapter resAdapter;
    private Registration[] listArr = new Registration[]{};

    public RestaurantListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        restaurantModelArrayList = new ArrayList<Registration>();

        resAdapter = new ResAdapter(restaurantModelArrayList, this.getActivity());

        recyclerView = getActivity().findViewById(R.id.restaurant_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(resAdapter);

        getRestaurants();

        addSearchFunction();
    }

    private void getRestaurants() {

        ApiService apiService = ApiServiceBuilder.getService();

        Call<Registration> call = apiService.getResponseRestuarnatList();

        call.enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(Call<Registration> call, Response<Registration> response) {

                Registration registration = response.body();
                for (int i = 0; i < response.body().getRegistrationArrayListModel().size(); i++) {

                    restaurantModelArrayList.add(response.body().getRegistrationArrayListModel().get(i));

                    Log.d(TAG, "onResponse: restaurantModelArrayList" + restaurantModelArrayList.get(i).getName());
                }
                listArr = new Registration[restaurantModelArrayList.size()];
                listArr = restaurantModelArrayList.toArray(listArr);
                resAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<Registration> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }

    private void addSearchFunction() {

        EditText searchInput = getActivity().findViewById(R.id.res_search);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                Log.d("SEARCH", charSequence.toString());

                restaurantModelArrayList.clear();

                for (Registration r : listArr) {
                    if (r.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {

                        restaurantModelArrayList.add(r);

                    }
                }
                resAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
