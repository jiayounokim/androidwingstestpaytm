package com.example.wingstestpaytm.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmadrosid.lib.drawroutemap.DrawMarker;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wingstestpaytm.Activities.CustomerMainActivity;
import com.example.wingstestpaytm.Adapters.TrayAdapter;
import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.ApiServiceBuilder;
import com.example.wingstestpaytm.JsonModelObject.Example;
import com.example.wingstestpaytm.JsonModelObject.OrderDetail;
import com.example.wingstestpaytm.Objects.Tray;
import com.example.wingstestpaytm.R;
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
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment implements OnMapReadyCallback {
    public CustomerMainActivity context;
    private ArrayList<Tray> trayList;
    private TrayAdapter trayAdapter;
    private Button statusView;
    private GoogleMap mMap;
    private Timer timer = new Timer();
    private Marker driverMarker;
    private static final int DEFAULT_ZOOM = 15;
    private ArrayList<LatLng> MarkerPoints;
    private RequestQueue queue;

    private RecyclerView recyclerView;

    private ArrayList<OrderDetail> orderDetailJsonModelObjectList;


    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MarkerPoints = new ArrayList<>();

        trayList = new ArrayList<>();

        trayAdapter = new TrayAdapter(trayList, this.getActivity());

        recyclerView = getActivity().findViewById(R.id.tray_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(trayAdapter);



        statusView = getActivity().findViewById(R.id.status);

        getLatestOrder();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.order_map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                getDriverLocation();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 2000);

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

            ParserTask parserTask = new ParserTask();

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
                DataParser parser = new DataParser();
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

    private void drawRouteOnMap(retrofit2.Response<Example> response ) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
        ApiService service = ApiServiceBuilder.getService();
        Call<Example> call = service.getResponseQuery(sharedPref.getString("token", ""));
        Toast.makeText(context, sharedPref.getString("token", "") + " drawRouteOnMap", Toast.LENGTH_SHORT).show();
        Example example = response.body();
        String status = "";
        try {
            String restaurantAddress = example.getOrder().getRegistration().getAddress();
            String orderAddress = example.getOrder().getAddress();
            Geocoder coder = new Geocoder(context);
            ArrayList<Address> resAddresses = (ArrayList<Address>) coder.getFromLocationName(restaurantAddress, 1);
            ArrayList<Address> ordAddresses = (ArrayList<Address>) coder.getFromLocationName(orderAddress, 1);
            if (!resAddresses.isEmpty() && !ordAddresses.isEmpty()) {

                LatLng restaurantPos = new LatLng(resAddresses.get(0).getLatitude(), resAddresses.get(0).getLongitude());
                LatLng orderPos = new LatLng(ordAddresses.get(0).getLatitude(), ordAddresses.get(0).getLongitude());

                Toast.makeText(getContext(), "in maps", Toast.LENGTH_SHORT).show();


//---------new code added---------------------------
                MarkerPoints.add(restaurantPos);
                MarkerPoints.add(orderPos);
                String url = getUrl(restaurantPos, orderPos);
                Log.d("onMapClick", url.toString());
                FetchUrl FetchUrl = new FetchUrl();

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
//-----------------------------------------------------------------
                //     DrawRouteMaps.getInstance(getActivity(), "AIzaSyBH08CP-PtsmAdxJxIBbGpwlgfp8kA8sFM").draw(restaurantPos, orderPos, mMap);
                DrawMarker.getInstance(getActivity()).draw(mMap, restaurantPos, R.drawable.pin_restaurant, "RestaurantModel Location");
                DrawMarker.getInstance(getActivity()).draw(mMap, orderPos, R.drawable.pin_customer, "Customer Location");

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(restaurantPos)
                        .include(orderPos).build();
                Point displaySize = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }
    private void getLatestOrder() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
        ApiService service = ApiServiceBuilder.getService();
        Call<Example> call = service.getResponseQuery(sharedPref.getString("token", ""));
        Toast.makeText(context, sharedPref.getString("token", ""), Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, retrofit2.Response<Example> response) {
                Example example = response.body();
                String status = "";
                if (example != null && example.getOrder() != null) {
                    try {
                        orderDetailJsonModelObjectList = (ArrayList<OrderDetail>) example.getOrder().getOrderDetails();
                        for (OrderDetail o : orderDetailJsonModelObjectList) {
                            Log.d("ORDERDETAIL", "onResponse: " + o.getSubTotal().toString());
                        }
                        status = example.getOrder().getStatus();
                        Toast.makeText(getActivity(), " " + status, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (orderDetailJsonModelObjectList == null || orderDetailJsonModelObjectList.size() == 0) {
                        TextView alertText = new TextView(getActivity());
                        alertText.setText("You have no order");
                        alertText.setTextSize(17);
                        alertText.setGravity(Gravity.CENTER);
                        alertText.setLayoutParams(
                                new TableLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                        LinearLayout linearLayout = getActivity().findViewById(R.id.order_layout);
                        linearLayout.removeAllViews();
                        linearLayout.addView(alertText);

                    }


                    for (int i = 0; i < orderDetailJsonModelObjectList.size(); i++) {
                        Tray trayModel = new Tray();
                        try {
                            OrderDetail orderDetail = orderDetailJsonModelObjectList.get(i);
                            trayModel.setMealName(orderDetail.getMeal().getName());
                            trayModel.setMealPrice(orderDetail.getMeal().getPrice());
                            trayModel.setMealQuantity(orderDetail.getQuantity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        trayList.add(trayModel);

                    }

                    trayAdapter.notifyDataSetChanged();
                    statusView.setText(status);
                    drawRouteOnMap(response);
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });

    }

    private void getDriverLocation() {

        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);

        String url = getString(R.string.API_URL) + "/customer/driver/location/?access_token=" + sharedPref.getString("token", "");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("DRIVER LOCATION", response.toString());
                        try {

                            String[] location = response.getString("location").split(",");
                            String lat = location[0];
                            String lng = location[1];

                            LatLng driPos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            try {

                                driverMarker.remove();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            driverMarker = mMap.addMarker(
                                    new MarkerOptions()
                                            .position(driPos)
                                            .title("Driver Location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_car)));


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
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
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (CustomerMainActivity) context;
    }
}