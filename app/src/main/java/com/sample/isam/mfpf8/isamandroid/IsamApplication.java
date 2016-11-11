/*
 * Licensed Materials - Property of IBM
 * 5725-I43 (C) Copyright IBM Corp. 2016. All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 */
package com.sample.isam.mfpf8.isamandroid;

import android.app.Application;

import com.worklight.wlclient.api.WLClient;

/**
 * Created by pranab on 26/10/16.
 */

public class IsamApplication extends Application {
    @Override
    public void onCreate () {
        super.onCreate();
        WLClient client = WLClient.createInstance(this);
        client.registerChallengeHandler(IsamChallegeHandler.getInstance());
    }
}
