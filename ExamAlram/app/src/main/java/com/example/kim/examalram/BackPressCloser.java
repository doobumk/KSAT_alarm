package com.example.kim.examalram;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by User on 2017-03-17.
 */

public class BackPressCloser {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloser(Activity context) {
        this.activity = context;
    }
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis(); showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    } public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.\n *이 화면을 벗어나면 저장되지 않습니다.", Toast.LENGTH_SHORT);
        toast.show(); }


}
