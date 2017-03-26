package com.example.kim.examalram.STUDY;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.kim.examalram.R;
import com.example.kim.examalram.TimeThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2017-03-15.
 */

public class CheckStudyTimeService extends Service {
    final static String STUDY_ACTION = "STUDY_ACTION";
    final static String RUNNING = "RUNNING";
    TimeThread thread;


    int tSec,tMin,tHour,mSec,mMin,mHour;
    int count,pCount; //핸들러가 호출한 횟수를 누적할 count
    int mCount,tCount,aCount; //빼기 전용

    int result,time,bResult;
    String mResult,aResult,cResult;

    String mStatus = "READY";


    IntentFilter filter;
    PauseReceiver pauseRecevier;

    private View mView;
    private WindowManager mManager;


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
        return true;
    }


    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        unregisterReceiver(pauseRecevier);
        thread.Stop(); //스레드 정지
        ///destroyView();
        Log.d("TAG","SEVICE ONDESTROY 작동");

    }

    /*public void createView(){
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.window_view, null);

        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
                );

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        mManager.addView(mView, mParams); //permission 필요
    }*/

    /*public void destroyView(){
        Log.d("TAG","destroyView 호출됨");
        mManager.removeViewImmediate(mView);
    }*/

    public class PauseReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("STATUS");
              String dResult = intent.getStringExtra("TIME");
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
