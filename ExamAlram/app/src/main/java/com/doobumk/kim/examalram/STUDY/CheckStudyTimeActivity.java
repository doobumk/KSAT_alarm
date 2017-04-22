package com.doobumk.kim.examalram.STUDY;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.doobumk.kim.examalram.R;
import com.doobumk.kim.examalram.RecordTimeDBHelper;
import com.doobumk.kim.examalram.SETTING.Setting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckStudyTimeActivity extends Activity {
    final static String READY = "READY";
    final static String RUNNING = "RUNNING";
    final static String PAUSE = "PAUSE";

    SQLiteDatabase db;
    RecordTimeDBHelper recordTimeDBHelper;

    Button start;
    Button temp;
    Button setStudyTime;
    TextView checkStudyTime;
    TextView checkStudyTime_only;
    TextView studyTimePercent;
    IntentFilter intentFilter;
    String aResult,bResult,cResult,dResult;
    int hour,minute,second,total,eResult;
    int mSec, mMin, mHour;
    int aSec, aMin, aHour;
    String percent;


    TimeBroadcastReceiver timeBroadcastReceiver;
    String mStatus = READY;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_check_study_time);

        recordTimeDBHelper = new RecordTimeDBHelper(this);
        try{
            db = recordTimeDBHelper.getWritableDatabase();
        }catch(SQLiteException e){
            db = recordTimeDBHelper.getReadableDatabase();
        }

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
                        if(second+minute+hour==0){
                            Toast.makeText(getApplicationContext(),"목표 공부시간이 입력되지않음",Toast.LENGTH_LONG).show();
                        }

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

        SharedPreferences preferences = getSharedPreferences("SaveState",0);
        registerReceiver(timeBroadcastReceiver,intentFilter);
        second = preferences.getInt("second",second);
        minute = preferences.getInt("minute",minute);
        hour = preferences.getInt("hour",hour);
        if(mStatus == RUNNING){
            resumeTimeService();
        }
        if(mStatus == PAUSE){
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
    }

    public void resumeTimeService(){
        Intent intent2 = new Intent();
        intent2.setAction(PAUSE);

        if(mStatus == RUNNING){
            intent2.putExtra("STATUS",RUNNING);

        }else if(mStatus == PAUSE) {
            intent2.putExtra("STATUS", PAUSE);

        }
        sendBroadcast(intent2);



    }

    public void stopTimeService(){
        Intent intent = new Intent(CheckStudyTimeActivity.this, CheckStudyTimeService.class);
        stopService(intent);
    }
    public void insert(){
            String timeRecord = checkStudyTime.getText().toString();
            String subject = "study_time";
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            long time = System.currentTimeMillis();
            String str = dayTime.format(new Date(time));
            percent = Integer.toString(eResult);
            db.execSQL("INSERT INTO timeRecord VALUES(null,'"+subject+"','"+timeRecord+"','"+percent+"','"+str+"');");
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

            }
            if(second+minute+hour !=0){
                total = (second+minute*60+hour*60*60)-(mSec+mMin*60+mHour*60*60);
                    aSec = total % 60;
                    aMin = total / 60 % 60;
                    aHour = total / 3600;
                    bResult = String.format("%02d:%02d:%02d", mHour, mMin, mSec);
                    cResult = String.format("%02d:%02d:%02d", aHour, aMin, aSec);
                    eResult =  100 * (mSec + mMin * 60 + mHour * 60 * 60) / (second + minute * 60 + hour * 60 * 60);
                    dResult = Integer.toString(eResult);
                    checkStudyTime.setText(bResult);
                studyTimePercent.setText(dResult + "%");
                if(total <=0){
                    checkStudyTime_only.setText("목표 달성");
                }
                if(total > 0) {
                    checkStudyTime_only.setText(cResult);
                }

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
                insert();

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
        super.onDestroy();
    }



}
