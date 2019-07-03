package com.example.wingstestpaytm.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import static com.example.wingstestpaytm.Activities.SignInActivity.BUTTON_SKIPPED;

public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private SharedPreferences sharedPref;
    NavigationView navigationView;
    String screen;
    Intent intent;
    private static final String TAG = "lgx_CustomerMainActivity";
    private ImageView customer_avatar;
    private TextView customer_name;

    @SuppressLint("LongLogTag")
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
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDrawerClosed(View drawerView) {
                int id = menuItem.getItemId();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (BUTTON_SKIPPED) {
                    Log.d(TAG, "onDrawerClosed: skipped below");
                    if (id == R.id.nav_restaurant) {
                        Log.d(TAG, "onDrawerClosed: nav restaurant skipped below IF");
                        Toast.makeText(CustomerMainActivity.this, "REST SKIPPED BELOW Customer", Toast.LENGTH_SHORT).show();
                    } else if (id == R.id.nav_tray) {
                        if (Objects.equals(screen, "tray")) {
                            Log.d(TAG, "onDrawerClosed: nav tray skipped below IF");
                            Toast.makeText(CustomerMainActivity.this, "TRAY SKIPPED BELOW Customer", Toast.LENGTH_SHORT).show();
                            handleLoginRequired();
                        } else {
                            Log.d(TAG, "onDrawerClosed: nav tray if_else Skipped");
                            handleLoginRequired();
                        }
                    } else if (id == R.id.nav_order) {
                        if (Objects.equals(screen, "order")) {
                            Log.d(TAG, "onDrawerClosed: nav order skipped below IF");
                            Toast.makeText(CustomerMainActivity.this, "ORDERS  SKIPPED BELOWCustomer", Toast.LENGTH_SHORT).show();
                            handleLoginRequired();
                        } else {
                            Log.d(TAG, "onDrawerClosed: nav order if_else Skipped");
                            handleLoginRequired();
                        }
                    } else {
                        Log.d(TAG, "onDrawerClosed: nav logout skipped below IF");
                        Toast.makeText(CustomerMainActivity.this, "LOGOUT SKIPPED BELOW Customer", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onDrawerClosed: logout clicked");
                        finishAffinity();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                    }
                } else {
                    BUTTON_SKIPPED = false;
                    if (id == R.id.nav_restaurant) {
                        Log.d(TAG, "onDrawerClosed: nav restaurant ELSE");
                        Toast.makeText(CustomerMainActivity.this, "REST Customer", Toast.LENGTH_SHORT).show();
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();
                    } else if (id == R.id.nav_tray) {
                        if (Objects.equals(screen, "tray")) {
                            Log.d(TAG, "onDrawerClosed: nav tray ELSE");
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
                        } else {
                            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction.replace(R.id.content_frame, new OrderFragment()).commit();
                        }
                    } else {
                        Toast.makeText(CustomerMainActivity.this, "LOGOUT Customer", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onDrawerClosed: logout clicked");
                        logoutToServer(sharedPref.getString("token", ""));
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove("token");
                        editor.apply();
                        finishAffinity();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        Toast.makeText(this, "onStart " + screen, Toast.LENGTH_SHORT).show();
        if (BUTTON_SKIPPED) {
            Log.d(TAG, "onCreate: BUTTON_SKIPPED IF --- " + BUTTON_SKIPPED);
            if (Objects.equals(screen, "tray")) {
                Log.d(TAG, "onCreate: tray skipped above IF");
                Toast.makeText(this, "Tray needs login", Toast.LENGTH_SHORT).show();
            } else if (Objects.equals(screen, "order")) {
                Log.d(TAG, "onCreate: order skipped above IF");
                Toast.makeText(this, "Order needs login", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onCreate: restaurant skipped above IF");
                Toast.makeText(this, "SKIPPED ABOVE RESTAURANT", Toast.LENGTH_SHORT).show();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();
            }
        } else {
            BUTTON_SKIPPED = false;
            Log.d(TAG, "onCreate: BUTTON_SKIPPED ELSE--- " + BUTTON_SKIPPED);
            if (Objects.equals(screen, "tray")) {
                Log.d(TAG, "onCreate: tray above ELSE");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.content_frame, new TrayFragment()).commit();
            } else if (Objects.equals(screen, "order")) {
                Log.d(TAG, "onCreate: order above ELSE");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.content_frame, new OrderFragment()).commit();
            } else {
                Log.d(TAG, "onCreate: restaurant above ELSE");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();
            }
        }
        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
        View header = navigationView.getHeaderView(0);
        customer_avatar = (ImageView) header.findViewById(R.id.customer_avatar);
        customer_name = header.findViewById(R.id.customer_name);
        if (BUTTON_SKIPPED) {
            Log.d(TAG, "BUTTON SKIPPED");
            customer_name.setText("Guest name");
            customer_avatar.setBackgroundResource(R.drawable.button_tray);
        } else {
            BUTTON_SKIPPED = false;
            Log.d(TAG, "BUTTON NOT SKIPPED");
            customer_name.setText(sharedPref.getString("name", ""));
            Picasso.with(this).load(sharedPref.getString("avatar", "")).transform(new CircleTransform()).into(customer_avatar);
        }
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

    @SuppressLint("LongLogTag")
    private void logoutToServer(final String token) {
        BUTTON_SKIPPED = true;
        Log.d(TAG, "logoutToServer: inside logoutToServer " + token);
        ApiService service = ApiServiceBuilder.getService();
        Call<Void> call = service.getToken(token, getString(R.string.CLIENT_ID), getString(R.string.CLIENT_SECRET));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "onResponse: logoutToServer " + response.body());
                if (response.isSuccessful()) {
                    Toast.makeText(CustomerMainActivity.this, "CUSTOMER SUCCESS RESPONSE RETROFIT " + response.code(), Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(CustomerMainActivity.this, "CUSTOMER ON RESPONSE ONLY RETROFIT" + +response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "onResponse: logoutToServer onFailure" + t.toString());
                Toast.makeText(CustomerMainActivity.this, "CUSTOMER FAILURE RETROFIT " + t.getMessage().toString() + t.getCause() + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    MenuItem menuItem;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuItem = item;
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLoginRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMainActivity.this);
        builder.setTitle("Start New Tray?");
        builder.setMessage("You are ordering meal from another restaurant. Would you like to clean the current tray?");
        builder.setPositiveButton("Cancel", null);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent loginIntent = new Intent(CustomerMainActivity.this, SignInActivity.class);
                startActivity(loginIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}