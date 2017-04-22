package com.doobumk.kim.examalram.KSAT;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.doobumk.kim.examalram.BackPressCloser;
import com.doobumk.kim.examalram.R;
import com.doobumk.kim.examalram.RecordTimeDBHelper;
import com.doobumk.kim.examalram.SETTING.Setting;
import com.doobumk.kim.examalram.TimeThread;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OtherActivity extends Activity {

    SQLiteDatabase db;
    RecordTimeDBHelper recordTimeDBHelper;
    SharedPreferences sharedPreferences;
    MediaPlayer mp;

    BackPressCloser backPressCloser;
    TimeThread mThread;

    TextView other_ellapse;
    TextView other_recordTime;
    Button other_entire;
    Button other_record;

    final int READY = 0;
    final int RUNNING = 1;
    final int PAUSE = 2;

    int tSec,tMin,tHour,mSec,mMin,mHour;
    int count; //핸들러가 호출한 횟수를 누적할 count
    int mCount,tCount; //빼기 전용
    int result;
    int flag = 0;

    long mSplitCount;
    int mStatus = READY;
    String mResult ;
    String percent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_other);

        mThread = new TimeThread(mTimer); //스레드 객체 생성

        other_ellapse = (TextView) findViewById(R.id.other_ellapse);
        other_recordTime = (TextView) findViewById(R.id.other_recordtext);
        other_entire = (Button) findViewById(R.id.other_entire);
        other_record = (Button) findViewById(R.id.other_record);
        other_record.setEnabled(false);


        mp = MediaPlayer.create(this,R.raw.over);
        backPressCloser = new BackPressCloser(this);

        recordTimeDBHelper = new RecordTimeDBHelper(this);
        try{
            db = recordTimeDBHelper.getWritableDatabase();
        }catch(SQLiteException e){
            db = recordTimeDBHelper.getReadableDatabase();
        }

        //80분
        other_entire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // entire.setEnabled(false);

                flag = 0;

                switch (mStatus) {
                    case READY: //처음 화면 상태에서 스타트 버튼 누름
                        mThread.start();
                        other_entire.setText("일시 정지");
                        other_record.setEnabled(true);
                        mStatus = RUNNING;
                        break;
                    case RUNNING: //실행중일떄 버튼 누름 => 일시정지로 바뀜
                        mThread.PauseResume(true);
                        other_entire.setText("재시작");
                        other_record.setText("초기화");
                        other_ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;
                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        other_entire.setText("일시 정지");
                        other_record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;
                }

            }
        });

        other_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus) {
                    case RUNNING:
                        String mRecord = other_recordTime.getText().toString();
                        mRecord += String.format("%d=>%s\n", mSplitCount, getEllapse());
                        other_recordTime.setText(mRecord);
                        mSplitCount++; //기록 번호 순서
                        break;
                    case PAUSE:
                        onFinish();
                        break;
                }
            }
        });


    }
    @Override
    public void onResume(){
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    //db에 넣기
    public void insert(){
        if(flag == 0) {
            String timeRecord = other_ellapse.getText().toString();
            String subject = "other";
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            long time = System.currentTimeMillis();
            String str = dayTime.format(new Date(time));

            db.execSQL("INSERT INTO timeRecord VALUES(null,'"+subject+"','"+timeRecord+"','"+percent+"','"+str+"');");
        }
    }

    public Handler mTimer = new Handler() {

        public void handleMessage(Message msg) {

            count++; //핸들러 호출시마다 값 증가
            if (count == 100) {
                tCount = mCount++; //계산용
                count = 0;//카운트를 초기화 한다.
            }
            if(msg.what==0) {
                if(flag == 0) {
                    result = 1800 - tCount; //100분에서 뺀거(초) 1800

                }
            }
            mSec = tCount % 60;
            mMin = tCount / 60 % 60;
            mHour = tCount / 3600;


            tSec = result % 60;
            tMin = result / 60 % 60;
            tHour = result / 3600;

            //시간끝나면
            if(result == 0){
                onFinish();
            }

            other_ellapse.setText(getEllapse());
        }
    };


    public void onDialogAlert(){
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(this);
        ab.setTitle("RESULT");
        if(flag == 0) {
            ab.setMessage("소요시간 : " + mHour + "시간 " + mMin + "분 " + mSec + "초" + "\n확인을 누르면 기록됩니다.");
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
        }
        else {
            ab.setMessage("소요시간 : " + mHour + "시간 " + mMin + "분 " + mSec + "초" + "\n");
            ab.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        ab.show();
    }
    //finish 메소드
    public void onFinish(){

        if(mStatus!=PAUSE) {
            percent = "100";
            if(sharedPreferences.getBoolean("vibrate",true)){
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
            }
            if(sharedPreferences.getBoolean("end",true)){
                mp.selectTrack(1);
                mp.start();
            }
        }
        onDialogAlert();
        percent = Integer.toString(tCount*100/1800);
        other_entire.setText("전체 문제(30분)");
        other_record.setText("중간 시간 기록");


        resetTime();

        mStatus = READY;
        mSplitCount = 0;
        other_ellapse.setText("00:00:00");
        other_recordTime.setText(""); // 초기화시 공백
        other_record.setEnabled(false);
        mThread.Stop(); //기존 스레드 정지
        mThread = new TimeThread(mTimer);
        other_entire.setEnabled(true);


    }
    //초기화 메소드
    public void resetTime(){
        count=0;
        tCount=0;
        mCount = 0;
        result = 0;
        mResult=null;
        tSec=0;
        tMin=0;
        tHour=0;

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        mThread.Stop();
        resetTime();
        super.onDestroy();
    }

    //경과시간 측정
    public String getEllapse() {
        mResult = String.format("%02d:%02d:%02d",tHour,tMin,tSec);
        return mResult;
    }

    @Override
    public void onBackPressed() {
        backPressCloser.onBackPressed();
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
