package com.example.babycrib;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.util.ArrayList;

/**
     * 2019.11.20 by mindi
     * 침대설정 fragment
     * 침대상태를 설정, 아기의 기상 후 행동패턴을 조회하는 fragment 입니다.
 */
public class Setting extends Fragment {

    // 설정값 변수
    private int SWING_SPEED /*스윙속도*/, SEAT_TEMPERATURE /*침대온도*/, BACK_SUPPORT /*등받이*/, HEIGHT /*높이*/;
    // 슬립모드 변수
    private int SLEEP_SWING, SLEEP_SEAT, SLEEP_BACK, SLEEP_HEIGHT;
    // 기상모드 변수
    private int WAKE_SWING, WAKE_SEAT, WAKE_BACK, WAKE_HEIGHT;
    // Seekbar 라이브러리 사용
    RangeSeekBar swing,seat, backsupport, height;
    // 기상 후 아기 행동패턴 pieChart
    PieChart pieChart;

    //MainAcitivity
    MainActivity mainActivityActivity;
    String tmp;

    ImageButton sleepmode, wakeupmode;
    ImageView refresh, enable;

    public Setting() {
        // Required empty public constructor
    }
    // viewpager fragment 생성 후 return
    public static Setting newInstance() {
        Bundle args = new Bundle();
        Setting fragment = new Setting();

        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DB에서 가져온 평균값 저장
        /* SLEEP_SWING = 1; SLEEP_SEAT = 3;  SLEEP_HEIGHT = 0; SLEEP_BACK = 0;
        WAKE_SWING = 0; WAKE_SEAT = 0;  WAKE_HEIGHT = 0; WAKE_BACK = 0;
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting, container, false);
        // 스윙속도
        swing = view.findViewById(R.id.swing);
        // 시트온도
        seat = view.findViewById(R.id.seat);
        // 높이
        height = view.findViewById(R.id.height);
        // 등받이
        backsupport = view.findViewById(R.id.backsupport);
        // 파이그래프 갱신
        refresh = view.findViewById(R.id.refresh);
        // 아기 기상후 행동패턴 출력 파이그래프
        pieChart = view.findViewById(R.id.piechart);

        enable = view.findViewById(R.id.enable);
        // 슬립모드
        sleepmode = view.findViewById(R.id.sleepmode);
        // 기상모드
        wakeupmode = view.findViewById(R.id.wakeupmode);

        // RangeSeekbar 초기화
        swing.setProgress(0, 4);
        swing.setRange(0, 3);
        swing.setIndicatorTextDecimalFormat("0");

        seat.setProgress(0, 4);
        seat.setRange(0, 3);
        seat.setIndicatorTextDecimalFormat("0");

        height.setProgress(0, 2);
        height.setRange(0, 1);
        height.setIndicatorTextDecimalFormat("0");

        backsupport.setProgress(0, 2);
        backsupport.setRange(0, 1);
        backsupport.setIndicatorTextDecimalFormat("0");

        // mainActivity 액티비티
        mainActivityActivity = (MainActivity) getActivity();

        // 이전상태 저장
        if(savedInstanceState != null){
            SWING_SPEED = savedInstanceState.getInt("SAVE_SWING");
            SEAT_TEMPERATURE = savedInstanceState.getInt("SAVE_SEAT");
            HEIGHT = savedInstanceState.getInt("SAVE_HEIGHT");
            BACK_SUPPORT = savedInstanceState.getInt("SAVE_BACK_SUPPORT");

            swing.setProgress((float) SWING_SPEED);
            seat.setProgress((float) SEAT_TEMPERATURE);
            height.setProgress((float) HEIGHT);
            backsupport.setProgress((float)BACK_SUPPORT);
        }

        // 파이차트 그리기
        drawPieChart();

        // 파이차트 갱신 리스너 등록
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation moonanimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.refresh_animation);
                refresh.startAnimation(moonanimation);
                // 그래프 갱신
                drawPieChart();
                pieChart.invalidate();
            }
        });
        enable.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mainActivityActivity.getBluetoothService().write("X");

                mainActivityActivity.setSetting_enable(true);
                enable.setVisibility(View.GONE);

            }
        });
        // 슬립모드 설정
        sleepmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swing.setProgress((float) SLEEP_SWING);
                seat.setProgress((float) SLEEP_SEAT);
                backsupport.setProgress((float) SLEEP_BACK);
                height.setProgress((float) SLEEP_HEIGHT);

//                swing.setEnabled(false);
//                seat.setEnabled(false);
//                backsupport.setEnabled(false);
//                height.setEnabled(false);

                SWING_SPEED = SLEEP_SWING;
                SEAT_TEMPERATURE = SLEEP_SEAT;
                HEIGHT = SLEEP_HEIGHT;
                BACK_SUPPORT = SLEEP_BACK;
                Toast.makeText(getActivity(),"슬립모드 단계설정 - 스윙: " + SLEEP_SWING + " 시트: " + SLEEP_SEAT+ " 등받이: " + SLEEP_BACK + " 높낮이: " + SLEEP_HEIGHT, Toast.LENGTH_SHORT).show();

//                send_SWING(SLEEP_SWING);
//                send_SEAT_TEMPERATURE(SLEEP_SEAT);
//                send_HEIGHT(HEIGHT);
                mainActivityActivity.setSWING_SPEED(SWING_SPEED);
                mainActivityActivity.setSEAT_TEMPERATURE(SEAT_TEMPERATURE);
                mainActivityActivity.setHEIGHT(HEIGHT);
                mainActivityActivity.setBACK_SUPPORT(BACK_SUPPORT);

                // 수동설정을 불가능하게 만든다
                mainActivityActivity.setSetting_enable(false);
                enable.setVisibility(View.VISIBLE);

                // HW로 'T'를 전송하여 슬립모드임을 알림
                mainActivityActivity.getBluetoothService().write("T");
            }
        });
        // 기상모드 설정
        wakeupmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swing.setProgress((float) WAKE_SWING);
                seat.setProgress((float) WAKE_SEAT);
                backsupport.setProgress((float) WAKE_BACK);
                height.setProgress((float) WAKE_HEIGHT);

//                swing.setEnabled(false);
//                seat.setEnabled(false);
//                backsupport.setEnabled(false);
//                height.setEnabled(false);

                SWING_SPEED = WAKE_SWING;
                SEAT_TEMPERATURE = WAKE_SEAT;
                BACK_SUPPORT = WAKE_BACK;
                HEIGHT = WAKE_HEIGHT;

                mainActivityActivity.setSWING_SPEED(SWING_SPEED);
                mainActivityActivity.setSEAT_TEMPERATURE(SEAT_TEMPERATURE);
                mainActivityActivity.setHEIGHT(HEIGHT);
                mainActivityActivity.setBACK_SUPPORT(BACK_SUPPORT);

                Toast.makeText(getActivity(),"기상모드 단계설정 - 스윙: " + WAKE_SWING + " 시트: " + WAKE_SEAT+ " 등받이: " + WAKE_BACK + " 높낮이: " + WAKE_HEIGHT, Toast.LENGTH_SHORT).show();

                // 수동설정을 불가능하게 만든다
                mainActivityActivity.setSetting_enable(false);
                enable.setVisibility(View.VISIBLE);

//                send_SWING(WAKE_SWING);
//                send_SEAT_TEMPERATURE(WAKE_SEAT);
//                send_BACK(BACK_SUPPORT);
//                send_HEIGHT(HEIGHT);

                // HW로 'W'를 전송하여 슬립모드임을 알림
                mainActivityActivity.getBluetoothService().write("W");

            }
        });


        // Seekbar 이벤트 리스너 등록
        // 1. 스윙조절
        swing.setOnRangeChangedListener(new OnRangeChangedListener() {
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue < 1) {
                    SWING_SPEED = 0;
                } else if (leftValue < 2) {
                    SWING_SPEED = 1;
                } else if (leftValue < 3) {
                    SWING_SPEED = 2;
                } else {
                    SWING_SPEED = 3;
                }
            }
            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                mainActivityActivity.setSWING_SPEED(SWING_SPEED);
                // 데이터 전송
                send_SWING(SWING_SPEED);
            }
        });

        // 시트온도 조절
        seat.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue < 1) {
                    SEAT_TEMPERATURE = 0;
                } else if (leftValue < 2) {
                    SEAT_TEMPERATURE = 1;
                } else if (leftValue < 3) {
                    SEAT_TEMPERATURE = 2;
                } else {
                    SEAT_TEMPERATURE = 3;
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                mainActivityActivity.setSEAT_TEMPERATURE(SEAT_TEMPERATURE);
                // 데이터 전송
                send_SEAT_TEMPERATURE(SEAT_TEMPERATURE);

            }
        });


        // 높낮이 조절
        height.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue < 1) {
                    HEIGHT = 0;

                } else {
                    HEIGHT = 1;
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                mainActivityActivity.setHEIGHT(HEIGHT);
                // 데이터 전송
                send_HEIGHT(HEIGHT);
            }
        });
        backsupport.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue < 1) {
                    BACK_SUPPORT = 0;

                } else {
                    BACK_SUPPORT = 1;
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                mainActivityActivity.setBACK_SUPPORT(BACK_SUPPORT);
                // 데이터 전송
                send_BACK(BACK_SUPPORT);
            }
        });

        return view;
    }

    // H/W 데이터 전송
    // 1. 스윙
    public void send_SWING(int data) {
        try {
            if (data == 0) {
                //mainActivityActivity.getBluetoothService().write("SWING_OFF");
                tmp = "D";
                mainActivityActivity.getBluetoothService().write("D");

            } else if (data == 1) {
                // mainActivityActivity.getBluetoothService().write("SWING_ONE");
                tmp = "E";
                mainActivityActivity.getBluetoothService().write("E");

            } else if (data == 2) {
                //mainActivityActivity.getBluetoothService().write("SWING_TWO");
                tmp = "F";
                mainActivityActivity.getBluetoothService().write("F");

            } else {
                //mainActivityActivity.getBluetoothService().write("SWING_THREE");
                tmp = "G";
                mainActivityActivity.getBluetoothService().write("G");
            }
            Toast.makeText(getActivity(),"사용자 단계설정 -  스윙: " + data + "단계 - H/W 전송: " + tmp, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "BLUETOOTH 연결이 되지 않았습니다. 잠시만 기다려주세요!", Toast.LENGTH_SHORT).show();
        }
    }

    // 2. 시트온도
    public void send_SEAT_TEMPERATURE(int data) {
        try {
            if (data == 0) {
                // mainActivityActivity.getBluetoothService().write("SEAT_OFF");
                tmp = "H";
                mainActivityActivity.getBluetoothService().write("H");

            } else if (data == 1) {
                //mainActivityActivity.getBluetoothService().write("SEAT_ONE");
                tmp = "I";
                mainActivityActivity.getBluetoothService().write("I");

            } else if (data == 2) {
                // mainActivityActivity.getBluetoothService().write("SEAT_TWO");
                tmp = "J";
                mainActivityActivity.getBluetoothService().write("J");

            } else {
                // mainActivityActivity.getBluetoothService().write("SEAT_THREE");
                tmp = "K";
                mainActivityActivity.getBluetoothService().write("K");
            }

            Toast.makeText(getActivity(),"사용자 단계설정 -  시트: " + data + "단계 - H/W 전송: " + tmp, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "BLUETOOTH 연결이 되지 않았습니다. 잠시만 기다려주세요!", Toast.LENGTH_SHORT).show();

        }
    }
    // 3. 등받이
    public void send_BACK(int data) {
        try {
            if (data == 0) {
                mainActivityActivity.getBluetoothService().write("L");
                tmp = "L";

            } else {
                mainActivityActivity.getBluetoothService().write("M");
                tmp = "M";

            }
            Toast.makeText(getActivity(),"사용자 단계설정 - 등받이: " + data + "단계 - H/W 전송: " + tmp, Toast.LENGTH_SHORT).show();

        } catch (NullPointerException e) {

        }
    }


    // 4. 높낮이
    public void send_HEIGHT(int data) {
        try {
            if (data == 0) {
                mainActivityActivity.getBluetoothService().write("O");
                tmp = "O";

            } else {
                mainActivityActivity.getBluetoothService().write("P");
                tmp = "P";

            }
            Toast.makeText(getActivity(),"사용자 단계설정 - 높낮이: " + data + "단계 - H/W 전송: " + tmp, Toast.LENGTH_SHORT).show();

        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "BLUETOOTH 연결이 되지 않았습니다. 잠시만 기다려주세요!", Toast.LENGTH_SHORT).show();

        }
    }
    public void onSaveInstanceState(Bundle outState){
        outState.putInt("SAVE_SWING", SWING_SPEED);
        outState.putInt("SAVE_SEAT", SEAT_TEMPERATURE);
        outState.putInt("SAVE_BACK", BACK_SUPPORT);
        outState.putInt("SAVE_HEIGHT", HEIGHT);
    }
    public void drawPieChart(){
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        // 데이터 갖고오기
        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM behaivor", null);
        while(cursor.moveToNext()){
            String name = cursor.getString(1).toString();
            int cnt = cursor.getInt(2);
            yValues.add(new PieEntry(cnt,name));
        }

        Description description = new Description();
        description.setText("기상 후 행동패턴"); //라벨
        description.setPosition(300,50);
        description.setTextSize(15);

        // description 설정
        pieChart.setDescription(description);
        //애니메이션
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(yValues,"Behaivors");

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        PieData data = new PieData((dataSet));
        dataSet.setVisible(true);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLUE
        );

        // x-value 삭제
        Legend l = pieChart.getLegend();
        l.setEnabled(false);

        pieChart.setData(data);

        db.close();
        // 갱신
        pieChart.invalidate();
    }
    public void setUI(int sw, int se, int he, int ba, boolean en){
        swing.setProgress((float) sw);
        seat.setProgress((float) se);
        height.setProgress((float) he);
        backsupport.setProgress((float)ba);
        if (en) {
            enable.setVisibility(View.GONE);
        }
        else{
            enable.setVisibility(View.VISIBLE);
        }
    }
}
