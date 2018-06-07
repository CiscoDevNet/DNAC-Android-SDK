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

import android.util.Base64;
import android.util.Log;


import java.util.List;

import cisco.com.dnac.v1.api.client.ApiInvoker;
import cisco.com.dnac.v1.api.*;
import api.NetworkDeviceApi;
import model.CountResult;
import model.NetworkDeviceListResult;
import model.NetworkDeviceListResultResponse;
import api.MiscApi;

public class DnacAccessClass {

    private String REQUEST_TAG ="DnacAccessClass";

    private String cookie = null;
    private String username = null;
    private String password = null;
    private String DnacIPaddress = null;
    private ApiInvoker apiInvoker = null;
    private MiscApi miscApi = null;
    private NetworkDeviceApi networkDeviceApi = null;
    private static DnacAccessClass instance = null;


    //Device Details parameters

    List<String> hostname = null;
    List<String> managementIpAddress = null;
    List<String> macAddress = null;
    List<String> locationName = null;
    List<String> serialNumber = null;
    List<String> location = null;
    List<String> family = null;
    List<String> type = null;
    List<String> series = null;
    List<String> collectionStatus = null;
    List<String> collectionInterval = null;
    List<String> notSyncedForMinutes = null;
    List<String> errorCode = null;
    List<String> errorDescription = null;
    List<String> softwareVersion = null;
    List<String> softwareType = null;
    List<String> platformId = null;
    List<String> role = null;
    List<String> reachabilityStatus = null;
    List<String> upTime = null;
    List<String> associatedWlcIp = null;
    List<String> licenseName = null;
    List<String> licenseType = null;
    List<String> licenseStatus = null;
    List<String> modulename = null;
    List<String> moduleequpimenttype = null;
    List<String> moduleservicestate = null;
    List<String> modulevendorequipmenttype = null;
    List<String> modulepartnumber = null;
    List<String> moduleoperationstatecode = null;
    String id = null;

    //


    public String getcookie()
    {
        return cookie;
    }

    public String getusername()
    {
        return username;
    }

    public String getPassword() { return password;}

    public String getDnacIPaddress() { return DnacIPaddress; }

    public void setCookie(String rawCookie) { cookie = rawCookie; }

    public void setUsername(String username_) { username = username_; }

    public void setDnacIPaddress(String IpAddress) { DnacIPaddress = IpAddress; }

    public void setPassword(String pwd) { password = pwd; }

    private DnacAccessClass() {

        Log.e(REQUEST_TAG,"DnacAccessClass constructor");
            miscApi = new MiscApi();
            networkDeviceApi = new NetworkDeviceApi();
    }

    public static DnacAccessClass getInstance() {
        if (instance == null) {
            synchronized(DnacAccessClass.class) {
                if (instance == null) {
                    instance = new DnacAccessClass();
                }
            }
        }
        return instance;

    }

    public Integer getNetworkDeviceCount_() {
        Integer count = 0;
        Log.e(REQUEST_TAG,"entering getNetworkDeviceCount_ function ");
        networkDeviceApi.addHeader("cookie", cookie);
        networkDeviceApi.setBasePath(DnacIPaddress);
        try {
            CountResult result =  networkDeviceApi.getNetworkDeviceCount();
            count = result.getResponse();
            Log.e(REQUEST_TAG,"result "+count);
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public String getAuthToken(){
        Log.e(REQUEST_TAG,"entering getAuthToken function ");
        String credentials = username+":"+password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        miscApi.setBasePath(DnacIPaddress);
        miscApi.addHeader("Authorization",auth);
        try {
            return miscApi.postAuthToken(null,auth).getToken();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<NetworkDeviceListResultResponse> getNetworkDeviceAllResponse_(){
        Log.e(REQUEST_TAG,"entering getDeviceList function ");
        networkDeviceApi.setBasePath(DnacIPaddress);
        networkDeviceApi.addHeader("cookie",cookie);
        try {
            return networkDeviceApi.getNetworkDevice(hostname, managementIpAddress, macAddress, locationName, serialNumber, location, family, type, series, collectionStatus, collectionInterval, notSyncedForMinutes, errorCode, errorDescription, softwareVersion, softwareType, platformId, role, reachabilityStatus, upTime, associatedWlcIp, licenseName, licenseType, licenseStatus, modulename, moduleequpimenttype, moduleservicestate, modulevendorequipmenttype, modulepartnumber, moduleoperationstatecode, id).getResponse();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String[] getListViewButtonDetails(){
        String[] ButtonDetails =null;
        List<NetworkDeviceListResultResponse>  networkDeviceAllResponseResponseList;
        networkDeviceAllResponseResponseList = getNetworkDeviceAllResponse_();
        Log.e(REQUEST_TAG, "entering getListviewButtonDetails function ");
        ButtonDetails =new String[networkDeviceAllResponseResponseList.size()];
        for (int i = 0; i < networkDeviceAllResponseResponseList.size(); i++) {
            ButtonDetails[i] = "MgmtIp - " + networkDeviceAllResponseResponseList.get(i).getManagementIpAddress() + "\n" + "Hostname - " + networkDeviceAllResponseResponseList.get(i).getHostname();
        }
        return ButtonDetails;

    }
}
