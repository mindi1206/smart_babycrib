package com.example.bluetooth;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentA extends Fragment {
    private int SWING_SPEED;
    private int SEAT_TEMPERATURE;
    private int BACK_SUPPORT;
    private int HEIGHT;


    private int SLEEP_SWING;
    private int SLEEP_SEAT;
    private int SLEEP_BACK;
    private int SLEEP_HEIGHT;

    private int WAKE_SWING;
    private int WAKE_SEAT;
    private int WAKE_BACK;
    private int WAKE_HEIGHT;

    Practice practiceActivity;
    RangeSeekBar swing;
    RangeSeekBar seat;
    RangeSeekBar backsupport;
    RangeSeekBar height;

    ImageButton sleepmode, wakeupmode;
    public FragmentA() {
        // Required empty public constructor
    }

    public static FragmentA newInstance() {
        Bundle args = new Bundle();
        FragmentA fragment = new FragmentA();

        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // DB에서 가져온 평균값 저장

        SLEEP_SWING = 1;
        SLEEP_SEAT = 3;
        SLEEP_BACK = 2;
        SLEEP_HEIGHT = 0;

        WAKE_SWING = 3;
        WAKE_SEAT = 0;
        WAKE_BACK = 1;
        WAKE_HEIGHT = 3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SWING_SPEED = 0;
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        swing = view.findViewById(R.id.swing);
        seat = view.findViewById(R.id.seat);
        backsupport = view.findViewById(R.id.backsupport);
        height = view.findViewById(R.id.height);

        sleepmode = view.findViewById(R.id.sleepmode);
        wakeupmode = view.findViewById(R.id.wakeupmode);

        swing.setProgress(0, 4);
        swing.setRange(0, 3);
        swing.setIndicatorTextDecimalFormat("0");

        seat.setProgress(0, 4);
        seat.setRange(0, 3);
        seat.setIndicatorTextDecimalFormat("0");

        backsupport.setProgress(0, 3);
        backsupport.setRange(0, 2);
        backsupport.setIndicatorTextDecimalFormat("0");

        height.setProgress(0, 4);
        height.setRange(0, 3);
        height.setIndicatorTextDecimalFormat("0");

        practiceActivity = (Practice) getActivity();

        sleepmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swing.setProgress((float)SLEEP_SWING);
                seat.setProgress((float)SLEEP_SEAT);
                backsupport.setProgress((float)SLEEP_BACK);
                height.setProgress((float)SLEEP_HEIGHT);

                SWING_SPEED = SLEEP_SWING;
                SEAT_TEMPERATURE = SLEEP_SEAT;
                BACK_SUPPORT = SLEEP_BACK;
                HEIGHT = SLEEP_HEIGHT;

                send_SWING(SLEEP_SWING);
                send_SEAT_TEMPERATURE(SLEEP_SEAT);
                send_BACK_SUPPORT(SLEEP_BACK);

            }
        });
        wakeupmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swing.setProgress((float)WAKE_SWING);
                seat.setProgress((float)WAKE_SEAT);
                backsupport.setProgress((float)WAKE_BACK);
                height.setProgress((float)WAKE_HEIGHT);

                SWING_SPEED = WAKE_SWING;
                SEAT_TEMPERATURE = WAKE_SEAT;
                BACK_SUPPORT = WAKE_BACK;
                HEIGHT = WAKE_HEIGHT;

                send_SWING(WAKE_SWING);
                send_SEAT_TEMPERATURE(WAKE_SEAT);
                send_BACK_SUPPORT(WAKE_BACK);

            }
        });

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
                Toast.makeText(getActivity(), "end", Toast.LENGTH_SHORT).show();
                send_SWING(SWING_SPEED);
