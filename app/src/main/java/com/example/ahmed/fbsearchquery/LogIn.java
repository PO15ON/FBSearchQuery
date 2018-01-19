package com.example.ahmed.fbsearchquery;

import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

public class LogIn extends AppCompatActivity {

    LoginButton loginButton;
    public static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

            if (AccessToken.getCurrentAccessToken() != null) {

                Intent intent = new Intent(LogIn.this, MainActivity.class);
                startActivity(intent);
                Log.i(TAG, "onCreate: " + AccessToken.getCurrentAccessToken().getToken());
            }

        Log.i(TAG, "onCreate: View Created");

        CallbackManager callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                                Log.i(TAG, "onSuccess: " + loginResult.getAccessToken());
                                Toast.makeText(LogIn.this, "Successful Before", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LogIn.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(LogIn.this, "Successful After", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // App code
                                Log.i(TAG, "onCancel: ");
                                Toast.makeText(LogIn.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                                Log.i(TAG, "onError: ");
                                Toast.makeText(LogIn.this, "Error " + exception, Toast.LENGTH_SHORT).show();
            }
        });




    }

    CallbackManager callbackManager = CallbackManager.Factory.create();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        String accessToken = AccessToken.getCurrentAccessToken().getToken();

        Log.i(TAG, "onActivityResult: requestCode = " + requestCode + " resultCode = " + resultCode + " data = " + data + " access token = " + accessToken);

        if (accessToken != null) {

        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent);
        }


    }

}
