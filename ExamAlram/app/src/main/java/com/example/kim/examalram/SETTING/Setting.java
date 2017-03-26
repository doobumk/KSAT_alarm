package com.example.kim.examalram.SETTING;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.kim.examalram.R;


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
