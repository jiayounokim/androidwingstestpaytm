package com.example.wingstestpaytm.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.ApiServiceBuilder;
import com.example.wingstestpaytm.JsonModelObject.revenue.ExampleRevenue;
import com.example.wingstestpaytm.JsonModelObject.revenue.Revenue;
import com.example.wingstestpaytm.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {

    private BarChart chart;

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chart = getActivity().findViewById(R.id.chart);

        getDriverRevenue();
    }

    private void dummyChart(Revenue response) {

        try {

            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, response.getMon()));
            entries.add(new BarEntry(1f, response.getTue()));
            entries.add(new BarEntry(2f, response.getWed()));
            entries.add(new BarEntry(3f, response.getThu()));
            entries.add(new BarEntry(4f, response.getFri()));
            entries.add(new BarEntry(5f, response.getSat()));
            entries.add(new BarEntry(6f, response.getSun()));


            BarDataSet set = new BarDataSet(entries, "Revenue by Day");
            set.setColor(getResources().getColor(R.color.colorAccent));

            BarData data = new BarData(set);
            data.setBarWidth(0.9f);
            chart.setData(data);
            chart.setFitBars(true);
            chart.invalidate();

            final String[] days = new String[]{

                    "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
            };

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return days[(int) value];
                }
            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1F);
            xAxis.setValueFormatter(formatter);

            chart.setDescription(null);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis axisRight = chart.getAxisRight();
            axisRight.setEnabled(false);

            YAxis axisLeft = chart.getAxisLeft();
            axisLeft.setAxisMinimum((float) 0.0);
            axisLeft.setAxisMaximum((float) 100.0);


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private void getDriverRevenue() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);

        ApiService service = ApiServiceBuilder.getService();
        Call<ExampleRevenue> call = service.getResponseDriverRevenue(sharedPref.getString("token", ""));

        call.enqueue(new Callback<ExampleRevenue>() {
            @Override
            public void onResponse(Call<ExampleRevenue> call, Response<ExampleRevenue> response) {
                Log.d("GET DRIVER REVENUE", response.toString());

                dummyChart(response.body().getRevenue());
            }

            @Override
            public void onFailure(Call<ExampleRevenue> call, Throwable t) {

            }
        });

//
//        String url = getString(R.string.API_URL) + "/driver/revenue/?access_token=" + sharedPref.getString("token", "");
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        Log.d("GET DRIVER REVENUE", response.toString());
//                        dummyChart(response);
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
//

    }
}