//                try {
//                    if (SWING_SPEED == 0) {
//                        practiceActivity.getBluetoothService().write("SWING_OFF");
//
//                    } else if (SWING_SPEED == 1) {
//                        practiceActivity.getBluetoothService().write("SWING_ONE");
//
//                    } else if (SWING_SPEED == 2) {
//                        practiceActivity.getBluetoothService().write("SWING_TWO");
//
//                    } else {
//                        practiceActivity.getBluetoothService().write("SWING_THREE");
//                    }
//                }
//                catch (NullPointerException e) {
//                    Toast.makeText(getActivity(), "BluetoothService 객체가 NULL입니다.", Toast.LENGTH_SHORT).show();
//                }
            }
        });

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
                send_SEAT_TEMPERATURE(SEAT_TEMPERATURE);
//                try {
//                    if (SEAT_TEMPERATURE == 0) {
//                        practiceActivity.getBluetoothService().write("SEAT_OFF");
//
//                    } else if (SEAT_TEMPERATURE == 1) {
//                        practiceActivity.getBluetoothService().write("SEAT_ONE");
//
//                    } else if (SEAT_TEMPERATURE == 2) {
//                        practiceActivity.getBluetoothService().write("SEAT_TWO");
//
//                    } else {
//                        practiceActivity.getBluetoothService().write("SEAT_THREE");
//                    }
//                }
//                catch (NullPointerException e) {
//                    Toast.makeText(getActivity(), "BluetoothService 객체가 NULL입니다.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        backsupport.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue < 1) {
                    BACK_SUPPORT = 0;
                } else if (leftValue < 2) {
                    BACK_SUPPORT = 1;
                }
                else {
                    BACK_SUPPORT = 2;
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                send_BACK_SUPPORT(BACK_SUPPORT);
//                try {
//                    if (BACK_SUPPORT == 0) {
//                        practiceActivity.getBluetoothService().write("BACK_OFF");
//                    } else if (BACK_SUPPORT == 1) {
//                        practiceActivity.getBluetoothService().write("BACK_ONE");
//                    } else {
//                        practiceActivity.getBluetoothService().write("BACK_TWO");
//                    }
//                }
//                catch (NullPointerException e) {
//                    //switchtext.setText("NULL");
//                    Toast.makeText(getActivity(), "BluetoothService 객체가 NULL입니다.", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        return view;
    }
    public void send_SWING(int data){
        try {
            if (data == 0) {
                practiceActivity.getBluetoothService().write("SWING_OFF");

            } else if (data == 1) {
                practiceActivity.getBluetoothService().write("SWING_ONE");

            } else if (data == 2) {
                practiceActivity.getBluetoothService().write("SWING_TWO");

            } else {
                practiceActivity.getBluetoothService().write("SWING_THREE");
            }
        }
        catch (NullPointerException e) {
            Toast.makeText(getActivity(), "BluetoothService 객체가 NULL입니다.", Toast.LENGTH_SHORT).show();
        }
    }
    public void send_BACK_SUPPORT(int data){
        try {
            if (data == 0) {
                practiceActivity.getBluetoothService().write("BACK_OFF");
            } else if (data == 1) {
                practiceActivity.getBluetoothService().write("BACK_ONE");
            } else {
                practiceActivity.getBluetoothService().write("BACK_TWO");
            }
        }
        catch (NullPointerException e) {
            //switchtext.setText("NULL");
            Toast.makeText(getActivity(), "BluetoothService 객체가 NULL입니다.", Toast.LENGTH_SHORT).show();
        }
    }
    public void send_SEAT_TEMPERATURE(int data){
        try {
            if (data == 0) {
                practiceActivity.getBluetoothService().write("SEAT_OFF");

            } else if (data == 1) {
                practiceActivity.getBluetoothService().write("SEAT_ONE");

            } else if (data == 2) {
                practiceActivity.getBluetoothService().write("SEAT_TWO");

            } else {
                practiceActivity.getBluetoothService().write("SEAT_THREE");
            }
        }
        catch (NullPointerException e) {
            Toast.makeText(getActivity(), "BluetoothService 객체가 NULL입니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
