package com.example.bluetooth;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SleepDiary extends Fragment {

    ArrayList<String> xlabels = new ArrayList<>();
    String[] mDays = {
            "05/25", "05/26", "05/27", "05/28", "05/29", "05/30", "05/31"
    };
    TextView startt, endt, sleept;
    boolean is_sleeping = false;
    String WAKE_TIME = "", WAKE_DATE = "", SLEEP_TIME = "", SLEEP_DATE = "";
    float SLEEPING_TIME;
    String year, day, month, date_format;
    TextView calendar, date, sleeptime;
    Button ok;
    int xoffset;
    EditText edit_sleeptime;

    long start_t, end_t;

    public SleepDiary() {
        // Required empty public constructor
    }
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
        View v  =inflater.inflate(R.layout.sleep_diary, container, false);
        edit_sleeptime = v.findViewById(R.id.edit_sleeptime);
        date = v.findViewById(R.id.date);
        sleeptime = v.findViewById(R.id.sleeptime);
        calendar = v.findViewById(R.id.fragment2_setdate);
        startt = v.findViewById(R.id.startt);
        endt = v.findViewById(R.id.endt);
        sleept = v.findViewById(R.id.sleept);
        ok = v.findViewById(R.id.ok);

        xlabels.add("05/25");
        xlabels.add("05/26");
        xlabels.add("05/27");
        xlabels.add("05/28");
        xlabels.add("05/29");
        xlabels.add("05/30");


        //
        final LineChart lineChart = v.findViewById(R.id.daygraph_chart);
        lineChart.setNoDataTextColor(Color.WHITE);




        final ArrayList<Entry> entries = new ArrayList<>();

        ArrayList<Entry> num = new ArrayList<>();


        entries.add(new Entry(0, 3.6f));
        entries.add(new Entry(1, 1.8f));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 3.2f));
        entries.add(new Entry(4, 4.5f));
        entries.add(new Entry(5, 2.3f));
        xoffset = 6;

//        num.add(new Entry(0, 2));
//        num.add(new Entry(1, 2));
//        num.add(new Entry(2, 3));
//        num.add(new Entry(3, 4));
//        num.add(new Entry(4, 3));
//        num.add(new Entry(5, 2));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float f = Float.parseFloat(edit_sleeptime.getText().toString());

                xlabels.add(date_format);
                XAxis xAxis = lineChart.getXAxis(); // x 축 설정
                xAxis.setValueFormatter(new DateFormatter(xlabels));
                entries.add(new Entry(xoffset, f));
                xoffset++;

                LineDataSet lineDataSet = new LineDataSet(entries, "수면시간");

                lineDataSet.setDrawCircles(false);
                lineDataSet.setColor(Color.parseColor("#87CEEB"));
                lineDataSet.setDrawCircles(true);
                lineDataSet.setDrawValues(true);
                lineDataSet.setCircleColor(Color.parseColor("#87CEEB"));
                lineDataSet.setCircleColorHole(Color.WHITE);
                lineDataSet.setLineWidth(4);
                lineDataSet.setCircleRadius(9);
                lineDataSet.setValueTextSize(11f);
                //lineDataSet.setDrawValues(false);
                lineDataSet.setCircleHoleRadius(5);
                lineDataSet.setValueTextColor(Color.parseColor("#87CEEB"));

                LineData lineData = new LineData();
                lineData.addDataSet(lineDataSet);
                lineChart.setData(lineData);




                xAxis.setLabelCount(entries.size(), true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.setVisibleXRangeMaximum(65f);
                xAxis.setDrawGridLines(false);

                YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
                yAxisRight.setDrawLabels(false);
                yAxisRight.setDrawAxisLine(false);
                yAxisRight.setDrawGridLines(false);
                lineChart.setDescription(null);
                lineChart.getAxisLeft().setDrawGridLines(false);
                lineChart.getAxisRight().setDrawGridLines(false);
                lineChart.getAxisLeft().setEnabled(false);

                lineChart.invalidate();
                edit_sleeptime.setText("");

            }
        });


        LineDataSet lineDataSet = new LineDataSet(entries, "수면시간");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(Color.parseColor("#87CEEB"));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(true);
        lineDataSet.setCircleColor(Color.parseColor("#87CEEB"));
        lineDataSet.setCircleColorHole(Color.WHITE);
        lineDataSet.setLineWidth(4);
        lineDataSet.setCircleRadius(9);
        lineDataSet.setValueTextSize(11f);
        //lineDataSet.setDrawValues(false);
        lineDataSet.setCircleHoleRadius(5);
        lineDataSet.setValueTextColor(Color.parseColor("#87CEEB"));


