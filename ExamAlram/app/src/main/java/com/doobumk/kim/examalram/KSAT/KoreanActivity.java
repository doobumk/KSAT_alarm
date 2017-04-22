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

public class KoreanActivity extends Activity {
    SQLiteDatabase db;
    RecordTimeDBHelper recordTimeDBHelper;
    SharedPreferences sharedPreferences;
    MediaPlayer mp;

    BackPressCloser backPressCloser;

    TimeThread mThread;


    TextView ellapse;
    TextView recordTime;
    Button entire;
    Button record;
    Button literature;
    Button nonliterature;
    Button grammar;
    Button nonliteratureAll;
    Button literatureAll;


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
        setContentView(R.layout.activity_korean);

        mThread = new TimeThread(mTimer); //스레드 객체 생성

        ellapse = (TextView) findViewById(R.id.ellapse);
        recordTime = (TextView) findViewById(R.id.recordtext);
        entire = (Button) findViewById(R.id.entire);
        record = (Button) findViewById(R.id.record);
        record.setEnabled(false);
        nonliterature = (Button) findViewById(R.id.nliterature);
        literature = (Button) findViewById(R.id.literature);
        grammar = (Button) findViewById(R.id.grammar);
        nonliteratureAll = (Button) findViewById(R.id.nliteratureAll);
        literatureAll = (Button) findViewById(R.id.literatureAll);

        mp = MediaPlayer.create(this,R.raw.over);
        backPressCloser = new BackPressCloser(this);

        recordTimeDBHelper = new RecordTimeDBHelper(this);
        try{
            db = recordTimeDBHelper.getWritableDatabase();
        }catch(SQLiteException e){
            db = recordTimeDBHelper.getReadableDatabase();
        }

        //80분
        entire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nonliterature.setEnabled(false);
                literature.setEnabled(false);
                grammar.setEnabled(false);
                nonliteratureAll.setEnabled(false);
                literatureAll.setEnabled(false);

                flag = 0;

