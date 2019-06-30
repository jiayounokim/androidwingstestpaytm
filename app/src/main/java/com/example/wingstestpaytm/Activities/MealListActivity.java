package com.example.wingstestpaytm.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.wingstestpaytm.Adapters.MealAdapter;
import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.ApiServiceBuilder;
import com.example.wingstestpaytm.Objects.MealModel;
import com.example.wingstestpaytm.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealListActivity extends AppCompatActivity {

    private ArrayList<MealModel> mealArrayList;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra("restaurantId");
        String restaurantName = intent.getStringExtra("restaurantName");

        getSupportActionBar().setTitle(restaurantName);

        mealArrayList = new ArrayList<>();
        mealAdapter = new MealAdapter(mealArrayList, this, restaurantId);

        recyclerView =  findViewById(R.id.meal_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mealAdapter);


        getMeals(restaurantId);
    }

    private void getMeals(String restaurantId) {

        //This is an instance of our ApiService, and we are gonna pass our API service class,
        ApiService apiService = ApiServiceBuilder.getService();
        // Next we are Calling our method on this API
        Call<MealModel> mealCall = apiService.getMeals(Integer.parseInt(restaurantId));
        // Now we choose if we are going to call it synchronously or asynchronously
        // Since we are in an activity and an UI thread we need to do an Async with the method enqueue
        mealCall.enqueue(new Callback<MealModel>() {
            @Override
            public void onResponse(Call<MealModel> call, Response<MealModel> response) {
                //What happens when we get a reponse from our server
                for (int i = 0; i < response.body().getMealList().size(); i++)
                    mealArrayList.add(response.body().getMealList().get(i));
                mealAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MealModel> call, Throwable t) {
                // This throws all the http error codes, network failures, null exceptions
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}