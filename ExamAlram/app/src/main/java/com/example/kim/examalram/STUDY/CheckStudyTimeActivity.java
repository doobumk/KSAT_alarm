package com.example.kim.examalram.STUDY;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;


import android.provider.Settings;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kim.examalram.R;
import com.example.kim.examalram.SETTING.Setting;

import java.util.ArrayList;

public class CheckStudyTimeActivity extends Activity {
    final static String READY = "READY";
    final static String RUNNING = "RUNNING";
    final static String PAUSE = "PAUSE";

    Button start;
    Button temp;
    Button setStudyTime;
    TextView checkStudyTime;
    TextView checkStudyTime_only;
    TextView studyTimePercent;
    IntentFilter intentFilter;
    String result;
    String aResult,bResult,cResult,dResult;
    String mResult;
    int hour,minute,second,total;
    int mSec, mMin, mHour;
    int aSec, aMin, aHour;


    TimeBroadcastReceiver timeBroadcastReceiver;
    String mStatus = READY;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("SERVICE RUNNIG","TRUE");
                return true;

            }
        }
        Log.d("SERVICE RUNNIG","FALSE");
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG","액티비티 ONCREATE 호출");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_study_time);

        checkStudyTime = (TextView)findViewById(R.id.studytime_all);
        checkStudyTime_only = (TextView)findViewById(R.id.studytime_only);
        studyTimePercent = (TextView)findViewById(R.id.studyTimePercent) ;

        setStudyTime = (Button)findViewById(R.id.setStudyTime);
        start = (Button)findViewById(R.id.studytime_all_btn);
        temp = (Button)findViewById(R.id.temp);
        temp.setEnabled(false);
        timeBroadcastReceiver = new TimeBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(CheckStudyTimeService.STUDY_ACTION);
        registerReceiver(timeBroadcastReceiver,intentFilter);

        if(isMyServiceRunning(CheckStudyTimeService.class)==true){
            SharedPreferences preferences = getSharedPreferences("SaveState",0);
            mStatus = preferences.getString("status",mStatus);
            Log.d("TAG",mStatus);
            switch (mStatus){
                case RUNNING:
                start.setText("일시 정지");
                temp.setEnabled(false);
                    break;
                case PAUSE:
                    start.setText("다시 시작");
                    temp.setText("초기화");
                    temp.setEnabled(true);

            }
        }else mStatus = READY;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus){
                    case READY:
                        startTimeService();
                        mStatus=RUNNING;
                        setStudyTime.setEnabled(false);
                        start.setText("일시 정지");

                        break;
                    case RUNNING:
                        pauseTimeService();
                        mStatus=PAUSE;
                        start.setText("다시 시작");
                        temp.setEnabled(true);
                        temp.setText("초기화");
                        break;
                    case PAUSE:

                        mStatus=RUNNING;
                        resumeTimeService();
                        start.setText("일시 정지");
                        temp.setEnabled(false);
                        break;
                }
            }
        });
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus){
                    case PAUSE:
                        stopTimeService();
                        reset();
                        onDialogAlert();
                }
            }
        });

        setStudyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberDialog();

            }
        });
    }

    public void reset(){
        hour=0;
        minute=0;
        second=0;
        checkStudyTime.setText("00:00:00");
        checkStudyTime_only.setText("00:00:00");
        studyTimePercent.setText("0%");
        mStatus = READY;
        start.setText("타이머 시작");
        temp.setEnabled(false);
        setStudyTime.setEnabled(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG","ACTIVITY ONRESUME");
        SharedPreferences preferences = getSharedPreferences("SaveState",0);
        registerReceiver(timeBroadcastReceiver,intentFilter);
        second = preferences.getInt("second",second);
        minute = preferences.getInt("minute",minute);
        hour = preferences.getInt("hour",hour);
        if(mStatus == RUNNING){
            resumeTimeService();
            Log.d("TAG","555555555");
        }
        if(mStatus == PAUSE){
            Log.d("TAG","4444444444");
            cResult = preferences.getString("time",cResult);
            checkStudyTime.setText(cResult);
            resumeTimeService();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(timeBroadcastReceiver);
        SharedPreferences preferences = getSharedPreferences("SaveState",0);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("status",mStatus);
        edit.putString("time",cResult);
        edit.putInt("second",second);
        edit.putInt("minute",minute);
        edit.putInt("hour",hour);
        edit.commit();
        Log.d("TAG","unbind 실행");
        Log.d("TAG","ONPAUSE");
    }

    public void startTimeService(){
        Intent intent = new Intent(CheckStudyTimeActivity.this, CheckStudyTimeService.class);
        startService(intent);
    }

    public void pauseTimeService(){ //스레드정지
        Intent intent = new Intent();
        intent.setAction(PAUSE);
        intent.putExtra("STATUS",PAUSE);
        sendBroadcast(intent);
        Log.d("TAG","PAUSETIMESERVICE 호출");
    }

    public void resumeTimeService(){
        Intent intent2 = new Intent();
        intent2.setAction(PAUSE);
        Log.d("TAG","RESUMETIME SERVICE 실행될떄"+mStatus);
        if(mStatus == RUNNING){
            intent2.putExtra("STATUS",RUNNING);
            Log.d("TAG","resumTimeService thread 재시작");
        }else if(mStatus == PAUSE) {
            intent2.putExtra("STATUS", PAUSE);
            Log.d("TAG", "resumTimeService thread 재시작");
        }
        sendBroadcast(intent2);



    }

    public void stopTimeService(){
        Intent intent = new Intent(CheckStudyTimeActivity.this, CheckStudyTimeService.class);
        stopService(intent);
        Log.d("TAG","stopTimeService 호출됨");

    }

    private class TimeBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Integer> result = intent.getIntegerArrayListExtra("STUDY_TIME");
            mSec = result.get(0);
            mMin = result.get(1);
            mHour = result.get(2);
            if(second+minute+hour==0){
                bResult = String.format("%02d:%02d:%02d",mHour,mMin,mSec);
                checkStudyTime.setText(bResult);
                studyTimePercent.setText("");
                checkStudyTime_only.setText("");
                Toast.makeText(getApplicationContext(),"목표 공부시간이 입력되지않음",Toast.LENGTH_LONG).show();
            }
            if(second+minute+hour !=0){
                total = (second+minute*60+hour*60*60)-(mSec+mMin*60+mHour*60*60);
                aSec = total % 60;
                aMin = total / 60 % 60;
                aHour = total / 3600;
                bResult = String.format("%02d:%02d:%02d",mHour,mMin,mSec);
                cResult = String.format("%02d:%02d:%02d",aHour,aMin,aSec);
                dResult =  Integer.toString(100*(mSec+mMin*60+mHour*60*60)/(second+minute*60+hour*60*60));
                checkStudyTime.setText(bResult);
                studyTimePercent.setText(dResult +"%");
                checkStudyTime_only.setText(cResult);
            }


        }
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

    public void onDialogAlert(){
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(this);
        ab.setTitle("RESULT");
        ab.setMessage("공부시간 : " +bResult+ "\n'기록'을 누르면 기록됩니다.");
        ab.setPositiveButton("기록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        ab.setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        ab.show();
    }

    public void numberDialog(){
        final android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(CheckStudyTimeActivity.this);
        LayoutInflater inflater = CheckStudyTimeActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.number_picker,null);

        final NumberPicker p1 = (NumberPicker)view.findViewById(R.id.p1);
        final NumberPicker p2 = (NumberPicker)view.findViewById(R.id.p2);
        final NumberPicker p3 = (NumberPicker)view.findViewById(R.id.p3);

        p1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hour = newVal;
            }
        });

        p2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minute = newVal;
            }
        });

        p3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                second = newVal;
            }
        });

        ab.setView(view);
        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aResult = String.format("%02d:%02d:%02d",hour,minute,second);
                checkStudyTime_only.setText(aResult);
            }
        });
        ab.setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ab.setTitle("시간 설정");
        p1.setMinValue(0);
        p1.setMaxValue(23);
        p2.setMinValue(0);
        p2.setMaxValue(59);
        p3.setMinValue(0);
        p3.setMaxValue(59);
        ab.show();
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG","ACTIVITY onDestroy 호출");
        super.onDestroy();
    }

}
