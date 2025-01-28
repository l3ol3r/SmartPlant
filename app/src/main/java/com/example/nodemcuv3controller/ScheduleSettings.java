package com.example.nodemcuv3controller;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class ScheduleSettings extends AppCompatActivity {

    private SharedPreferences pref;
    private String chooseYear, chooseMonth, chooseDay = "##";
    private Button closeButt, addDateButt;
    private ImageButton deleteDateButt;
    private ArrayList<TextView> texts;
    public static int textInd = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_screen);

        pref = getSharedPreferences("Mypref", MODE_PRIVATE);
        ButtonsSetup();

        texts = new ArrayList<TextView>();

        CalendarView calendarView = findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                chooseDay = Integer.toString(mDay);
                chooseMonth = Integer.toString(mMonth + 1);
                chooseYear = Integer.toString(mYear);
                if (mDay < 10) {
                    chooseDay = "0" + chooseDay;
                }
                if (mMonth < 10) {
                    chooseMonth = "0" + chooseMonth;
                }

                ScrollView scrlView = findViewById(R.id.scheduleList);
                LinearLayout layout = scrlView.findViewById(R.id.scheduleLayout);

                if (texts.size() == textInd) {
                    texts.add(new TextView(getApplicationContext()));
                    texts.get(textInd).setText(chooseDay + "." + chooseMonth + "." + chooseYear);
                    texts.get(textInd).setTextSize(40);
                    texts.get(textInd).setGravity(0x11);
                    layout.addView(texts.get(textInd));
                }
                else{
                    texts.get(textInd).setVisibility(View.VISIBLE);
                    texts.get(textInd).setText(chooseDay + "." + chooseMonth + "." + chooseYear);
                    texts.get(textInd).setTextSize(40);
                    texts.get(textInd).setGravity(0x11);
                }
                textInd ++;
            }
        });
    }

    private void ButtonsSetup(){
        closeButt = findViewById(R.id.closeButt_sch);
        addDateButt = findViewById(R.id.addDate);
        deleteDateButt = findViewById(R.id.deleteDate);

        closeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addDateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDate();
            }
        });
        deleteDateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textInd > 0) {
                    textInd--;
                    texts.get(textInd).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void AddDate(){
        String schedule = "";
        for (int i = 0; i < textInd; i ++){
            String s = texts.get(i).getText().toString(), Date = "";
            for (int j = 0; j < s.length(); j ++){
                if (s.charAt(j) != '.'){
                    Date += s.charAt(j);
                }
            }
            for (int j = 0; j < 4; j ++){
                schedule += Date.charAt(j);
            }
            schedule += "_";
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Schedule", schedule);
        System.out.println(schedule);
        editor.apply();
        startActivity(new Intent(getApplicationContext(), WaterPreferences.class));
    }

}
