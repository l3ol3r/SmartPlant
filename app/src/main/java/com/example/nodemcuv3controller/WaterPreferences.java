package com.example.nodemcuv3controller;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WaterPreferences extends AppCompatActivity {

    private Button closeButt, sendButt;
    private Request request;
    private SharedPreferences pref;
    private OkHttpClient client;
    private TextView Hours, Minutes, value;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_pref);

        Hours = findViewById(R.id.Hours);
        Minutes = findViewById(R.id.Minutes);
        value = findViewById(R.id.value);
        client = new OkHttpClient();
        pref = getSharedPreferences("Mypref", MODE_PRIVATE);

        ButtonsSetup();
    }

    private void ButtonsSetup(){
        closeButt = findViewById(R.id.closeButtonWatPref);
        sendButt = findViewById(R.id.sendSchedule);

        closeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sendButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendSchedule();
            }
        });
    }

    private void SendSchedule(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sch_hours = Hours.getText().toString(), sch_minutes = Minutes.getText().toString();
                if (sch_hours.length() < 2){
                    sch_hours = "0" + sch_hours;
                }
                if (sch_minutes.length() < 2){
                    sch_minutes = "0" + sch_minutes;
                }
                request = new Request.Builder().url("http://" + pref.getString("ip", "").toString() + "/schedule?value=" + Integer.toString(Integer.valueOf(value.getText().toString()) * 6 + 100) + "&dates=" + pref.getString("Schedule", "") + "&time=" + sch_hours + sch_minutes).build();
                try {
                    Response response = client.newCall(request).execute();
                }
                catch (IOException e) {
                }
            }
        }).start();
    }
}
