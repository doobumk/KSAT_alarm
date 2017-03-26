package com.example.kim.examalram;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.kim.examalram.CHART.DatabaseTest;
import com.example.kim.examalram.KSAT.KSATActivity;
import com.example.kim.examalram.SETTING.Setting;
import com.example.kim.examalram.STUDY.CheckStudyTimeActivity;

public class MainActivity extends Activity {
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE=6412;

    GridView gridView;
    String[] text = {"공부 시간","수능 영역별"
    ,"사용자","빡공모드","SETTING"};  //초기화면 텍스트

    int[] image={R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.a}; //초기화면 그림


    public void testPermission() { //권한얻어오기
        onDialogAlert();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                String a = Integer.toString(requestCode);
                Log.d("TAG",a);
            }else onDialogAlert();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG","onCreate 호출");

        if(!Settings.canDrawOverlays(this)){
            testPermission();
        }

        Log.d("TAG","권한체크 종료");

        ExamAdapter adapter = new ExamAdapter(MainActivity.this,text,image);
        gridView=(GridView)findViewById(R.id.grid);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,position + "번쨰 그림 선택",Toast.LENGTH_SHORT).show(); //여기서 다른 액티비티 연결

                if(position==0){  //공부 타이머

                    Intent intent = new Intent(MainActivity.this,CheckStudyTimeActivity.class);
                    startActivity(intent);
                }

                if(position==1){ //수능 타이머
                    Intent intent1 = new Intent(MainActivity.this,KSATActivity.class);
                    startActivity(intent1);
                }
                if(position==2){
                    Intent intent2 = new Intent(MainActivity.this, DatabaseTest.class);
                    startActivity(intent2);
                }

                if(position==4) {//setting
                    Intent intent4 = new Intent(MainActivity.this,Setting.class);
                    startActivity(intent4);
                }


            }
        });
    }



    public void onDialogAlert(){
        //  AlertDialog.Builder ab = new AlertDialog.Builder(this);
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(this);
        ab.setTitle("알림");
        ab.setMessage("앱 사용을 위해선 앱 위에 그리기 설정을 '허용'으로 바꿔주세요");
        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);

            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        ab.show();
    }

}
