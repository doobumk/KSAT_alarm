package com.example.kim.examalram.KSAT;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kim.examalram.BackPressCloser;
import com.example.kim.examalram.R;
import com.example.kim.examalram.RecordTimeDBHelper;
import com.example.kim.examalram.SETTING.Setting;
import com.example.kim.examalram.TimeThread;

public class EnglishActivity extends Activity {

    SQLiteDatabase db;
    RecordTimeDBHelper recordTimeDBHelper;
    SharedPreferences sharedPreferences;
    MediaPlayer mp;

    BackPressCloser backPressCloser;

    TimeThread mThread;


    TextView english_ellapse;
    TextView english_recordTime;
    Button english_entire;
    Button  english_record;
    Button english_reading;
    Button english_listen;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english);

        mThread = new TimeThread(mTimer); //스레드 객체 생성

        english_ellapse = (TextView) findViewById(R.id.english_ellapse);
        english_recordTime = (TextView) findViewById(R.id.english_recordtext);
        english_entire = (Button) findViewById(R.id.english_entire);
        english_record = (Button) findViewById(R.id.english_record);
        english_record.setEnabled(false);
        english_reading = (Button) findViewById(R.id.english_reading);
        english_listen = (Button) findViewById(R.id.english_listen);

        mp = MediaPlayer.create(this,R.raw.over);
        backPressCloser = new BackPressCloser(this);

        recordTimeDBHelper = new RecordTimeDBHelper(this);
        try{
            db = recordTimeDBHelper.getWritableDatabase();
        }catch(SQLiteException e){
            db = recordTimeDBHelper.getReadableDatabase();
        }

        //80분
        english_entire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // entire.setEnabled(false);
                english_listen.setEnabled(false);
                english_reading.setEnabled(false);
                flag = 0;

                switch (mStatus) {
                    case READY: //처음 화면 상태에서 스타트 버튼 누름
                        mThread.start();
                        english_entire.setText("일시 정지");
                        english_record.setEnabled(true);
                        mStatus = RUNNING;
                        break;
                    case RUNNING: //실행중일떄 버튼 누름 => 일시정지로 바뀜
                        mThread.PauseResume(true);
                        english_entire.setText("재시작");
                        english_record.setText("초기화");
                        english_ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;
                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        english_entire.setText("일시 정지");
                        english_record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;
                }

            }
        });

        english_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus) {
                    case RUNNING:
                        String mRecord = english_recordTime.getText().toString();
                        mRecord += String.format("%d=>%s\n", mSplitCount, getEllapse());
                        english_recordTime.setText(mRecord);
                        mSplitCount++; //기록 번호 순서
                        break;
                    case PAUSE:
                        onFinish();
                        break;
                }
            }
        });

        english_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                english_entire.setEnabled(false);
                //nonliterature.setEnabled(false);
                english_reading.setEnabled(false);

                flag = 1;

                switch (mStatus){
                    case READY:
                        mThread.start();
                        english_record.setEnabled(true);
                        english_listen.setText("일시 정지");
                        mStatus = RUNNING;
                        break;

                    case RUNNING:
                        mThread.PauseResume(true);
                        english_listen.setText("재시작");
                        english_record.setText("초기화");
                        english_ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;

                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        english_listen.setText("일시 정지");
                        english_record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;

                }
            }
        });

        english_reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                english_entire.setEnabled(false);
                english_listen.setEnabled(false);

                flag = 2;

                switch (mStatus){
                    case READY:
                        mThread.start();
                        english_record.setEnabled(true);
                        english_reading.setText("일시 정지");
                        mStatus = RUNNING;
                        break;

                    case RUNNING:
                        mThread.PauseResume(true);
                        english_reading.setText("재시작");
                        english_record.setText("초기화");
                        english_ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;

                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        english_reading.setText("일시 정지");
                        english_record.setText("중간 시간 기록");
                        mStatus = RUNNING;
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
            String timeRecord = english_ellapse.getText().toString();
            String subject = "english";
            db.execSQL("INSERT INTO timeRecord VALUES(null,'"+subject+"','" +timeRecord+"',null)");
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
                    result = 4200 - tCount; //70분에서 뺀거(초) 4800
                }
                if (flag == 1) { //listening 25분 25*60
                    result = 1500 - tCount;
                }
                if (flag == 2) { //reading 45
                    result = 2700 - tCount;
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

            english_ellapse.setText(getEllapse());
        }
    };


    public void onDialogAlert(){
        //  AlertDialog.Builder ab = new AlertDialog.Builder(this);
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(this);
        ab.setTitle("RESULT");
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

        ab.show();
    }
    //finish 메소드
    public void onFinish(){

        if(mStatus!=PAUSE) {
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

        english_entire.setText("전체 문제(70분)");
        english_record.setText("중간 시간 기록");
        english_listen.setText("듣기(25분)");
        english_reading.setText("문법/독해(45분)");


        resetTime();

        mStatus = READY;
        mSplitCount = 0;
        english_ellapse.setText("00:00:00");
        english_recordTime.setText(""); // 초기화시 공백
        english_record.setEnabled(false);

        mThread.Stop(); //기존 스레드 정지
        mThread = new TimeThread(mTimer);


        english_entire.setEnabled(true);
        english_listen.setEnabled(true);
        english_reading.setEnabled(true);


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
