//package com.example.wingstestpaytm.Activities;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.android.volley.RequestQueue;
//import com.example.wingstestpaytm.ApiService;
//import com.example.wingstestpaytm.ApiServiceBuilder;
//import com.example.wingstestpaytm.AppDatabase;
//import com.example.wingstestpaytm.JsonModelObject.StatusSuccessOrError;
//import com.example.wingstestpaytm.R;
//import com.stripe.android.Stripe;
//import com.stripe.android.TokenCallback;
//import com.stripe.android.model.Card;
//import com.stripe.android.model.Token;
//import com.stripe.android.view.CardInputWidget;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class PaymentActivity2 extends AppCompatActivity {
//
//    private String restaurantId, address, orderDetails;
//    private Button buttonPlaceOrder;
//    private RequestQueue queue;
//    private SharedPreferences sharedPref;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment);
//        getSupportActionBar().setTitle("");
//
//        Intent intent = getIntent();
//        restaurantId = intent.getStringExtra("restaurantId");
//        address = intent.getStringExtra("address");
//        orderDetails = intent.getStringExtra("orderDetails");
//
//        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
//
//        final CardInputWidget mCardInputWidget = findViewById(R.id.card_input_widget);
//        buttonPlaceOrder = findViewById(R.id.button_place_order);
//        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("StaticFieldLeak")
//            @Override
//            public void onClick(View v) {
//                final Card card = mCardInputWidget.getCard();
//                if (card == null) {
//                    Toast.makeText(PaymentActivity2.this, "Card cannot be blank", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    setButtonPlaceOrder("LOADING...", false);
//
//
//                    new AsyncTask<Void, Void, Void>() {
//                        @SuppressLint("WrongThread")
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//
//                            Stripe stripe = new Stripe(getApplicationContext(), "pk_test_ba3iZklWU8qQFubI4mD4IHnM");
//                            stripe.createToken(
//                                    card,
//                                    new TokenCallback() {
//                                        public void onSuccess(Token token) {
//
//                                            addOrder(token.getId(), sharedPref.getString("token", ""));
//
//                                        }
//
//                                        public void onError(Exception error) {
//                                            // Show localized error message
//                                            Toast.makeText(getApplicationContext(),
//                                                    error.getLocalizedMessage(),
//                                                    Toast.LENGTH_LONG
//                                            ).show();
//
//                                            setButtonPlaceOrder("PLACE ORDER", true);
//
//
//                                        }
//                                    }
//                            );
//
//                            return null;
//                        }
//                    }.execute();
//                }
//            }
//        });
//    }
//
//    private void addOrder(final String stripeToken, String accessToken) {
//
//        ApiService service = ApiServiceBuilder.getService();
//        Call<StatusSuccessOrError> call = service.getCustomerOrderAddPayment(accessToken, restaurantId, address, orderDetails, stripeToken);
//
//        call.enqueue(new Callback<StatusSuccessOrError>() {
//            @Override
//            public void onResponse(Call<StatusSuccessOrError> call, Response<StatusSuccessOrError> response) {
//                try {
//                    StatusSuccessOrError statusSuccessOrError = response.body();
//
//                    if (statusSuccessOrError.getStatus().equals("success")) {
//
//                        Toast.makeText(PaymentActivity2.this, " success ", Toast.LENGTH_SHORT).show();
//                        Log.d("loghere", "onResponse: success onResponse before deleteTray");
//                        deleteTray();
//
//                        Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
//                        intent.putExtra("screen", "order");
//                        startActivity(intent);
//                    } else {
//
//                        Toast.makeText(PaymentActivity2.this, "error", Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                setButtonPlaceOrder("PLACE ORDER", true);
//
//            }
//
//            @Override
//            public void onFailure(Call<StatusSuccessOrError> call, Throwable t) {
//                setButtonPlaceOrder("PLACE ORDER", true);
//                Toast.makeText(PaymentActivity2.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
////        String url = getString(R.string.API_URL) + "/customer/order/add/";
////
////        StringRequest postRequest = new StringRequest
////                (Request.Method.POST, url, new Response.Listener<String>() {
////
////                    @Override
////                    public void onResponse(String response) {
////
////
////                        Log.d("ORDER ADDED", response.toString());
////                        try {
////                            JSONObject jsonObject = new JSONObject(response);
////                            if (jsonObject.getString("status").equals("success")) {
////
////
////                                deleteTray();
////
////                                Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
////                                intent.putExtra("screen", "order");
////                                startActivity(intent);
////
////
////                            } else {
////
////                                Toast.makeText(PaymentActivity2.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
////                            }
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////
////                        setButtonPlaceOrder("PLACE ORDER", true);
////
////
////                    }
////                }, new Response.ErrorListener() {
////
////                    @Override
////                    public void onErrorResponse(VolleyError error) {
////                        // TODO: Handle error
////
////                        setButtonPlaceOrder("PLACE ORDER", true);
////
////                        Toast.makeText(PaymentActivity2.this, error.toString(), Toast.LENGTH_SHORT).show();
////
////
////                    }
////                }) {
////
////            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////
////                final SharedPreferences sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
////                Map<String, String> params = new HashMap<String, String>();
////
////                params.put("access_token", sharedPref.getString("token", ""));
////                params.put("registration_id", restaurantId);
////                params.put("address", address);
////                params.put("order_details", orderDetails);
////                params.put("stripe_token", stripeToken);
////
////
////                return params;
////
////            }
////        };
////
////        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
////
////        Log.d("VOLLEY REQUEST MULT", String.valueOf(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
////        Log.d("VOLLEY REQUEST TIMEOUT", String.valueOf(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
////        Log.d("VOLLEY REQUEST TIMEOUT", String.valueOf(DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
////
////        if (queue == null) {
////            queue = Volley.newRequestQueue(this);
////        }
////        queue.add(postRequest);
////    }
//
//    }
//
//    private void setButtonPlaceOrder(String text, boolean isEnable) {
//
//        buttonPlaceOrder.setText(text);
//        buttonPlaceOrder.setClickable(isEnable);
//        if (isEnable) {
//            buttonPlaceOrder.setBackgroundColor(getResources().getColor(R.color.colorGreen));
//
//
//        } else {
//
//            buttonPlaceOrder.setBackgroundColor(getResources().getColor(R.color.colorLightGray));
//
//        }
//    }
//
//
//    @SuppressLint("StaticFieldLeak")
//    public void deleteTray() {
//
//        final AppDatabase db = AppDatabase.getAppDatabase(this);
//
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                db.trayDao().deleteAll();
//                return null;
//            }
//        }.execute();
//    }
//}
