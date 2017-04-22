package com.doobumk.kim.examalram.CHART;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.doobumk.kim.examalram.ListViewAdapter;
import com.doobumk.kim.examalram.ListViewItem;
import com.doobumk.kim.examalram.R;
import com.doobumk.kim.examalram.RecordTimeDBHelper;

import java.util.ArrayList;

public class StaticActivity extends Activity {
    private  Button static_study,static_korean,static_math,static_others,static_second,static_english;
    SQLiteDatabase db;
    RecordTimeDBHelper recordTimeDBHelper;
    String flag;
    private ArrayList<ListViewItem> list = new ArrayList<>();
    public static ListView listView;
    ListViewAdapter listViewAdapter;
    String time,percent,subject,date;

    private Activity activity;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_static);
        activity = this;
        static_study = (Button)findViewById(R.id.static_studytime);
        static_korean = (Button)findViewById(R.id.static_korean);
        static_math = (Button)findViewById(R.id.static_math);
        static_others = (Button)findViewById(R.id.static_others);
        static_second = (Button)findViewById(R.id.static_second);
        static_english = (Button)findViewById(R.id.static_english);


        static_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "study_time";
                viewInflate();
            }
        });
        static_korean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "korean";
                viewInflate();
            }
        });
        static_math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "math";
                viewInflate();
            }
        });
        static_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "english";
                viewInflate();

            }
        });
        static_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "other";
                viewInflate();

            }
        });
        static_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "second_language";
                viewInflate();
            }
        });


    }

    public void getData(){
        recordTimeDBHelper = new RecordTimeDBHelper(this);
        db = recordTimeDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM timeRecord WHERE subject='"+flag+"';",null);
        if(cursor != null){
            int count = cursor.getCount();
            for(int i=0; i<count; i++){
                cursor.moveToNext();
                subject = cursor.getString(1);
                time = cursor.getString(2);
                percent = cursor.getString(3);
                date = cursor.getString(4);
                ListViewItem listViewItem = new ListViewItem(time,percent+"%",date);
                listViewAdapter = new ListViewAdapter(list);
                list.add(listViewItem);
            }
        }
    }

    public void viewInflate(){
        getData();
        view = activity.getLayoutInflater().inflate(R.layout.datalistview,null);
        listView = (ListView)view.findViewById(R.id.datalistview);
        listView.setAdapter(listViewAdapter);
        AlertDialog.Builder listViewDialog = new AlertDialog.Builder(activity);
        listViewDialog.setView(view);
        switch (flag){
            case "study_time":
                listViewDialog.setTitle("공부 시간 통계");
                break;
            case "korean":
                listViewDialog.setTitle("국어 통계");
                break;
            case "math":
                listViewDialog.setTitle("수학 통계");
                break;
            case "english":
                listViewDialog.setTitle("영어 통계");
                break;
            case "other":
                listViewDialog.setTitle("한국사/탐구영역 통계");
                break;
            case "second_language":
                listViewDialog.setTitle("제2외국어 통계");
                break;
        }
        listViewDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                list.clear();
            }
        });
        listViewDialog.show();
    }
}
