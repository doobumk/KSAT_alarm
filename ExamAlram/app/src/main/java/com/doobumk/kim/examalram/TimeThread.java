package com.doobumk.kim.examalram;

import android.os.Handler;

/**
 * Created by User on 2017-03-15.
 */

public class TimeThread extends Thread {
    Handler handler;

    boolean isRun = true;
    boolean isWait = false;

    public TimeThread(Handler handler) {
        this.handler = handler;
    }


    public void PauseResume(boolean isWait) { //일시정지 재시작
        synchronized (this) {
            this.isWait = isWait;
            notify();
        }
    }
    public void Stop() { //정지
        synchronized (this) {
            isRun = false;
        }
    }
    @Override
    public void run() {
        while (isRun == true) {
            try {
                //매주기 10/1000 초씩 쉰다.
                Thread.sleep(10);
            } catch (InterruptedException e) {}
            while (isWait == true) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(0);
        }
    }
}