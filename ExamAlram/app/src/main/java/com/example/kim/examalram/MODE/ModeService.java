package com.example.kim.examalram.MODE;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.kim.examalram.R;

/**
 * Created by User on 2017-03-27.
 */

public class ModeService extends Service{

    private View mView;
    private WindowManager mManager;

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        createView();
        return super.onStartCommand(intent, flags, startId);
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
            }
        });

    }

    public void destroyView(){
        Log.d("TAG","destroyView 호출됨");
        mManager.removeViewImmediate(mView);
    }

    @Override
    public void onDestroy() {
        destroyView();
        super.onDestroy();
    }
}
