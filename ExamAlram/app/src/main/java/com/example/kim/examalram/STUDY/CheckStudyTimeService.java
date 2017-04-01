package com.example.kim.examalram.STUDY;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.example.kim.examalram.TimeThread;
import java.util.ArrayList;


/**
 * Created by User on 2017-03-15.
 */

public class CheckStudyTimeService extends Service {
    final static String STUDY_ACTION = "STUDY_ACTION";
    final static String RUNNING = "RUNNING";
    TimeThread thread;


    int mSec,mMin,mHour;
    int count;//핸들러가 호출한 횟수를 누적할 count
    int mCount,tCount;//빼기 전용

    int bResult;
    String mResult;

    String mStatus = "READY";

    IntentFilter filter;
    PauseReceiver pauseRecevier;

    ;


    class CheckHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            Intent intent = new Intent();
            intent.setAction(STUDY_ACTION);

            count++; //핸들러 호출시마다 값 증가
            if (count == 100) {
                tCount = mCount++; //계산용
                count = 0;//카운트를 초기화 한다.
            }

            mSec = tCount % 60;
            mMin = tCount / 60 % 60;
            mHour = tCount / 3600;


            bResult = (mSec+mMin*60+mHour*60*60);


            mResult = String.format("%02d:%02d:%02d",mHour,mMin,mSec);
            ArrayList<Integer> mResult = new ArrayList<>();
            mResult.add(mSec);
            mResult.add(mMin);
            mResult.add(mHour);
            //시간끝나면
            intent.putIntegerArrayListExtra("STUDY_TIME",mResult);
            sendBroadcast(intent);
        }
    }


    IBinder mBinder = new MyBinder();

    class MyBinder extends Binder{

        CheckStudyTimeService getService(){
            return CheckStudyTimeService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pauseRecevier = new PauseReceiver();
        filter = new IntentFilter();
        filter.addAction(CheckStudyTimeActivity.PAUSE);
        registerReceiver(pauseRecevier,filter);
        Log.d("TAG","리시버 CREATE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = new TimeThread(new CheckHandler());
        thread.start();

        Log.d("쓰레드 상태1",thread.getState().toString());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("TAG","onUnbind 호출");
        return true;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.d("TAG","onRebind 호출");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        unregisterReceiver(pauseRecevier);
        thread.Stop(); //스레드 정지
        Log.d("TAG","SEVICE ONDESTROY 작동");

    }

    public class PauseReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("STATUS");
            Log.d("TAG","수신시 STATUS값"+status);
            switch (status){
                case "PAUSE":
                    thread.PauseResume(true);
                    Log.d("쓰레드 상태2",thread.getState().toString());
                    Log.d("TAG","스레드 일시정지");
                    break;
                case "RUNNING":
                    thread.PauseResume(false);
                    Log.d("쓰레드 상태3",thread.getState().toString());
                    Log.d("TAG","스레드 재시작");
                    mStatus = RUNNING;
                    break;
                case "RESUME":
                    thread.PauseResume(true);
                    Log.d("쓰레드 상태2",thread.getState().toString());
                    Log.d("TAG","스레드 일시정지");
                    break;
            }
        }
    }
}
