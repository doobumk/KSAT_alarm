package com.example.kim.examalram.CHART;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.kim.examalram.R;
import com.example.kim.examalram.RecordTimeDBHelper;

public class DatabaseTest extends Activity {
    SQLiteDatabase db;
    RecordTimeDBHelper recordTimeDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);

        recordTimeDBHelper = new RecordTimeDBHelper(this);
        db = recordTimeDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM timeRecord",null);
        startManagingCursor(cursor);
        String[] from = {"content","subject"};
        int[] to = {android.R.id.text1,android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor,from,to);
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
    }
}
