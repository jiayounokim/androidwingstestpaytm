package com.example.wingstestpaytm.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wingstestpaytm.ApiService;
import com.example.wingstestpaytm.ApiServiceBuilder;
import com.example.wingstestpaytm.JsonModelObject.ResponseBodyLogin;
import com.example.wingstestpaytm.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private Button customerButton, driverButton;
    private CallbackManager callbackManager;

    private SharedPreferences sharedPref;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        customerButton = findViewById(R.id.button_customer);
        driverButton = findViewById(R.id.button_driver);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                customerButton.setTextColor(getResources().getColor(android.R.color.white));

                driverButton.setBackgroundColor(getResources().getColor(android.R.color.white));
                driverButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                driverButton.setTextColor(getResources().getColor(android.R.color.white));

                customerButton.setBackgroundColor(getResources().getColor(android.R.color.white));
                customerButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccessToken.getCurrentAccessToken() == null) {
                    LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile", "email"));

                } else {
                    ColorDrawable customerbuttonColor = (ColorDrawable) customerButton.getBackground();
                    if (customerbuttonColor.getColor() == getResources().getColor(R.color.colorAccent)) {

                        loginToServer(AccessToken.getCurrentAccessToken().getToken(), "customer");


                    } else {

                        loginToServer(AccessToken.getCurrentAccessToken().getToken(), "driver");

                    }
                }
            }
        });


        callbackManager = CallbackManager.Factory.create();
        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);


        final Button buttonLogout = findViewById(R.id.button_logout);

        buttonLogout.setVisibility(View.GONE);


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("FACEBOOK TOKEN", loginResult.getAccessToken().getToken());
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Log.d("FACEBOOK DETAILS", object.toString());


                                        SharedPreferences.Editor editor = sharedPref.edit();

                                        try {
                                            editor.putString("name", object.getString("name"));
                                            editor.putString("email", object.getString("email"));
                                            editor.putString("avatar", object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();

                                        }

                                        editor.commit();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();


                        ColorDrawable customerbuttonColor = (ColorDrawable) customerButton.getBackground();
                        if (customerbuttonColor.getColor() == getResources().getColor(R.color.colorAccent)) {

                            loginToServer(AccessToken.getCurrentAccessToken().getToken(), "customer");


                        } else {

                            loginToServer(AccessToken.getCurrentAccessToken().getToken(), "driver");

                        }

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        if (AccessToken.getCurrentAccessToken() != null) {

            Log.d("USER", sharedPref.getAll().toString());
            buttonLogin.setText("Continue as " + sharedPref.getString("email", ""));
            buttonLogout.setVisibility(View.VISIBLE);

        }


        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                buttonLogin.setText("Login with Facebook");
                buttonLogout.setVisibility(View.GONE);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginToServer(String facebookAccessToken, final String userType) {

        buttonLogin.setText("LOADING...");
        buttonLogin.setClickable(false);
        buttonLogin.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        ApiService service = ApiServiceBuilder.getService();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("grant_type", "convert_token");
            jsonObject.put("client_id", getString(R.string.CLIENT_ID));
            jsonObject.put("client_secret", getString(R.string.CLIENT_SECRET));
            jsonObject.put("backend", "facebook");
            jsonObject.put("token", facebookAccessToken);
            jsonObject.put("user_type", userType);

            Log.d("Token Object", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Call<ResponseBodyLogin> call = service.facebookLogin(requestBody);
        call.enqueue(new Callback<ResponseBodyLogin>() {
            @Override
            public void onResponse(Call<ResponseBodyLogin> call, Response<ResponseBodyLogin> response) {
                Log.d("Token Object", response.toString());
                ResponseBodyLogin responseBodyLogin = response.body();

                SharedPreferences.Editor editor = sharedPref.edit();

                try {

                    editor.putString("token", responseBodyLogin.getAccessToken());
                    Log.d("loghere", "onResponse: accesstoken from response body login " + responseBodyLogin.getAccessToken());

                } catch (Exception e) {

                    e.printStackTrace();

                }

                editor.commit();

                if (userType.equals("customer")) {

                    Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                    startActivity(intent);
                } else {

                    Intent intent = new Intent(getApplicationContext(), DriverMainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBodyLogin> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Sorry! Somme Error Occured" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });


    }
}
