/*
 * Copyright (c) 2018 Cisco and/or its affiliates.
 *
 * This software is licensed to you under the terms of the Cisco Sample
 * Code License, Version 1.0 (the "License"). You may obtain a copy of the
 * License at
 *
 *                https://developer.cisco.com/docs/licenses
 *
 * All use of the material herein must be in accordance with the terms of
 * the License. All rights not expressly granted by the License are
 * reserved. Unless required by applicable law or agreed to separately in
 * writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

/* THIS FILE IS RESPONSIBLE FOR MAIN ACTIVITY */

package com.cisco.dnac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MainActivity extends Activity {
    public static final String REQUEST_TAG = "MainActivity";
    private Button mButton;
    private com.android.volley.RequestQueue mQueue;
    public static String rawCookies;
    public  EditText username_t;
    public  EditText password_t;
    public EditText ip_address;
    public static String ip, username,password ;
    private ProgressDialog progressBar;
    public static Intent intent1;
    private String loginUrl = "/api/system/v1/identitymgmt/login";
    public static Switch sslSetting;
    public static boolean sslSettingInfo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);
        username_t = (EditText) findViewById(R.id.editText1);
        password_t = (EditText) findViewById(R.id.editText2);
        ip_address = (EditText)findViewById(R.id.editText3);
        sslSetting = (Switch) findViewById(R.id.switch1);
        sslSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sslSetting.isChecked()) {
                    Log.e(REQUEST_TAG,"sslsetting.isChecked is true");
                    sslSettingInfo = true;
                }
                else {
                    Log.e(REQUEST_TAG, "sslsetting.isChecked is false");
                    sslSettingInfo = false;
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
                startParsingTask();

            }
        });

    }
    private void showPopup() {
        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Authenticating ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    public void startParsingTask() {
        Log.e(REQUEST_TAG,"sslSettingInfo "+sslSettingInfo);
        //if(!sslSettingInfo){
            handleSSLHandshake(!sslSettingInfo);
        //}
        Thread LoginThreadResponse = new Thread() {
            public void run() {
                Login LoginThread = new Login(getApplicationContext());
                String response = null;
                try {
                    response = LoginThread.execute().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                final String loginresponse = response;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mTextView.setText("Response is: " + response);
                        if(loginresponse!=null) {
                            if (loginresponse.equals("success")) {
                                progressBar.dismiss();
                                intent1 = new Intent(MainActivity.this, DeviceCountActivity.class);
                                startActivity(intent1);
                            } else if (loginresponse.equals("com.android.volley.AuthFailureError")) {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            } else if (loginresponse.equals("com.android.volley.TimeoutError")) {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(), "Check your connection", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(), "SSl verification is true please turn it off", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };
        LoginThreadResponse.start();
    }

    /*

    Login Thread - It is to handle login request that is made using Android Native API's - The response could be delayed
    Hence it is handled in separate thread and not in main thread.
     */

    private class Login extends AsyncTask<Void, Void, String> {
        private Context mContext;

        public Login(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(Void... params) {


            final RequestFuture<String> futureRequest = RequestFuture.newFuture();
            mQueue = RequestQueue.getInstance(mContext.getApplicationContext())
                    .getRequestQueue();
            ip = ip_address.getText().toString();
            DnacAccessClass.getInstance().setDnacIPaddress(ip);
            username = username_t.getText().toString();
            DnacAccessClass.getInstance().setUsername(username);
            password = password_t.getText().toString();
            DnacAccessClass.getInstance().setPassword(password);
            final String url = "https://"+ip+loginUrl;
            StringRequest jsonRequest = new StringRequest(Request.Method
                    .GET, url, futureRequest, futureRequest) {

                @Override
                public HashMap<String, String> getHeaders() {
                    String credentials = username+":"+password;
                    String token = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }


                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    Map<String, String> responseHeaders = response.headers;
                    rawCookies = responseHeaders.get("Set-Cookie");
                    return super.parseNetworkResponse(response);
                }

            };

                jsonRequest.setTag(REQUEST_TAG);
                mQueue.add(jsonRequest);
            try {
                return futureRequest.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                Log.e(REQUEST_TAG,"Exception :"+e.getCause());
                return e.getCause().toString();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;

        }

    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake(final Boolean trust) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return trust;
                }
            });
        } catch (Exception ignored) {
        }
    }
}
