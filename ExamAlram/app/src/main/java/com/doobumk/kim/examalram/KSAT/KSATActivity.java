package com.doobumk.kim.examalram.KSAT;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.doobumk.kim.examalram.R;
import com.doobumk.kim.examalram.SETTING.Setting;

public class KSATActivity extends Activity {
    Button korean;
    Button math;
    Button english;
    Button other;
    Button secondL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ksat);

        korean = (Button) findViewById(R.id.korean);
        math = (Button) findViewById(R.id.math);
        english = (Button) findViewById(R.id.english);
        other = (Button) findViewById(R.id.others);
        secondL = (Button) findViewById(R.id.second);


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

    }

    //여기서 수능시간 / 모의고사 시간 계산


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





