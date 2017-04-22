package com.doobumk.kim.examalram.SETTING;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.doobumk.kim.examalram.R;

/**
 * Created by User on 2017-03-17.
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting);

    }
}