                switch (mStatus) {
                    case READY: //처음 화면 상태에서 스타트 버튼 누름
                        mThread.start();
                        entire.setText("일시 정지");
                        record.setEnabled(true);
                        mStatus = RUNNING;
                        break;
                    case RUNNING: //실행중일떄 버튼 누름 => 일시정지로 바뀜
                        mThread.PauseResume(true);
                        entire.setText("재시작");
                        record.setText("초기화");
                        ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;
                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        entire.setText("일시 정지");
                        record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;
                }

            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus) {
                    case RUNNING:
                        String mRecord = recordTime.getText().toString();
                        mRecord += String.format("%d=>%s\n", mSplitCount, getEllapse());
                        recordTime.setText(mRecord);
                        mSplitCount++; //기록 번호 순서
                        break;
                    case PAUSE:
                        onFinish();
                        break;
                }
            }
        });

        nonliterature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entire.setEnabled(false);
                literature.setEnabled(false);
                grammar.setEnabled(false);
                nonliteratureAll.setEnabled(false);
                literatureAll.setEnabled(false);
                flag = 1;
                switch (mStatus){
                    case READY:
                        mThread.start();
                        record.setEnabled(true);
                        nonliterature.setText("일시 정지");
                        mStatus = RUNNING;
                        break;

                    case RUNNING:
                        mThread.PauseResume(true);
                        nonliterature.setText("재시작");
                        record.setText("초기화");
                        ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;

                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        nonliterature.setText("일시 정지");
                        record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;

                }
            }
        });

        literature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entire.setEnabled(false);
                nonliterature.setEnabled(false);
                grammar.setEnabled(false);
                nonliteratureAll.setEnabled(false);
                literatureAll.setEnabled(false);
                flag = 2;
                switch (mStatus){
                    case READY:
                        mThread.start();
                        record.setEnabled(true);
                        literature.setText("일시 정지");
                        mStatus = RUNNING;
                        break;

                    case RUNNING:
                        mThread.PauseResume(true);
                        literature.setText("재시작");
                        record.setText("초기화");
                        ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;

                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        literature.setText("일시 정지");
                        record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;
                }
            }
        });



        nonliteratureAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entire.setEnabled(false);
                nonliterature.setEnabled(false);
                literature.setEnabled(false);
                grammar.setEnabled(false);
                literatureAll.setEnabled(false);
                flag = 4;
                switch (mStatus){
                    case READY:
                        mThread.start();
                        record.setEnabled(true);
                        nonliteratureAll.setText("일시 정지");
                        mStatus = RUNNING;
                        break;

                    case RUNNING:
                        mThread.PauseResume(true);
                        nonliteratureAll.setText("재시작");
                        record.setText("초기화");
                        ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;

                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        literature.setText("일시 정지");
                        record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;
                }
            }
        });

        literatureAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entire.setEnabled(false);
                nonliterature.setEnabled(false);
                literature.setEnabled(false);
                grammar.setEnabled(false);
                nonliteratureAll.setEnabled(false);
                flag = 4;

                switch (mStatus){
                    case READY:
                        mThread.start();
                        record.setEnabled(true);
                        literatureAll.setText("일시 정지");
                        mStatus = RUNNING;
                        break;

                    case RUNNING:
                        mThread.PauseResume(true);
                        literatureAll.setText("재시작");
                        record.setText("초기화");
                        ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;

                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        literatureAll.setText("일시 정지");
                        record.setText("중간 시간 기록");
                        mStatus = RUNNING;
                        break;
                }
            }
        });

        grammar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entire.setEnabled(false);
                nonliterature.setEnabled(false);
                literature.setEnabled(false);
                //grammar.setEnabled(false);
                nonliteratureAll.setEnabled(false);
                literatureAll.setEnabled(false);

                flag = 3;

                switch (mStatus){
                    case READY:
                        mThread.start();
                        record.setEnabled(true);
                        grammar.setText("일시 정지");
                        mStatus = RUNNING;

                        break;

                    case RUNNING:
                        mThread.PauseResume(true);
                        grammar.setText("재시작");
                        record.setText("초기화");
                        ellapse.setText(getEllapse());
                        mStatus = PAUSE;
                        break;

                    case PAUSE: //일시정지 상태일떄 다시 누르면
                        mThread.PauseResume(false);
                        mTimer.sendEmptyMessage(0);
                        grammar.setText("일시 정지");
                        record.setText("중간 시간 기록");
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
            String timeRecord = ellapse.getText().toString();
            String subject = "korean";
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
                    result = 4800 - tCount; //80분에서 뺀거(초) 4800
                }
                if (flag == 1) {
                    result = 300 - tCount;
                }
                if (flag == 2) {
                    result = 270 - tCount;
                }
                if (flag == 3) {
                    result = 1200 - tCount;
                }
                if (flag == 4) {
                    result = 1500 - tCount;
                } //비문학 문학 둘다 25분씩 겹침
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
            ellapse.setText(getEllapse());
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
        resetTime();
        recordTime.setText(""); // 초기화시 공백
        record.setEnabled(false);
        entire.setText("전체 문제(80분)");
        record.setText("중간 시간 기록");
        nonliterature.setText("비문학(5분)");
        literature.setText("문학(4분30초)");
        grammar.setText("화법/작문/어법(20분)");
        nonliteratureAll.setText("비문학(25분)");
        literatureAll.setText("문학(25분)");

        mStatus = READY;
        mSplitCount = 0;

        mThread.Stop(); //기존 스레드 정지
        mThread = new TimeThread(mTimer);

        entire.setEnabled(true);
        nonliterature.setEnabled(true);
        literature.setEnabled(true);
        grammar.setEnabled(true);
        nonliteratureAll.setEnabled(true);
        literatureAll.setEnabled(true);

    }
    //초기화 메소드
    public void resetTime(){
        ellapse.setText("00:00:00");
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





