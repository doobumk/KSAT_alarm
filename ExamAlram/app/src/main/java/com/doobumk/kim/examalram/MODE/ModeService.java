package com.doobumk.kim.examalram.MODE;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.doobumk.kim.examalram.R;


/**
 * Created by User on 2017-03-27.
 */

public class ModeService extends Service{

    private View mView;
    private WindowManager mManager;
    PhoneStateListener phoneStateListener;

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        createView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        phoneStateListener = new PhoneStateCheckListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createView(){
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.window_view, null);

        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.RGB_565
        );

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        mManager.addView(mView, mParams); //permission 필요
        Button unlock = (Button)mView.findViewById(R.id.unlock);
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyView();
                onDestroy();
            }
        });

    }

    public void destroyView() {
        mManager.removeViewImmediate(mView);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class PhoneStateCheckListener extends PhoneStateListener{
        ModeService modeService;
        PhoneStateCheckListener(ModeService modeService){
            this.modeService = modeService;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(state == TelephonyManager.CALL_STATE_IDLE){

            }else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
                stopSelf();
            }else if(state == TelephonyManager.CALL_STATE_RINGING){
                stopSelf();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

}
