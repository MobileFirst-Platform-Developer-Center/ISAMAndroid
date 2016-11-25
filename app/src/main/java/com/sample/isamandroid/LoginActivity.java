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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameField, passwordField;
    private Button loginButton, cancelButton;

    private static final String TAG = "LoginActivity";
    private BroadcastReceiver loginSuccessReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sample.isamandroid.R.layout.activity_login);

        usernameField = (EditText)findViewById(com.sample.isamandroid.R.id.username);
        passwordField = (EditText)findViewById(com.sample.isamandroid.R.id.password);

        loginButton = (Button)findViewById(com.sample.isamandroid.R.id.login);
        cancelButton = (Button)findViewById(com.sample.isamandroid.R.id.cancel);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password =passwordField.getText().toString();

                IsamChallegeHandler.getInstance().submitLogin(username, password);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsamChallegeHandler.getInstance().cancel();
            }
        });

        loginSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("TAG", "loginSuccessReceived");
                finish();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(loginSuccessReceiver, new IntentFilter(Constants.ACTION_LOGIN_SUCCESS));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginSuccessReceiver);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        IsamChallegeHandler.getInstance().cancel();
    }
}
