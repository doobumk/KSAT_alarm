package com.example.kim.examalram;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kim on 2017-03-11.
 */

public class RecordTimeDBHelper extends SQLiteOpenHelper {
    public RecordTimeDBHelper(Context context){
        super(context,"ksat.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE timeRecord (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+"subject TEXT,content TEXT,time DATE)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS timeRecord");
        onCreate(db);
    }
}
