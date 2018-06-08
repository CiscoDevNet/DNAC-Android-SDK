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

 /* THIS FILE IS RESPONSIBLE FOR LIST SCREEN THAT APPEARS AFTER LOGIN PAGE */

package com.cisco.dnac;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.cisco.dnac.MainActivity.ip;
import static com.cisco.dnac.MainActivity.password;
import static com.cisco.dnac.MainActivity.rawCookies;
import static com.cisco.dnac.MainActivity.sslSetting;
import static com.cisco.dnac.MainActivity.username;

public class DeviceCountActivity extends Activity {
    public static final String REQUEST_TAG = "DeviceCountActivity";
    private TextView mTextView, authdetails;
    public int devicecount ;
    private Button deviceCountButton,authTokenButton,deviceListButton;
    ListView listView;
    public String[] NetworkDevices =null;
    public  ArrayAdapter<String> adapter;
    public String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_count);

        //Network Device Count
        deviceCountButton = (Button) findViewById(R.id.NetworkDeviceCount);
        mTextView = (TextView) findViewById(R.id.DeviceCount);

        //AuthToken
        authTokenButton = (Button) findViewById(R.id.AuthToken) ;
        authdetails = (TextView) findViewById(R.id.AuthTokenResult);

        //Network Device List
        deviceListButton = (Button) findViewById(R.id.NetworkDeviceList);

        listView = (ListView) findViewById(R.id.listView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        deviceCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceCount();
            }
        });
        authTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthToken();
            }

        });

        deviceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceList();
            }
        });

    }

    /*
    This method places a request for fetching the token response from DNAC
     */

    public void AuthToken() {
        Thread AuthTokenThread = new Thread() {
            public void run() {
                //auth token
                AuthToken authtoken = new AuthToken(getApplicationContext());

                try {
                    authToken = authtoken.execute().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        authdetails.setText("Token : " + authToken);
                    }
                });
            }
        };
        AuthTokenThread.start();
    }

    /*
    This method places a request to get the number of devices present at present in the DNAC Context
     */


    public void DeviceCount() {
        Thread DeviceCountThread = new Thread() {
            public void run() {
                //network device count

                DeviceCount deviceCount = new DeviceCount(getApplicationContext());

                try {
                    devicecount = deviceCount.execute().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("Number of devices connected : " + devicecount);
                    }
                });
            }
        };
        DeviceCountThread.start();
    }

    /*
    This method places a request to retrieve the list of devices from DNAC
     */

    public void DeviceList(){
        Thread DeviceListThread = new Thread() {
            public void run() {
                DeviceList deviceList = new DeviceList(getApplicationContext());

                try {
                    NetworkDevices = deviceList.execute().get(10, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter = new ArrayAdapter<String>(DeviceCountActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1,
                        NetworkDevices);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getApplicationContext(), "Device Selected:" + position, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DeviceCountActivity.this, DeviceDetailsActivity.class);
                                intent.putExtra("device_selected", position);
                                startActivity(intent);

                            }
                        });
                    }
                });
            }
        };
        DeviceListThread.start();
    }

    /*
    This method places a request to get the number of devices present at present in DNAC - This again requests
    the method exposed by DnacAccessClass

     */

    public class DeviceCount extends AsyncTask<Void, Void,Integer> {
        public Context mContext;

        public DeviceCount(Context context) {
            mContext = context;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            DnacAccessClass instance = DnacAccessClass.getInstance();
            instance.setDnacIPaddress("https://"+ip);
            instance.setCookie(rawCookies);
            Integer count = instance.getNetworkDeviceCount_();
            return count;
        }
    }

    /*
    This method places a request for fetching the token response from DNAC - This again requests
    the method exposed by DnacAccessClass
     */
    public class AuthToken extends AsyncTask<Void, Void, String> {
        public Context mContext;

        public AuthToken(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            DnacAccessClass instance = DnacAccessClass.getInstance();
            instance.setDnacIPaddress("https://"+ip);
            instance.setUsername(username);
            instance.setPassword(password);
            return instance.getAuthToken();
        }
    }

    /*
    This method places a request to retrieve the list of devices from DNAC - his again requests
    the method exposed by DnacAccessClass
     */
    public class DeviceList extends AsyncTask<Void, Void, String[]> {
        public Context mContext;

        public DeviceList(Context context) {
            mContext = context;
        }
        @Override
        protected String[] doInBackground(Void... params) {

            DnacAccessClass instance = DnacAccessClass.getInstance();
            instance.setDnacIPaddress("https://"+ip);
            instance.setCookie(rawCookies);
            return instance.getListViewButtonDetails();
        }
    }

}
