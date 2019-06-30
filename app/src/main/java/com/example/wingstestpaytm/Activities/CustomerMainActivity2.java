package com.example.wingstestpaytm.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.ApiServiceBuilder;
import com.example.wingstestpaytm.Fragments.OrderFragment;
import com.example.wingstestpaytm.Fragments.RestaurantListFragment;
import com.example.wingstestpaytm.Fragments.TrayFragment;
import com.example.wingstestpaytm.R;
import com.example.wingstestpaytm.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerMainActivity2 extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        int id = menuItem.getItemId();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        if (id == R.id.nav_restaurant){
                            /**
                             * {@link com.example.wingstestpaytm.DocumentationApp.DocumentationAppClass.addRestaurantListFragmentToCustomerMainActivity }
                             */
                            transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();

                        } else if (id == R.id.nav_tray){
                            transaction.replace(R.id.content_frame, new TrayFragment()).commit();

                        } else if (id == R.id.nav_order){
                            transaction.replace(R.id.content_frame, new OrderFragment()).commit();


                        } else if (id == R.id.nav_logout){

                            logoutToServer(sharedPref.getString("token", ""));
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove("token");
                            editor.apply();

                            finishAffinity();
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);


                        }


                        return true;
                    }
                });


        Intent intent = getIntent();
        String screen = intent.getStringExtra("screen");

        if (Objects.equals(screen, "tray")){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new TrayFragment()).commit();

        } else if (Objects.equals(screen, "order")){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new OrderFragment()).commit();

        }


        else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();
        }

        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);

        View header = navigationView.getHeaderView(0);
        ImageView customer_avatar = (ImageView) header.findViewById(R.id.customer_avatar);
        TextView customer_name = header.findViewById(R.id.customer_name);

        customer_name.setText(sharedPref.getString("name", ""));
        Picasso.with(this).load(sharedPref.getString("avatar", "")).transform(new CircleTransform()).into(customer_avatar);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

    private void logoutToServer(final String token){




        ApiService service = ApiServiceBuilder.getService();

        Call<Void> call = service.getToken(token, getString(R.string.CLIENT_ID), getString(R.string.CLIENT_SECRET));
        call.enqueue(new Callback<Void>() {


            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(CustomerMainActivity2.this, "CUSTOMER SUCCESS RESPONSE RETROFIT", Toast.LENGTH_SHORT).show();

                }

                Toast.makeText(CustomerMainActivity2.this, "CUSTOMER ON RESPONSE ONLY RETROFIT", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CustomerMainActivity2.this, "CUSTOMER FAILURE RETROFIT", Toast.LENGTH_SHORT).show();
            }


        });
    }

}
