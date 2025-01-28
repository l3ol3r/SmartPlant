package com.example.nodemcuv3controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public Request request;
    OkHttpClient client;
    private SharedPreferences pref;
    private TextView ipPool, requestResult, waterValue;
    private NetworkSniffTask sniffTask;

    @Override
    protected void onStart() {
        getIp();
        super.onStart();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("Mypref", MODE_PRIVATE);

        ipPool = findViewById(R.id.ippool);
        requestResult = findViewById(R.id.requestresult);
        waterValue = findViewById(R.id.watervalue);
        client = new OkHttpClient();
        sniffTask = new NetworkSniffTask(getApplicationContext());


        ButtonsSetup();
        getIp();
    }

    private void ButtonsSetup(){
        Button SEND = findViewById(R.id.sendrequest);
        ImageButton AddDevice = findViewById(R.id.addButton);
        ImageButton ScheduleSettings = findViewById(R.id.scheduleSettings);

        SEND.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Water(Integer.getInteger(waterValue.getText().toString()));
                saveip(ipPool.getText().toString());
            }
        });
        AddDevice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddDeviceActivity.class));
            }
        });
        ScheduleSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.example.nodemcuv3controller.ScheduleSettings.class));
            }
        });
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
    private void getIp() {
        String ip = pref.getString("ip", "");
        if (!ip.isEmpty() & ip != null)
        {
            ipPool.setText(ip);
        }
    }
    private void Water(Integer value) {
        new Thread(new Runnable() {
            @Override
            public void run() { 
                request = new Request.Builder().url("http://" + ipPool.getText().toString() + "/water?=" + Integer.toString((Integer.valueOf(waterValue.getText().toString())) * 6 + 100)).build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String resultText = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                requestResult.setText(resultText);
                            }
                        });
                    }
                } catch (IOException e) {
                    System.out.println(e.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestResult.setText("Wrong IP or server doesn't answered");
                        }
                    });
                }
            }
        }).start();
    }
}