/**
* Copyright 2016 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.sample.isamandroid;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.challengehandler.GatewayChallengeHandler;

import java.util.HashMap;

/**
 * Created by pranab on 26/10/16.
 */

public class IsamChallegeHandler extends GatewayChallengeHandler {

    private static final String gatewayName = "LtpaBasedSSO";
    private static final String TAG = "IsamChallengeHandler";
    private static IsamChallegeHandler sharedInstance = new IsamChallegeHandler();
    private LocalBroadcastManager broadcastManager;

    public static IsamChallegeHandler getInstance() {
        return sharedInstance;
    }

    private IsamChallegeHandler() {
        super(gatewayName);
        broadcastManager = LocalBroadcastManager.getInstance(WLClient.getInstance().getContext());
    }

    @Override
    public boolean canHandleResponse(WLResponse wlResponse) {
        Log.d(TAG, "canHandleResponse");
        if(wlResponse != null && !wlResponse.getResponseText().isEmpty() &&
                wlResponse.getResponseText().contains("pkmslogin.form")) {
            return true;
        }
        return false;
    }

    @Override
    public void onSuccess(WLResponse wlResponse) {
        Log.d(TAG, "onSuccess");
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGIN_SUCCESS);
        broadcastManager.sendBroadcast(intent);
        if (!wlResponse.getResponseText().contains("logged out"))
            submitSuccess(wlResponse);
    }

    @Override
    public void onFailure(WLFailResponse wlFailResponse) {
        Log.d(TAG, "onFailure");
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGIN_FAILURE);
        broadcastManager.sendBroadcast(intent);
        Log.d(gatewayName, "handleFailure");
        super.cancel();
    }

    @Override
    public void handleChallenge(WLResponse wlResponse) {
        Log.d(TAG, "handleChallenge");
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGIN_REQUIRED);
        broadcastManager.sendBroadcast(intent);
    }

    public void submitLogin (final String username, final String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("login-form-type", "pwd");
        submitLoginForm("../../../../../../../pkmslogin.form?token=Unknown", params, null, 0, "post");
    }

    public void cancel () {
        super.cancel();
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGIN_SUCCESS);
        broadcastManager.sendBroadcast(intent);
    }

    public void submitLogout () {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", "");
        params.put("password", "");
        params.put("login-form-type", "pwd");
        submitLoginForm("../../../../../../../pkmslogout", params, null, 0, "post");
    }
}
