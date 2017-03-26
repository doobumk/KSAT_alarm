package com.example.kim.examalram.KSAT;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kim.examalram.R;
import com.example.kim.examalram.SETTING.Setting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KSATActivity extends Activity {
    Button korean;
    Button math;
    Button english;
    Button other;
    Button secondL;
    TextView Exam6;
    TextView Exam9;
    TextView Exam11;
    String juneExam = String.valueOf(juneDate());
    String sepExam = String.valueOf(sepDate());
    String ksatExam = String.valueOf(ksatDate());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ksat);

        korean = (Button) findViewById(R.id.korean);
        math = (Button) findViewById(R.id.math);
        english = (Button) findViewById(R.id.english);
        other = (Button) findViewById(R.id.others);
        secondL = (Button) findViewById(R.id.second);
        Exam6 = (TextView) findViewById(R.id.juneExam);
        Exam9 = (TextView)findViewById(R.id.sepExam);
        Exam11 = (TextView)findViewById(R.id.ksat);





        //국어영역
        korean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KSATActivity.this, KoreanActivity.class);
                startActivity(intent);
            }
        });
        //수학
        math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KSATActivity.this, MathActivity.class);
                startActivity(intent);
            }
        });
        //영어
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KSATActivity.this, EnglishActivity.class);
                startActivity(intent);
            }
        });
        //탐구
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KSATActivity.this, OtherActivity.class);
                startActivity(intent);
            }
        });
        //제2외
        secondL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KSATActivity.this, SecondLanguageActivity.class);
                startActivity(intent);
            }
        });

        Exam6.setText("6월 모평 "+"D-"+juneExam);
        Exam9.setText("9월 모평 "+"D-"+sepExam);
        Exam11.setText("수능 "+"D-"+ksatExam);
    }

    //여기서 수능시간 / 모의고사 시간 계산
    public long ksatDate(){
        SimpleDateFormat fm1 = new SimpleDateFormat("yyyy-MM-dd");
        String date = fm1.format(new Date());
        String ksatTime = "2017-11-17"; //수능
        long diffDays = 0;

            try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = formatter.parse(date);
            Date endDate = formatter.parse(ksatTime); //수능

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
        }catch(ParseException e) {e.printStackTrace();}
        return diffDays;
    }
    //6
    public long juneDate(){
        SimpleDateFormat fm1 = new SimpleDateFormat("yyyy-MM-dd");
        String date = fm1.format(new Date());
        String juneTime = "2017-06-01"; //6평 시험
        long diffDays6 = 0;

        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = formatter.parse(date);
            Date juneDate = formatter.parse(juneTime); //6평

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long test6 = juneDate.getTime() - beginDate.getTime();
            diffDays6 = test6 / (24 * 60 * 60 * 1000);
        }catch(ParseException e) {e.printStackTrace();}
        return diffDays6;
    }
    //9
    public long sepDate(){
        SimpleDateFormat fm1 = new SimpleDateFormat("yyyy-MM-dd");
        String date = fm1.format(new Date());
        String sepTime = "2017-09-06"; //9평 시험
        long diffDays9 = 0;

        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = formatter.parse(date);
            Date juneDate = formatter.parse(sepTime); //9평

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long test9 = juneDate.getTime() - beginDate.getTime();
            diffDays9 = test9 / (24 * 60 * 60 * 1000);
        }catch(ParseException e) {e.printStackTrace();}
        return diffDays9;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.bar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this,Setting.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}





