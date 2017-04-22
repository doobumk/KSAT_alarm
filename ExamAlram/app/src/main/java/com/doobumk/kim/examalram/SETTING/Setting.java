package com.doobumk.kim.examalram.SETTING;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class Setting extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        SettingFragment settingFragment = new SettingFragment();
        mFragmentTransaction.replace(android.R.id.content, settingFragment);
        mFragmentTransaction.commit();


    }


}
