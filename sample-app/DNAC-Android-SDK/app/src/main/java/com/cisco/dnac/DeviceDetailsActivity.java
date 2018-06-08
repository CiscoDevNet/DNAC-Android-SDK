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

package com.cisco.dnac;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.cisco.dnac.MainActivity.ip;
import static com.cisco.dnac.MainActivity.rawCookies;

/* THIS FILE IS RESPONSIBLE FOR DEVICE DETAILS SCREEN */

public class DeviceDetailsActivity extends Activity {
    public static final String REQUEST_TAG = "DeviceDetailsActivity";
    private TextView mTextView;
    int device_selected;
    String Networkdetails = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        mTextView = (TextView) findViewById(R.id.DeviceDetails);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                device_selected= 0;
            } else {
                device_selected= extras.getInt("device_selected");
            }
        } else {
            device_selected= (Integer) savedInstanceState.getSerializable("device_selected");
        }
        deviceDetails() ;

    }
    /* THREAD MECHANISM for fetching the device details */

    public void deviceDetails(){
        Thread deviceDetailsThread = new Thread() {
            public void run() {
                Log.e(REQUEST_TAG,"entering deviceDetails function ");
                DeviceDetails threadE = new DeviceDetails(getApplicationContext());

                try {
                    Networkdetails= threadE.execute().get(10, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(Networkdetails);
                    }
                });
            }
        };
        deviceDetailsThread.start();

    }

    /* This methods fetches the device details from DnacAccessClass method */
    public class DeviceDetails extends AsyncTask<Void, Void, String> {
        public Context mContext;

        public DeviceDetails(Context context) {
            mContext = context;
        }
        @Override
        protected String doInBackground(Void... params) {

            DnacAccessClass instance = DnacAccessClass.getInstance();
            instance.setDnacIPaddress("https://"+ip);
            instance.setCookie(rawCookies);
            return instance.getNetworkDeviceAllResponse_().get(device_selected).toString();
        }
    }



}