//        LineDataSet timeSet = new LineDataSet(num, "낮잠횟수");
//        timeSet.setDrawCircles(false);
//        timeSet.setColor(Color.parseColor("#ffcc00"));
//        timeSet.setDrawCircles(true);
//        timeSet.setDrawValues(true);
//        timeSet.setCircleColor(Color.parseColor("#ffcc00"));
//        timeSet.setCircleColorHole(Color.WHITE);
//        timeSet.setLineWidth(4);
//        timeSet.setCircleRadius(9);
//        timeSet.setValueTextSize(11f);
//        timeSet.setCircleHoleRadius(5);
//        timeSet.setValueTextColor(Color.parseColor("#ffcc00"));
//        lineDataSet.disableDashedLine();

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                date.setText(xlabels.get((int)e.getX()));
                sleeptime.setText("" +e.getY());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);
        // lineData.addDataSet(timeSet);

        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
        xAxis.setValueFormatter(new DateFormatter(xlabels));
        xAxis.setLabelCount(entries.size(), true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setVisibleXRangeMaximum(65f);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        lineChart.setDescription(null);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);


        calendar.setOnTouchListener(new View.OnTouchListener() {
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
                                m += 1;
                                if (m < 10) {
                                    month = "0" + m;
                                } else {
                                    month = "" + m;
                                }
                                if (d < 10) {
                                    day = "0" + d;
                                } else {
                                    day = "" + d;
                                }
                                date_format = month + "/" + day;

                                calendar.setText(month + "월 " + day + "일");
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


        lineChart.invalidate();


        return v;
    }

    public void sleeptime() {
        if(!is_sleeping) {
            is_sleeping = true;
            SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

            Date currentTime = new Date();
            SLEEP_DATE = date.format(currentTime);
            SLEEP_TIME = time.format(currentTime);
            startt.setText(SLEEP_DATE+ " " + SLEEP_TIME);
        }
    }
    public String waketime(){

        String str = "";
        if(is_sleeping){
            SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

            Date currentTime = new Date();
            WAKE_DATE = date.format(currentTime);
            WAKE_TIME = time.format(currentTime);
            endt.setText( WAKE_DATE + " " + WAKE_TIME);
           // Toast.makeText(getActivity(), WAKE_DATE + " " + WAKE_TIME, Toast.LENGTH_SHORT).show();
            is_sleeping = false;
             str =  count_time();


        }
        return str;
    }

    public String count_time(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        long h, m, s;
        String str2 = "";
        try{
            Date starttime = sdf.parse(SLEEP_TIME);
            Date endtime= sdf.parse(WAKE_TIME);

            long diff =  endtime.getTime() - starttime.getTime();
            // h = diff/1000*60*60;
            m = diff/60000;
            s = diff/1000;
            String str = "" + m + "." +s;
            SLEEPING_TIME = Float.parseFloat(str);
            sleept.setText(""+SLEEPING_TIME);
             str2 = "diff: "+diff + " m: " + m + " s: " + s;
            //Toast.makeText(getActivity(), "diff: "+diff + " m: " + m + " s: " + s,  Toast.LENGTH_SHORT).show();
        }
        catch (ParseException e){
            Toast.makeText(getActivity(), "안된다", Toast.LENGTH_SHORT).show();
        }
        return str2;
    }
}

class DateFormatter implements IAxisValueFormatter {
    private ArrayList<String> mValues;

    public DateFormatter(ArrayList<String> values){
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis){
        return mValues.get((int)value);
    }
}
