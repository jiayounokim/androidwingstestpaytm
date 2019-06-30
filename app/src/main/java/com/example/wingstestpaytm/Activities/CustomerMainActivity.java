package com.example.wingstestpaytm.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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

public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mToggle;

    private SharedPreferences sharedPref;


    NavigationView navigationView;
    String screen;
    Intent intent;

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
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        intent = getIntent();
        screen = intent.getStringExtra("screen");

        Toast.makeText(this, "onStart " + screen, Toast.LENGTH_SHORT).show();

        if (Objects.equals(screen, "tray")) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.content_frame, new TrayFragment()).commit();

        } else if (Objects.equals(screen, "order")) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.content_frame, new OrderFragment()).commit();

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void logoutToServer(final String token) {

        ApiService service = ApiServiceBuilder.getService();

        Call<Void> call = service.getToken(token, getString(R.string.CLIENT_ID), getString(R.string.CLIENT_SECRET));
        call.enqueue(new Callback<Void>() {


            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {


                    Toast.makeText(CustomerMainActivity.this, "CUSTOMER SUCCESS RESPONSE RETROFIT " + response.code(), Toast.LENGTH_SHORT).show();

                }

                Toast.makeText(CustomerMainActivity.this, "CUSTOMER ON RESPONSE ONLY RETROFIT" + +response.code(), Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CustomerMainActivity.this, "CUSTOMER FAILURE RETROFIT " + t.getMessage().toString() + t.getCause() + t.toString(), Toast.LENGTH_SHORT).show();
            }


        });
    }

    MenuItem menuItem;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        menuItem = item;


        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {

                int id = menuItem.getItemId();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (id == R.id.nav_restaurant) {
                    Toast.makeText(CustomerMainActivity.this, "REST Customer", Toast.LENGTH_SHORT).show();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();

                } else if (id == R.id.nav_tray) {
                    if (Objects.equals(screen, "tray")) {
                        Toast.makeText(CustomerMainActivity.this, "TRAY Customer", Toast.LENGTH_SHORT).show();
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.content_frame, new TrayFragment()).commit();
                    } else {
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                        transaction.replace(R.id.content_frame, new TrayFragment()).commit();
                    }


                } else if (id == R.id.nav_order) {

                    if (Objects.equals(screen, "order")) {
                        Toast.makeText(CustomerMainActivity.this, "ORDERS Customer", Toast.LENGTH_SHORT).show();
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                        transaction.replace(R.id.content_frame, new OrderFragment()).commit();
                    } else{
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                        transaction.replace(R.id.content_frame, new OrderFragment()).commit();
                    }


                } else {
                    Toast.makeText(CustomerMainActivity.this, "LOGOUT Customer", Toast.LENGTH_SHORT).show();
                    logoutToServer(sharedPref.getString("token", ""));
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove("token");
                    editor.apply();

                    finishAffinity();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                }


            }
        });


        return false;


    }


}
