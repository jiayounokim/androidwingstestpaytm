package com.example.wingstestpaytm.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmadrosid.lib.drawroutemap.DrawMarker;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wingstestpaytm.R;
import com.example.wingstestpaytm.Utils.CircleTransform;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryFragment extends Fragment implements OnMapReadyCallback {


    private TextView customerName;
    private TextView customerAddress;
    private ImageView customerImage;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private Marker driverMarker;
    private ArrayList<LatLng> MarkerPoints;
    private LocationCallback mLocationCallback;
    private String orderId;
    private RequestQueue queue;

    public DeliveryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customerName = getActivity().findViewById(R.id.customer_name_driver);
        customerAddress = getActivity().findViewById(R.id.customer_address_driver);
        customerImage = getActivity().findViewById(R.id.customer_image_driver);
        MarkerPoints = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.delivery_map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getLatestOrder();

        handleButtonCompleteOrder();

    }

    private void getLatestOrder() {

        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);

        String url = getString(R.string.API_URL) + "/driver/order/latest/?access_token=" + sharedPref.getString("token", "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("GET LATEST ORDER", response.toString());

                        JSONObject latestOrderJSONObject = null;
                        orderId = null;
                        Boolean orderIsDelivered = null;


                        try {

                            latestOrderJSONObject = response.getJSONObject("order");
                            orderId = latestOrderJSONObject.getString("id");
                            orderIsDelivered = latestOrderJSONObject.getString("status").equals("Delivered");

                            customerName.setText(latestOrderJSONObject.getJSONObject("customer").getString("name"));
                            customerAddress.setText(latestOrderJSONObject.getString("address"));
                            Picasso.with(getActivity())
                                    .load(latestOrderJSONObject.getJSONObject("customer").getString("avatar"))
                                    .transform(new CircleTransform())
                                    .into(customerImage);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                        if (latestOrderJSONObject == null || orderId == null || orderIsDelivered) {

                            TextView alertText = new TextView(getActivity());
                            alertText.setText("you have no outstanding order");
                            alertText.setTextSize(17);
                            alertText.setId(alertText.generateViewId());

                            ConstraintLayout constraintLayout = getActivity().findViewById(R.id.delivery_layout);
                            constraintLayout.removeAllViews();
                            constraintLayout.addView(alertText);

                            ConstraintSet set = new ConstraintSet();
                            set.clone(constraintLayout);
                            set.connect(alertText.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
                            set.connect(alertText.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
                            set.connect(alertText.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT);
                            set.connect(alertText.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT);
                            set.applyTo(constraintLayout);

                        }

                        drawRouteOnMap(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        if (queue == null) {
            queue = Volley.newRequestQueue(getActivity());
        }
        queue.add(jsonObjectRequest);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        getLocationPermission();
        getDeviceLocation();

        startLocationUpdates();
    }

    //---------------------new functions start-----------------------
    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBH08CP-PtsmAdxJxIBbGpwlgfp8kA8sFM";


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            DeliveryFragment.ParserTask parserTask = new DeliveryFragment.ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DeliveryFragment.DataParser parser = new DeliveryFragment.DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public class DataParser {

        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude));
                                hm.put("lng", Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }


            return routes;
        }


        /**
         * Method to decode polyline points
         * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }
    //------------------------new function end--------------------------------------

    private void drawRouteOnMap(JSONObject response) {

        try {

            String restaurantAddress = response.getJSONObject("order").getJSONObject("registration").getString("address");
            String orderAddress = response.getJSONObject("order").getString("address");

            Geocoder coder = new Geocoder(getActivity());
            ArrayList<Address> resAddresses = (ArrayList<Address>) coder.getFromLocationName(restaurantAddress, 1);
            ArrayList<Address> ordAddresses = (ArrayList<Address>) coder.getFromLocationName(orderAddress, 1);

            if (!resAddresses.isEmpty() && !ordAddresses.isEmpty()) {

                LatLng restaurantPos = new LatLng(resAddresses.get(0).getLatitude(), resAddresses.get(0).getLongitude());
                LatLng orderPos = new LatLng(ordAddresses.get(0).getLatitude(), ordAddresses.get(0).getLongitude());
//---------new code added---------------------------
                MarkerPoints.add(restaurantPos);
                MarkerPoints.add(orderPos);
                String url = getUrl(restaurantPos, orderPos);
                Log.d("onMapClick", url.toString());
                DeliveryFragment.FetchUrl FetchUrl = new DeliveryFragment.FetchUrl();

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
//-----------------------------------------------------------------
                //        DrawRouteMaps.getInstance(getActivity(), "AIzaSyBH08CP-PtsmAdxJxIBbGpwlgfp8kA8sFM").draw(restaurantPos, orderPos, mMap);
                DrawMarker.getInstance(getActivity()).draw(mMap, restaurantPos, R.drawable.pin_restaurant, "RestaurantModel Location");
                DrawMarker.getInstance(getActivity()).draw(mMap, orderPos, R.drawable.pin_customer, "Customer Location");

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(restaurantPos)
                        .include(orderPos).build();
                Point displaySize = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
            }


        } catch (JSONException | IOException e) {

            e.printStackTrace();
        }
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
            DeliveryFragment.this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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

                                LatLng pos = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                driverMarker = mMap.addMarker(new MarkerOptions().position(pos)
                                        .title("Driver Location")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));

                                updateDriverLocation(mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());

                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
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

                    startLocationUpdates();
                }
            }
        }
    }

    private void startLocationUpdates() {

        try {

            if (mLocationPermissionGranted) {

                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(500);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                mLocationCallback = new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {

                            return;
                        }

                        for (Location location : locationResult.getLocations()) {

                            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                            try {

                                driverMarker.remove();

                            } catch (Exception e) {
                            }
                            driverMarker = mMap.addMarker(
                                    new MarkerOptions()
                                            .position(pos)
                                            .title("Driver Location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car))

                            );

                            updateDriverLocation(location.getLatitude() + "," + location.getLongitude());

                            Log.d("NEW DRIVER LOCATION", Double.toString(pos.latitude) + "," + Double.toString(pos.longitude));

                        }
                    }

                };

                mFusedLocationProviderClient.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        null

                );


            }
        } catch (SecurityException e) {

            Log.d("Exception %s", e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }


    private void updateDriverLocation(final String location) {


        String url = getString(R.string.API_URL) + "/driver/location/update/";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                        Log.d("UPDATE DRIVER LOCATION", response);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("ERROR MESSAGE", error.toString());

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();

                params.put("access_token", sharedPref.getString("token", ""));
                params.put("location", location);


                return params;

            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue = Volley.newRequestQueue(getActivity());
        queue.add(postRequest);

    }

    private void handleButtonCompleteOrder() {

        Button buttonCompleteOrder = getActivity().findViewById(R.id.button_complete_order);
        buttonCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Show an alert
                AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
                builder.setTitle("Complete OrderModel");
                builder.setMessage("Is this order completed?");
                builder.setPositiveButton("Cancel", null);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        completeOrder(orderId);

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    private void completeOrder(final String orderId) {


        String url = getString(R.string.API_URL) + "/driver/order/complete/";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                        Log.d("ORDER COMPLETED", response);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content_frame, new OrderListFragment()).commit();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("ERROR MESSAGE", error.toString());

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();

                params.put("access_token", sharedPref.getString("token", ""));
                params.put("order_id", orderId);


                return params;

            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (queue == null) {

            queue = Volley.newRequestQueue(getActivity());
        }
        queue.add(postRequest);


    }
}
