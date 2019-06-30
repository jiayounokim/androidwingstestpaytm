package com.example.wingstestpaytm.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.wingstestpaytm.Activities.PaymentActivity;
import com.example.wingstestpaytm.Adapters.TrayAdapter;
import com.example.wingstestpaytm.AppDatabase;
import com.example.wingstestpaytm.Objects.Tray;
import com.example.wingstestpaytm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrayFragment extends Fragment implements OnMapReadyCallback {

    private AppDatabase db;
    private ArrayList<Tray> trayList;
    private TrayAdapter trayAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private TextView totalView;
    private EditText address;

    private RecyclerView recyclerView;

    public TrayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_tray, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = AppDatabase.getAppDatabase(getContext());
        listTray();

        trayList = new ArrayList<>();

        trayAdapter = new TrayAdapter(trayList, this.getActivity());

        recyclerView = getActivity().findViewById(R.id.tray_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(trayAdapter);

//        ListView listView = getActivity().findViewById(R.id.tray_list);
//        listView.setAdapter(adapter);


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.tray_map);
        mapFragment.getMapAsync(this);

        address = getActivity().findViewById(R.id.tray_address);

        handleMapAddress();

        handleAddPayment();

    }

    @SuppressLint("StaticFieldLeak")
    private void listTray() {

        new AsyncTask<Void, Void, List<Tray>>() {

            @Override
            protected List<Tray> doInBackground(Void... voids) {
                return db.trayDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Tray> trays) {
                super.onPostExecute(trays);
                if (!trays.isEmpty()) {
                    trayList.clear();
                    trayList.addAll(trays);
                    trayAdapter.notifyDataSetChanged();

                    float total = 0;
                    for (Tray tray : trays) {

                        total += tray.getMealQuantity() * tray.getMealPrice();
                    }

                    totalView = getActivity().findViewById(R.id.tray_total);
                    totalView.setText("Rs." + total);

                } else {

                    TextView alertText = new TextView(getActivity());
                    alertText.setText("Your tray is empty. Please order a meal");
                    alertText.setTextSize(17);
                    alertText.setGravity(Gravity.CENTER);
                    alertText.setLayoutParams(
                            new TableLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                    LinearLayout linearLayout = getActivity().findViewById(R.id.tray_layout);

                    //linearLayout.removeAllViews();

                    ///this is for testing paytm.
                    //linearLayout.addView(alertText);


                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                    getDeviceLocation();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        getLocationPermission();
        getDeviceLocation();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            TrayFragment.this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();

                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                mMap.addMarker(new MarkerOptions().position(
                                        new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())
                                ));

                                Geocoder coder = new Geocoder(getActivity());
                                try {

                                    ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocation(
                                            mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1
                                    );

                                    if (!addresses.isEmpty()) {

                                        address.setText(addresses.get(0).getAddressLine(0));
                                    }

                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void handleMapAddress() {

        address.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                if (i == EditorInfo.IME_ACTION_DONE) {

                    Geocoder coder = new Geocoder(getActivity());
                    try {

                        ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocationName(textView.getText().toString(), 1);

                        if (!addresses.isEmpty()) {

                            double lat = addresses.get(0).getLatitude();
                            double lng = addresses.get(0).getLongitude();

                            LatLng pos = new LatLng(lat, lng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM));
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(pos));


                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
    }

    private void handleAddPayment() {

        Button buttonAddPayment = getActivity().findViewById(R.id.button_add_payment);
        buttonAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.getText().toString().equals("")) {
                    address.setError("Address cannot be blank");
                } else {
                    Intent intent = new Intent(getContext(), PaymentActivity.class);


                    /////for testing pay tm///

                   // ArrayList<Tray> tList= new ArrayList<Tray>();

                    //Tray  t1= new Tray();
                 //   Tray t2=new Tray();

                   // t1.setId(0);
                    //t2.setId(1);

                    //t1.setRestaurantId("1234");
                    //t2.setRestaurantId("2345");

                    //t1.setMealId("1");
                    //t2.setMealId("2");

                   // t1.setMealQuantity(2);
                    //t2.setMealQuantity(3);

                    //tList.add(t1);
                    //tList.add(t2);


                    /////////////////////////


                    intent.putExtra("restaurantId", trayList.get(0).getRestaurantId());//");//trayList.get(0).getRestaurantId());
                    intent.putExtra("address", address.getText().toString());

                    ArrayList<HashMap<String, Integer>> orderDetails = new ArrayList<HashMap<String, Integer>>();
                    for (Tray tray : trayList) {//trayList  changed to tList for checking

                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("meal_id", Integer.parseInt(tray.getMealId()));
                        map.put("quantity", tray.getMealQuantity());
                        orderDetails.add(map);

                    }

                    intent.putExtra("orderDetails", new Gson().toJson(orderDetails));

                    startActivity(intent);
                }
            }
        });
    }

}
