package com.example.wingstestpaytm.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wingstestpaytm.ApiClientFabCoding;
import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.AppDatabase;
import com.example.wingstestpaytm.ChecksumPaytm;
import com.example.wingstestpaytm.Objects.TransactionStatus;
import com.example.wingstestpaytm.R;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaymentActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    private String restaurantId, address, orderDetails;
    private Button buttonPlaceOrder;
    private SharedPreferences sharedPref;
    String custid = "", orderId = "";
    private static final String TAG = "loghere";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");
        address = intent.getStringExtra("address");
        orderDetails = intent.getStringExtra("orderDetails");

        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
        //creating random orderId and custId just for demonstration.
        //orderId = Helper.generateRandomString();
        //custid = Helper.generateRandomString();

        Log.d(TAG, "onCreate: Payment: " + orderId + " " + custid);

        final CardInputWidget mCardInputWidget = findViewById(R.id.card_input_widget);
        buttonPlaceOrder = findViewById(R.id.button_place_order);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PaymentActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }

                //call this function on click event for BUY or CHECKOUT button
                generateCheckSum();

//                final String M_id = "Ulbgcl83114033677105";
//                final String customer_id = UUID.randomUUID().toString().substring(0, 28);
//                final String order_id = UUID.randomUUID().toString().substring(0, 28);
//                String url = "http://paytmwingstest1.atwebpages.com/paytm/generateChecksumPaytm.php";
//                final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
//
//
//                RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//
//                        Toast.makeText(PaymentActivity.this, "onResponse", Toast.LENGTH_SHORT).show();
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            if (jsonObject.has("CHECKSUMHASH")) {
//
//                                String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");
//
//                                PaytmPGService paytmPGService = PaytmPGService.getStagingService();
//
//                                HashMap<String, String> paramMap = new HashMap<String, String>();
//                                paramMap.put("MID", M_id);
//                                paramMap.put("ORDER_ID", order_id);
//                                paramMap.put("CUST_ID", customer_id);
//                                paramMap.put("CHANNEL_ID", "WAP");
//                                paramMap.put("TXN_AMOUNT", "200");
//                                paramMap.put("WEBSITE", "WEBSTAGING");
//                                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
//                                paramMap.put("CALLBACK_URL", callBackUrl);
//                                paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
//
//                                final PaytmOrder order = new PaytmOrder(paramMap);
//
//                                paytmPGService.initialize(order, null);
//                                paytmPGService.startPaymentTransaction(PaymentActivity.this, true, true, new PaytmPaymentTransactionCallback() {
//                                    @Override
//                                    public void onTransactionResponse(Bundle inResponse) {
//
//                                        Log.d(TAG, "onTransactionResponse: PaymentActivity: " + inResponse);
//
//                                        String statusPaytmBundleResponse = inResponse.getString("STATUS");
//                                        Log.d(TAG, "onTransactionResponse: " + statusPaytmBundleResponse);
//
//                                        Toast.makeText(getApplicationContext(), "onTransactionResponse", Toast.LENGTH_LONG).show();
//
////                                        addOrder(statusPaytmBundleResponse, sharedPref.getString("token", ""));
//
//                                    }
//
//                                    @Override
//                                    public void networkNotAvailable() {
//                                        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
//                                    }
//
//                                    @Override
//                                    public void clientAuthenticationFailed(String inErrorMessage) {
//                                        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
//                                    }
//
//                                    @Override
//                                    public void someUIErrorOccurred(String inErrorMessage) {
//                                        Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
//
//                                    }
//
//                                    @Override
//                                    public void onErrorLoadingWebPage(int iniErrorCode, String
//                                            inErrorMessage, String inFailingUrl) {
//                                        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
//                                    }
//
//                                    @Override
//                                    public void onBackPressedCancelTransaction() {
//                                        Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
//                                    }
//
//                                    @Override
//                                    public void onTransactionCancel(String inErrorMessage, Bundle
//                                            inResponse) {
//                                        Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
//
//                            }
//
//                        } catch (
//                                JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast.makeText(PaymentActivity.this, "Something is fkn wrong", Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        HashMap<String, String> paramMap = new HashMap<String, String>();
//                        paramMap.put("MID", M_id);
//                        paramMap.put("ORDER_ID", order_id);
//                        paramMap.put("CUST_ID", customer_id);
//                        paramMap.put("CHANNEL_ID", "WAP");
//                        paramMap.put("TXN_AMOUNT", "200");
//                        paramMap.put("WEBSITE", "WEBSTAGING");
//                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
//                        paramMap.put("CALLBACK_URL", callBackUrl);
//
//                        return paramMap;
//                    }
//                };
//
//                requestQueue.add(stringRequest);

            }


        });


    }

    private void generateCheckSum() {

        String accessToken =sharedPref.getString("token", "");
        ApiService apiService = ApiClientFabCoding.getService();

        Call<ChecksumPaytm> call = apiService.getCustomerOrderAddPayment(accessToken,restaurantId, address, orderDetails);

        call.enqueue(new Callback<ChecksumPaytm>() {
            @Override
            public void onResponse(Call<ChecksumPaytm> call, Response<ChecksumPaytm> response) {
                Log.d(TAG, "onResponse: Payment Activity: " + response.body());

                ChecksumPaytm checksum = response.body();

                Log.d(TAG, "onResponse: Payment Activity: " + response.body());
                // when app is ready to publish use production service
                PaytmPGService service1paytm = PaytmPGService.getStagingService();
                // when app is ready to publish use production service
                // PaytmPGService  Service = PaytmPGService.getProductionService();

                //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
                HashMap<String, String> paramMap = new HashMap<String, String>();
                //these are mandatory parameters
                paramMap.put("MID", checksum.getMerchant_key()); //MID provided by paytm
                paramMap.put("ORDER_ID", checksum.getOrderId());
                paramMap.put("CUST_ID", checksum.getCustId());
                paramMap.put("TXN_AMOUNT", checksum.getTnxAmount());
                paramMap.put("CALLBACK_URL", checksum.getCall_back_url());
                paramMap.put("CHECKSUMHASH",checksum.getChecksumHash());

                paramMap.put("CHANNEL_ID", checksum.getChannel_id());//ne
               
                paramMap.put("WEBSITE",checksum.getWebsite());//ne
                paramMap.put("INDUSTRY_TYPE_ID", checksum.getIndustry_type_id());//ne


               // paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
                //paramMap.put( "MOBILE_NO" , "999999999");  // no need

                //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
                
                PaytmOrder Order = new PaytmOrder(paramMap);
                Log.d(TAG, "onResponse: Payment Activity: " + response.body());

                service1paytm.initialize(Order, null);

                // start payment service call here
                service1paytm.startPaymentTransaction(PaymentActivity.this, true, true,PaymentActivity.this);
                Log.d(TAG, "onResponse: Payment Activity: " + response.body());

            }

            @Override
            public void onFailure(Call<ChecksumPaytm> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteTray() {

        final AppDatabase db = AppDatabase.getAppDatabase(PaymentActivity.this);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                db.trayDao().deleteAll();
                return null;
            }
        }.execute();
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {

        String orderId = inResponse.getString("ORDERID");
        Toast.makeText(PaymentActivity.this,"orderid" + orderId, Toast.LENGTH_SHORT).show();


        ApiService apiService = ApiClientFabCoding.getService();
        String accessToken =sharedPref.getString("token", "");
        Call<TransactionStatus> call = apiService.checkTransactionStatus(orderId,accessToken,restaurantId, address, orderDetails);

        call.enqueue(new Callback<TransactionStatus>() {
            @Override
            public void onResponse(Call<TransactionStatus> call, Response<TransactionStatus> response) {

              ///  if(response.body()==null){

                 //   return;
                //}
                TransactionStatus status = response.body();
                Log.d("intt", "onTransactionResponse: ");


                Toast.makeText(PaymentActivity.this,"on payment" + status.getPaytStatus(), Toast.LENGTH_SHORT).show();


                    //showOrderStatus(false);

            }

            @Override
            public void onFailure(Call<TransactionStatus> call, Throwable t) {


            }
        });


       // Toast.makeText(PaymentActivity.this, "Success on payment", Toast.LENGTH_SHORT).show();
        Toast.makeText(PaymentActivity.this, "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "onTransactionResponse: ");





    }

    @Override
    public void networkNotAvailable() {
        Log.d(TAG, "networkNotAvailable: ");
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Log.d(TAG, "clientAuthenticationFailed: ");
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Log.d(TAG, "someUIErrorOccurred: ");
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {


        Log.d(TAG, "onErrorLoadingWebPage: ");
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.d(TAG, "onBackPressedCancelTransaction: ");
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Log.d(TAG, "onTransactionCancel: ");
    }
}
