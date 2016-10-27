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
