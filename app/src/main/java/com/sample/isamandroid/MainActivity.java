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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.worklight.wlclient.api.WLAccessTokenListener;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLLogoutResponseListener;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;
import com.worklight.wlclient.auth.AccessToken;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Button buttonGetData = null;
    private Button buttonLogout = null;

    private MainActivity _this = null;

    private static final String TAG = "MainActivity";
    private static boolean useObtain = false;
    private BroadcastReceiver loginRequiredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sample.isamandroid.R.layout.activity_main);

        _this = this;

        buttonGetData = (Button)findViewById(com.sample.isamandroid.R.id.getDataBtn);
        buttonLogout = (Button)findViewById(com.sample.isamandroid.R.id.logoutBtn);

        buttonGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (useObtain) {
                    WLAuthorizationManager.getInstance().obtainAccessToken("", new WLAccessTokenListener() {
                        @Override
                        public void onSuccess(AccessToken accessToken) {
                            Log.d(TAG, "Obtain onSuccess");
                            _this.showAlert("Success", "Obtain Success");
                        }

                        @Override
                        public void onFailure(WLFailResponse wlFailResponse) {
                            Log.d(TAG, "Obtain onFailure");
                            _this.showAlert("Failure", "Obtain failure");
                        }
                    });
                } else {
                    try {
                        URI adapterPath = new URI ("/adapters/ResourceAdapter/balance");
                        WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
                        request.send(new WLResponseListener() {
                            @Override
                            public void onSuccess(WLResponse wlResponse) {
                                _this.showAlert("Success", wlResponse.getResponseText());
                            }

                            @Override
                            public void onFailure(WLFailResponse wlFailResponse) {
                                _this.showAlert("Failure", wlFailResponse.getErrorMsg());
                            }
                        });
                    } catch (URISyntaxException use) {
                        use.printStackTrace();
                    }
                }
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WLAuthorizationManager.getInstance().logout("LtpaBasedSSO", new WLLogoutResponseListener() {
                    @Override
                    public void onSuccess() {
                        IsamChallegeHandler.getInstance().submitLogout();
                        showAlert("Logged out", "Logout success");
                    }

                    @Override
                    public void onFailure(WLFailResponse wlFailResponse) {
                        showAlert("Failure", "Failed to logout");
                    }
                });
            }
        });

        loginRequiredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showLoginScreen();
            }
        };
    }

    @Override
    protected void onStart () {
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(loginRequiredReceiver,
                        new IntentFilter(Constants.ACTION_LOGIN_REQUIRED));
    }

    @Override
    protected void onStop () {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginRequiredReceiver);
        super.onStop();
    }

    private void showLoginScreen () {
        Intent login = new Intent(_this, LoginActivity.class);
        _this.startActivity(login);
    }

    public void showAlert (final String title, final String message) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(_this)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }
}
