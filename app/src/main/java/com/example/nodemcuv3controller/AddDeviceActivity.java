package com.example.nodemcuv3controller;

import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddDeviceActivity extends AppCompatActivity {

    private SharedPreferences pref;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device);
        pref = getSharedPreferences("Mypref", MODE_PRIVATE);
        getWifiData();
        Button addButton = findViewById(R.id.adddevicebutt);
        Button closeButt = findViewById(R.id.closeButt_add);

        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWifiData();
                AddDevice();
            }
        });
        closeButt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void AddDevice(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextView wifiName = findViewById(R.id.wifiname), wifiPassword = findViewById(R.id.wifipassword);
                Request request = new Request.Builder().url("http://192.168.4.1/homeconnect?ssid=" + wifiName.getText().toString() + "&pass=" + wifiPassword.getText().toString()).build();
                Request ipcheck = new Request.Builder().url("http://192.168.4.1/ipcheck").build();
                OkHttpClient client = new OkHttpClient();
                for (int i = 0; i < 10; i ++) {
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            Thread.sleep(1000);
                            OkHttpClient client1 = new OkHttpClient();
                            Response response1 = client1.newCall(ipcheck).execute();
                            Thread.sleep(500);
                            if (response1.isSuccessful()) {
                                System.out.println("OK");
                                Response finalResponse = response1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            saveip(finalResponse.body().string());
                                            finish();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
                        Log.e("AddDeviceDebug", e.toString());
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                finish();
            }
        }).start();
    }

    private void saveip(String ip) {
        if (ip.isEmpty() | ip == null)
        {
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ip", ip);
        editor.apply();
    }

    private void saveWifiData(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("homeWifi_ssid", ((TextView) findViewById(R.id.wifiname)).getText().toString());
        editor.putString("homeWifi_password", ((TextView) findViewById(R.id.wifipassword)).getText().toString());
        editor.apply();
    }

    private void getWifiData(){
        ((TextView) findViewById(R.id.wifiname)).setText(pref.getString("homeWifi_ssid", ""));
        ((TextView) findViewById(R.id.wifipassword)).setText(pref.getString("homeWifi_password", ""));
    }
}
