
package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.util.Iterator;
import java.util.Set;

public class Practice extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;
    BluetoothService bluetoothService = null;
    MyService SleepService = null;
    BluetoothDevice device;
    ServiceHandler mHandler;
    IntentFilter filter = null;

    ViewPager viewPager;
    TabLayout tabLayout;
    TestPageAdapter testPageAdapter;

    FragmentA fragmentA;
    FragmentB fragmentB;
    SleepDiary sleepDiary;
    PlayLullaby playLullaby;

    boolean is_sleeping = false;
    String WAKE_TIME, WAKE_DATE, SLEEP_TIME, SLEEP_DATE;
    float SLEEPING_TIME;

    public int PAGE_INDEX;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    private String BABY_STATE = "WAKEUP";

    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "page index: " +  PAGE_INDEX, Toast.LENGTH_SHORT).show();

            String str = (String)msg.obj;
            BABY_STATE = str;
            // DateFormat
            if(str.equals("W0")){
                fragmentB.setState("WAKEUP");
                String str2 = sleepDiary.waketime();
                Toast.makeText(getApplicationContext(), str2, Toast.LENGTH_SHORT).show();
                //waketime();
            }
            else if(str.equals("W1")){
                fragmentB.setState("SLEEP");
                sleepDiary.sleeptime();
                //sleeptime();
            }
            else if(str.equals("P1")){
                fragmentB.setState("DIAPER");
            }
        }
    }
    public void sendState(){
        fragmentB.setState(BABY_STATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // 1. 블루투스 를 지원하는지의 여부를 확인한다
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mHandler = new ServiceHandler();

        viewPager = findViewById(R.id.pager);
        testPageAdapter = new TestPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(testPageAdapter);
        tabLayout =  findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        fragmentA = FragmentA.newInstance();
        fragmentB = FragmentB.newInstance();
        sleepDiary = SleepDiary.newInstance();
        playLullaby = PlayLullaby.newInstance();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(0).setIcon(R.drawable.profile_b);
                tabLayout.getTabAt(1).setIcon(R.drawable.diary_b);
                tabLayout.getTabAt(2).setIcon(R.drawable.sleep_b);
                tabLayout.getTabAt(3).setIcon(R.drawable.lulaby_b);
                PAGE_INDEX = position;

                switch (position){
                    case 0:
                        fragmentB.setState(BABY_STATE);
                        tabLayout.getTabAt(0).setIcon(R.drawable.profile);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.diary);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.sleep);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.lullaby);
                        break;

                    default:
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.profile_b);
        tabLayout.getTabAt(1).setIcon(R.drawable.diary_b);
        tabLayout.getTabAt(2).setIcon(R.drawable.sleep_b);
        tabLayout.getTabAt(3).setIcon(R.drawable.lulaby_b);
    }

    public class TestPageAdapter extends FragmentStatePagerAdapter {
        private final int PAGE_NUMBER = 4;

        public TestPageAdapter(FragmentManager fm){
            super(fm);
        }
        @Override
        public Fragment getItem(int position){
            PAGE_INDEX = position;
            switch (position){
                case 0:
                    return fragmentB;
                case 1:
                    return fragmentA;
                case 2:
                    return sleepDiary;
                case 3:
                    return playLullaby;
                default:
                    return null;
            }
        }
        public int getCount(){
            return PAGE_NUMBER;
        }

        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "홈";
                case 1:
                    return "침대설정";
                case 2:
                    return "수면일기";
                case 3:
                    return "자장가";

                default:
                    return null;
            }
        }
        @Override
        public int getItemPosition(Object object){
            return super.getItemPosition(object);
        }
    }

    public void onStart() {
        super.onStart();
        // 블루투스 활성화 확인
        // 안되어 있으면 강제 ON
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        if (bluetoothService == null) {
            bluetoothService = new BluetoothService(this, bluetoothAdapter, mHandler);
        }
    }

    public void onResume() {
        super.onResume();
        if (bluetoothService != null) {
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                bluetoothService.start();

                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                // Request discover from BluetoothAdapter
                bluetoothAdapter.startDiscovery();

                final Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
                if (deviceSet.size() > 0) {
                    final String[] deviceArray = new String[deviceSet.size()];
                    Iterator<BluetoothDevice> iterator = deviceSet.iterator();
                    int i = 0;
                    while (iterator.hasNext()) {
                        BluetoothDevice d = iterator.next();
                        deviceArray[i] = d.getName();
                        i++;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle("블루투스 기기 선택");
                    builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String selectDeviceStr = deviceArray[i];
                            for (BluetoothDevice d : deviceSet) {
                                if (d.getName().equals(selectDeviceStr)) {
                                    device = d;
                                    bluetoothService.connect(device);
                                }
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getName().equals("HC-06")) {
                    bluetoothService.connect(device);
                }
            }
        }
    };

    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    public BluetoothService getBluetoothService(){
        return bluetoothService;
    }

//    public void sleeptime() {
//
//        if(!is_sleeping) {
//            is_sleeping = true;
//            SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
//            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
//
//            Date currentTime = new Date();
//            SLEEP_DATE = date.format(currentTime);
//            SLEEP_TIME = time.format(currentTime);
//
//            Toast.makeText(getApplicationContext(), SLEEP_DATE+ " " + SLEEP_TIME, Toast.LENGTH_SHORT).show();
//        }
//    }
//    public void waketime(){
//
//        if(is_sleeping){
//            SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
//            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
//
//            Date currentTime = new Date();
//            WAKE_DATE = date.format(currentTime);
//            WAKE_TIME = time.format(currentTime);
//
//            Toast.makeText(getApplicationContext(), WAKE_DATE + " " + WAKE_TIME, Toast.LENGTH_SHORT).show();
//            is_sleeping = false;
//            count_time();
//        }
//
//    }
//
//    public void count_time(){
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
//        long h, m, s;
//        try{
//            Date starttime = sdf.parse(SLEEP_TIME);
//            Date endtime= sdf.parse(WAKE_TIME);
//
//            long diff =  endtime.getTime() - starttime.getTime();
//            // h = diff/1000*60*60;
//            m = diff/60000;
//            s = diff/1000;
//            String str = "" + m + "." +s;
//            //SLEEPING_TIME = Float.parseFloat(str);
//            Toast.makeText(getApplicationContext(), "diff: "+diff + " m: " + m + " s: " + s,  Toast.LENGTH_SHORT).show();
//        }
//        catch (ParseException e){
//            Toast.makeText(getApplicationContext(), "안된다", Toast.LENGTH_SHORT).show();
//        }
//    }
}

