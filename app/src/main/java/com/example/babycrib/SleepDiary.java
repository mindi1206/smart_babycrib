package com.example.babycrib;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.util.ArrayList;
import java.util.Calendar;


/**
     * 2019.11.20 by mindi
     * 수면일기 fragment
     * 아기의 수면기록을 조회하는 fragment 입니다.
 */
public class SleepDiary extends Fragment {

    ArrayList<String> xlabels = new ArrayList<>(); // 그래프의 x축 (y축은 onCreateView에)
    ArrayList<String> textDate = new ArrayList<>(); // 날짜 ArrayList
    ArrayList<String> textTime = new ArrayList<>(); // 수면시간 ArrayList

    TextView startt, endt, sleept;
    boolean is_sleeping = false;
    String WAKE_TIME = "" /*기상시각*/, WAKE_DATE = ""/*기상일자*/, SLEEP_TIME = ""/*수면시각*/, SLEEP_DATE = ""/*수면일자*/,
            START_DATE = ""/*기록조회 시작일자*/, END_DATE = ""/*기록조회 끝일자*/;
    float SLEEPING_TIME /*수면시간*/;
    String year, day, month, date_format;
    TextView start, end, sleepdate, sleeptime, discription;
    ImageButton search, daybutton, nightbutton;
    LinearLayout date_container, time_container;
    ImageView preview;

    int xoffset;
    EditText edit_sleeptime;
    SleepListAdapter sleepListAdapter;
    boolean moon;

    Cursor cursor /*데이터베이스 cursor*/, listcursor/*listcursor*/;
    long start_t, end_t;

    public SleepDiary() {
        // Required empty public constructor
    }

    // viewpager fragment 생성 후 return
    public static SleepDiary newInstance() {
        Bundle args = new Bundle();
        SleepDiary fragment = new SleepDiary();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.sleep_diary, container, false);
        moon = true;

        // ID initialize
        daybutton =v.findViewById(R.id.daybutton);
        nightbutton = v.findViewById(R.id.nightbutton);

        date_container = v.findViewById(R.id.date_container);
        time_container = v.findViewById(R.id.time_container);
        discription = v.findViewById(R.id.discription);
        preview = v.findViewById(R.id.preview);

        sleepdate = v.findViewById(R.id.sleepdate);
        sleeptime = v.findViewById(R.id.sleeptime);
        start = v.findViewById(R.id.start);
        end = v.findViewById(R.id.end);
        startt = v.findViewById(R.id.startt);
        endt = v.findViewById(R.id.endt);
        search = v.findViewById(R.id.search);
        sleept = v.findViewById(R.id.sleept);

        // 아기의 하루 수면기록 출력 ListView
        ListView list = v.findViewById(R.id.daysleep);
        sleepListAdapter = new SleepListAdapter(getActivity());
        list.setAdapter(sleepListAdapter);

        // 수면 그래프
        final LineChart lineChart = v.findViewById(R.id.daygraph_chart);
        lineChart.setNoDataTextColor(Color.WHITE);

        // 그래프 y값 ArrayList
        final ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> num = new ArrayList<>();


