package com.example.babycrib;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
     * 2019.11.20 by mindi
     * 홈 fragment
     * 아기상태 조회, 블루투스 연결 등이 가능한 fragment 입니다.
 */
public class Home extends Fragment {
    ImageView stateimage, setBehaivor, reconnect, report;
    TextView statetext,sleepinfo1,sleepinfo2;
    String name;
    MainActivity mainActivity;

    String text;
    boolean ButtonVisible, report_visible;
    int imageID;

    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance() {
        Bundle args = new Bundle();
        Home fragment = new Home();

        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        name = "서준이가 ";
        text = name + "활동중이에요!";
        imageID =  R.drawable.toy;
        ButtonVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        stateimage = view.findViewById(R.id.stateimage);
        statetext = view.findViewById(R.id.statetext);
        sleepinfo1 = view.findViewById(R.id.sleepinfo1);
        sleepinfo2 = view.findViewById(R.id.sleepinfo2);

        report= view.findViewById(R.id.behaivor_report);

        reconnect = view.findViewById(R.id.reconnect);

        mainActivity = (MainActivity) getActivity();

        Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.babyanimation);
        stateimage.startAnimation(animation);

        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 수면 정보 설정
        Cursor cursor = db.rawQuery("SELECT AVG(SLEEPINGTIME) AS TIMEAVAREGE FROM SLEEPDIARY WHERE SEP='낮'", null);
        while (cursor.moveToNext()) {
            int amount = cursor.getInt(0);
            int hour = amount / 60;
            int minute = amount - (60 * hour);
            String s_time = hour + "시간 " + minute + "분";
            s_time = "<font color=\"#87CEEB\">" + s_time + "</font>";
            sleepinfo1.setText(Html.fromHtml("최근 낮 수면시간은 " + s_time + " 이며"));
        }
        // 낮잠
        cursor = db.rawQuery("SELECT AVG(SLEEPINGTIME) AS TIMEAVAREGE FROM SLEEPDIARY WHERE SEP='밤'", null);
        while(cursor.moveToNext()){
            int amount = cursor.getInt(0);
            int hour = amount / 60;
            int minute = amount - (60 * hour);
            String s_time = hour + "시간 " + minute + "분";
            s_time = "<font color=\"#87CEEB\">" + s_time + "</font>";
            sleepinfo2.setText(Html.fromHtml("밤 수면시간은 " + s_time + " 입니다."));
        }
        if (savedInstanceState != null) {
            text = savedInstanceState.getString("TEXT");
            imageID = savedInstanceState.getInt("IMAGE_ID");
            ButtonVisible = savedInstanceState.getBoolean("VISIBLE_STATE");
        }
        else {

        }

        // 블루투스 재연결
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.bluetoothconnect();
            }
        });
        // 아기 기상 후 행동패턴 기록
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.WakeupBehaivor();
                report.setVisibility(View.GONE);
            }
        });

        db.close();
        String str = mainActivity.sendState();

        return view;

    }
    public void invalidate(){

    }

    // UI 갱신 함수
    public void setState(String str) {
        if (str.equals("WAKEUP")) {
            text = name + "활동중이에요!";
            imageID = R.drawable.toy;
            setChange(text, imageID);
        } else if (str.equals("SLEEP")) {
            text = name + "잠들었어요!";
            imageID = R.drawable.moon;
            setChange(text, imageID);
        } else if (str.equals("DIAPER")) {
            text = name + "소변을 했어요!";
            imageID = R.drawable.diaper;
            setChange(text, imageID);
        }
    }

    // UI 갱신 함수
    public void setChange(String text, int imageID) {
        stateimage.setImageResource(imageID);
        statetext.setText(text);

        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT AVG(SLEEPINGTIME) AS TIMEAVAREGE FROM SLEEPDIARY WHERE SEP='낮'", null);
        while (cursor.moveToNext()) {
            int amount = cursor.getInt(0);
            int hour = amount / 60;
            int minute = amount - (60 * hour);
            String s_time = hour + "시간 " + minute + "분";
            s_time = "<font color=\"#87CEEB\">" + s_time + "</font>";
            sleepinfo1.setText(Html.fromHtml("최근 낮 수면시간은 " + s_time + " 이며"));
            //sleepinfo1.setText("최근 낮 수면시간은 " + s_time + "이며");
        }

        cursor = db.rawQuery("SELECT AVG(SLEEPINGTIME) AS TIMEAVAREGE FROM SLEEPDIARY WHERE SEP='밤'", null);
        while(cursor.moveToNext()){
            int amount = cursor.getInt(0);
            int hour = amount / 60;
            int minute = amount - (60 * hour);
            String s_time = hour + "시간 " + minute + "분";
            s_time = "<font color=\"#87CEEB\">" + s_time + "</font>";
            sleepinfo2.setText(Html.fromHtml("밤 수면시간은 " + s_time + " 입니다."));
        }
        if (mainActivity.report_visible){
            report.setVisibility(View.VISIBLE);
        }
        else{
            report.setVisibility(View.GONE);
        }

        db.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("IMAGE_ID", imageID);
        outState.putString("TEXT", text);
        outState.putBoolean("VISIBLE_STATE", ButtonVisible);
    }


    public void setButtonState(){
        ButtonVisible = !ButtonVisible;
        // 보이게
        if(ButtonVisible){
           // setBehaivor.setVisibility(setBehaivor.VISIBLE);
        }
        // 안보이게
        else{
           // setBehaivor.setVisibility(setBehaivor.INVISIBLE);
        }
    }

}