        // 밤 수면시간 조회 버튼 리스너 등록
        nightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 낮 수면시간 조회 -> 밤 수면시간 조회
                if(!moon){
                    lineChart.clear();
                }
                // 현재 밤 수면시각 조회중 으로 변경
                moon = true;
            }
        });
        // 낮 수면시간 조회 버튼 리스너 등록
        daybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 밤 수면시간 조회 -> 낮 수면시간 조회
                if(moon){
                    // 그래프 초기화
                    lineChart.clear();
                }
                // 모드 변경
                moon = false;
            }
        });

        // 수면기록조회 시작일자 TextView 리스너 등록
        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        final Calendar c = Calendar.getInstance();
                        // DatePicketDialog 쓸 때 필요함
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getLayoutInflater().getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                // 월
                                m += 1;
                                if (m < 10) {
                                    // 월 범위가 1~9 일 경우
                                    // 0을 추가해 01, 02 등으로 표기
                                    month = "0" + m;
                                } else {
                                    month = "" + m;
                                }
                                // 일
                                if (d < 10) {
                                    // 일 범위가 1~9일 경우
                                    // 0을 추가해 01, 02 등으로 표기
                                    day = "0" + d;
                                } else {
                                    day = "" + d;
                                }
                                date_format = month + "/" + day;

                                // 문자열 생성
                                START_DATE = y + "-" + month + "-" + day;
                                // 생성한 문자열로  기록조회 시작일자 TextView 초기화
                                start.setText(START_DATE);
                            }
                        }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });

        // 수면기록조회 끝일자 TextView 리스너 등록
        end.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getLayoutInflater().getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                // 월
                                m += 1;
                                if (m < 10) {
                                    // 월 범위가 1~9 일 경우
                                    // 0을 추가해 01, 02 등으로 표기
                                    month = "0" + m;
                                } else {
                                    month = "" + m;
                                }
                                // 일
                                if (d < 10) {
                                    // 일 범위가 1~9 일 경우
                                    // 0을 추가해 01, 02 등으로 표기
                                    day = "0" + d;
                                } else {
                                    day = "" + d;
                                }
                                date_format = month + "/" + day;

                                // 문자열 생성
                                END_DATE = y + "-" + month + "-" + day;
                                // 생성한 문자열로 기록조회 끝일자 TextView 초기화
                                end.setText(END_DATE);
                            }
                        }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });

        // 수면 기록 조회 버튼 리스너 등록
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor;
                preview.setVisibility(View.GONE);
                lineChart.setVisibility(View.VISIBLE);

                // 데이터베이스 서치 시작
                if (!START_DATE.equals("") && !END_DATE.equals("")) {
                    DBHelper dbHelper = new DBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    // 밤잠
                    if(moon){
                        // 쿼리문 작성
                        cursor = db.rawQuery("SELECT SLEEPDATE, AVG(SLEEPINGTIME) AS TIMEAVAREGE FROM SLEEPDIARY WHERE SEP='밤' AND SLEEPDATE BETWEEN '"
                                + START_DATE + "' AND '" + END_DATE +
                                "' GROUP BY SLEEPDATE ORDER BY SLEEPDATE ASC", null);
                    }
                    // 낮잠
                    else{
                        // 쿼리문 작성
                        cursor = db.rawQuery("SELECT SLEEPDATE, AVG(SLEEPINGTIME) AS TIMEAVAREGE FROM SLEEPDIARY WHERE SEP='낮' AND SLEEPDATE BETWEEN '"
                                + START_DATE + "' AND '" + END_DATE +
                                "' GROUP BY SLEEPDATE ORDER BY SLEEPDATE ASC", null);
                    }

                    // 데이터가 하나도 없다면
                    if (cursor.getCount() == 0) {
                        Toast.makeText(getActivity(), "설정한 기간의 데이터가 없습니다", Toast.LENGTH_SHORT).show();
                    }
                    // 아니면 그래프 출력
                    else {
                        int index = 0;
                        // 그래
                        xlabels.clear(); // 그래프 x좌표
                        entries.clear(); // 그래프 y좌표
                        textTime.clear(); // 날짜
                        textDate.clear(); // 수면시간

                        while (cursor.moveToNext()) {
                            // 그래프 x좌표(일자 2019-10-28) 삽입
                            xlabels.add(cursor.getString(0));
                            // 그래프 y좌표
                            // y좌표 표현상의 문제(그래프 이기 때문에 숫자값이어야함) 때문에
                            // 데이터베이스에 분으로 저장하고, 60으로 나눈 값을 float형으로 출력
                            float time = cursor.getInt(1) / 60;
                            // 그래프 y좌표 삽입(시간
                            entries.add(new Entry(index++, time));

                            // 좌표 선택 시, 하단에 날짜와 수면시간 출력을 위한 데이터 삽입
                            // 날짜 삽입
                            textDate.add(cursor.getString(0));

                            // 수면시간 삽입
                            int amount = cursor.getInt(1);
                            int hour = amount / 60;
                            int minute = amount - (60 * hour);
                            String s_time = hour + "시간 " + minute + "분";
                            textTime.add(s_time);
                        }

                        // x축 설정
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setValueFormatter(new DateFormatter(xlabels));

                        LineDataSet lineDataSet;
                        // 밤잠 조회모드
                        if (moon) {
                            lineDataSet = new LineDataSet(entries, "밤잠시간");
                            // 그래프 선을 파란색으로
                            lineDataSet.setColor(Color.parseColor("#87CEEB"));
                            // 좌표 색을 파란색으로
                            lineDataSet.setCircleColor(Color.parseColor("#87CEEB"));
                            // Value(y값)을 파란색으로
                            lineDataSet.setValueTextColor(Color.parseColor("#87CEEB"));
                        }
                        // 낮잠 조회모드
                        else {
                            lineDataSet = new LineDataSet(entries, "낮잠시간");
                            // 그래프 선을 노란색으로
                            lineDataSet.setColor(Color.parseColor("#FFCC00"));
                            // 좌표 색을 노란색으로
                            lineDataSet.setCircleColor(Color.parseColor("#FFCC00"));
                            // Value(y값)을 노란색으로
                            lineDataSet.setValueTextColor(Color.parseColor("#FFCC00"));
                        }

                        /*그래프 그리기
                         * MPAndroidChart 사용
                         */

                        // 좌표 그리기 허용
                        lineDataSet.setDrawCircles(true);
                        // 좌표 위에 Value(y값) 출력
                        lineDataSet.setDrawValues(true);

                        // 좌표 내부를 하얗게
                        lineDataSet.setCircleColorHole(Color.WHITE);
                        // 좌표 내부 직경을 5로
                        lineDataSet.setCircleHoleRadius(5);
                        // 그래프 선 너비는 4로
                        lineDataSet.setLineWidth(4);
                        // 좌표 외부 직경을 9로
                        lineDataSet.setCircleRadius(9);
                        // 좌표 위에 출력되는 Value의 Text크기를 11f 로
                        lineDataSet.setValueTextSize(11f);

                        // lineChart에 lineData 추가
                        LineData lineData = new LineData();
                        lineData.addDataSet(lineDataSet);
                        lineChart.setData(lineData);

                        xAxis.setLabelCount(entries.size(), true);
                        // x좌표는 그래프의 하단에 출력
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        // 이건 뭔지 모르겠음
                        lineChart.setVisibleXRangeMaximum(65f);
                        // 바둑판 그리기 설정 해제
                        xAxis.setDrawGridLines(false);

                        //오른쪽 y축 설정
                        YAxis yAxisRight = lineChart.getAxisRight();
                        yAxisRight.setDrawLabels(false);
                        yAxisRight.setDrawAxisLine(false);
                        yAxisRight.setDrawGridLines(false);
                        // description 출력 X
                        lineChart.setDescription(null);
                        // 바둑판 그리기 설정 해제
                        lineChart.getAxisLeft().setDrawGridLines(false);
                        lineChart.getAxisRight().setDrawGridLines(false);
                        lineChart.getAxisLeft().setEnabled(false);


                        // 그래프 갱신
                        lineChart.invalidate();
                    }
                    db.close();
                }
            }
        });

        // 그래프 좌표(노드) 선택 리스너 등록
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e/*선택한 좌표*/, Highlight h) {
                date_container.setVisibility(View.VISIBLE);
                time_container.setVisibility(View.VISIBLE);
                discription.setVisibility(View.GONE);

                String date = xlabels.get((int) e.getX());

                // 수면날짜 출력
                sleepdate.setText(textDate.get((int) e.getX() /*x좌표 위치*/));
                // 수면시간 출력
                sleeptime.setText(textTime.get((int) e.getX() /*x좌표 위치*/));

                DBHelper dbHelper = new DBHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                if(moon){
                    listcursor = db.rawQuery("SELECT SLEEPTIME, WAKETIME, SEP, SLEEPINGTIME FROM SLEEPDIARY WHERE SLEEPDATE = '"
                            + date + "' AND SEP='밤' ORDER BY SLEEPTIME ASC", null);
                }
                else{
                    listcursor = db.rawQuery("SELECT SLEEPTIME, WAKETIME, SEP, SLEEPINGTIME FROM SLEEPDIARY WHERE SLEEPDATE = '"
                            + date + "' AND SEP='낮' ORDER BY SLEEPTIME ASC", null);
                }

                sleepListAdapter.notifyDataSetChanged();
                sleepListAdapter.notifyDataSetInvalidated();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // 그래프 갱신
        lineChart.invalidate();

        return v;
    }

    // ListAdapter
    // 그래프의 좌표(노드) 하나를 선택 시, 해당 일자의 모든 수면 기록을 출력하는 ListAdapter 입니다
    class SleepListAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public SleepListAdapter(Activity activity) {
            inflater = activity.getLayoutInflater();
        }

        public int getCount() {
            // listcursor는  lineChart.setOnChartValueSelectedListener 에서 초기화 되어있습니다.
            if (listcursor == null) {
                return 0;
            } else {
                return listcursor.getCount() + 1;
            }
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return 0;

        }

        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.sleeplist, null);
            }

            // List 출력
            if (listcursor != null) {
                // 잠든시각
                TextView sleeptime = convertView.findViewById(R.id.sleeptime);
                // 기상시각
                TextView waketime = convertView.findViewById(R.id.waketime);
                // 구분 (낮/밤)
                TextView sep = convertView.findViewById(R.id.sep);
                // 수면시간
                TextView sleepingtime = convertView.findViewById(R.id.sleepingtime);

                if (position == 0) {
                    sleeptime.setText("잠든시각");
                    waketime.setText("기상시각");
                    sep.setText("낮/밤");
                    sleepingtime.setText("수면시간");
                }
                // 출력
                else {
                    listcursor.moveToPosition(position - 1);
                    sleeptime.setText(listcursor.getString(0));
                    waketime.setText(listcursor.getString(1));
                    sep.setText(listcursor.getString(2));

                    int amount = listcursor.getInt(3);
                    int hour = amount / 60;
                    int minute = amount - (60 * hour);
                    String time = hour + "시간 " + minute + "분";

                    sleepingtime.setText(time);
                }
            }
            return convertView;
        }
    }

}

class DateFormatter implements IAxisValueFormatter {
    private ArrayList<String> mValues;

    public DateFormatter(ArrayList<String> values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mValues.get((int) value);
    }
}

