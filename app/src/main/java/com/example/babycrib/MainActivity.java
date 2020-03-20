
package com.example.babycrib;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;
    BluetoothService bluetoothService = null;
    BluetoothDevice device;
    ServiceHandler mHandler;
    IntentFilter filter = null;

    ViewPager viewPager;
    TabLayout tabLayout;
    TestPageAdapter testPageAdapter;

    Setting setting;
    Home home;
    SleepDiary sleepDiary;
    PlayLullaby playLullaby;

    // 순위별로 정렬된 리스트
    Queue q = new LinkedList();
    // 재생된 음악 리스트
    Stack s = new Stack();
    // 현재 재생중인 음악 인덱스
    int play_index;
    boolean is_play, setting_enable = true, report_visible = false;

    boolean is_sleeping = false;
    String WAKE_TIME, WAKE_DATE, SLEEP_TIME, SLEEP_DATE;
    int SLEEPING_TIME;

    int SWING_SPEED, SEAT_TEMPERATURE, BACK_SUPPORT, HEIGHT;

    public int PAGE_INDEX = 0;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    private String BABY_STATE;

    // 자장가별 HW 전송 신호
    private String[] sendTo = {
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9"
    };
    // 자장가 타이틀
    private String[] titles = {
            "핑크퐁 자장가", "슈만 자장가", "섬집아기", "브람스 자장가", "캐롤",
            "모짜르트 자장가", "오르골 자장가", "잠자는 아기", "명상음악", "아기를 위한 클래식", "음악을 선택해주세요"
    };



    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;

            // 아기 상태 판단
            // 1. 기상
            if (str.equals("B")) {
                if(BABY_STATE == "SLEEP"){
                    //String str2 = sleepDiary.waketime();
                    waketime();
                    BABY_STATE = "WAKEUP";
                    setReport_visible(true);
                    stateNotification();
                }
                BABY_STATE = "WAKEUP";
                // 현재 페이지가 home 일 때 화면 갱신
                if(PAGE_INDEX == 0 ) {
                    home.setState("WAKEUP");
                }

                // noticifation
                Toast.makeText(getApplicationContext(), "H/W MESSAGE:" + str + " BABY STATE: " + BABY_STATE, Toast.LENGTH_SHORT).show();

            }
            // 2. 수면
            else if (str.equals("A")) {
                BABY_STATE = "SLEEP";
                if(PAGE_INDEX == 0 ) {
                    home.setState("SLEEP");
                }

                //sleepDiary.sleeptime();
                sleeptime();
                // 자장가의 musicstart = true로 초기화
                //playLullaby.rankPlayList();

                // 11/15
                rankPlayList();

                is_play = false;
                // noticifation
                stateNotification();
                Toast.makeText(getApplicationContext(), "H/W MESSAGE:" + str + " BABY STATE: " + BABY_STATE, Toast.LENGTH_SHORT).show();
            }
            // 3. 기저귀
            else if (str.equals("C")) {
                BABY_STATE = "DIAPER";
                // noticifation
                stateNotification();
                // 현재 페이지가 home 일 때 화면 갱신
                if (PAGE_INDEX == 0) {
                    home.setState("DIAPER");
                }

                Toast.makeText(getApplicationContext(), "H/W MESSAGE:" + str + " BABY STATE: " + BABY_STATE, Toast.LENGTH_SHORT).show();
            }
            // 시스템 알림
            // 1. 음악 정지
            else if (str.equals("S")) {
                if(is_play){
                    playNextMusic();
                    // 현재 페이지가 playLullaby 일 때 화면 갱신
                    if(PAGE_INDEX == 3){
                        playLullaby.setUI(play_index, is_play);
                    }
                }
            }

            // 수면모드 클릭 시
            else if(str.equals("T")){
                Toast.makeText(getApplicationContext(), "T가 전송되었습니다", Toast.LENGTH_SHORT).show();
                //액티비티의 큐 랭크 함수 호출해 큐 초기화
                Initplaylist();
                // 액티비티의 큐 의 음악 실행 함수 호출
                playNextMusic();
            }

            // 블루투스 연결 성공과 실패 알림
            // 연결실패
            else if (str.equals("fail")) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 실패", Toast.LENGTH_SHORT).show();
            bluetoothService = null;
        }
            // 연결성공
            else if (str.equals("success")) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 성공", Toast.LENGTH_SHORT).show();
            bluetoothService.write("=");
        }
        }
    }

    public void setSWING_SPEED(int sw){
        SWING_SPEED = sw;
    }
    public void setSEAT_TEMPERATURE(int se){
        SEAT_TEMPERATURE = se;
    }
    public void setHEIGHT(int he){
        HEIGHT = he;
    }
    public void setBACK_SUPPORT(int ba){
        BACK_SUPPORT = ba;
    }
    public void setSetting_enable(boolean enable){
        if(setting_enable !=enable){
            setting_enable = enable;
        }
    }

    public void setReport_visible(boolean visible){
        if(report_visible !=visible){
            report_visible = visible;
        }
    }
    public boolean getSetting_enable(){
        return setting_enable;
    }


    public void bluetoothconnect(){
        bluetoothService = null;
        bluetoothService = new BluetoothService(this, bluetoothAdapter, mHandler);
//        if (bluetoothService == null) {


//            bluetoothService = new BluetoothService(this, bluetoothAdapter, mHandler);
//        }
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

    // 서준이가 일어나있을때만 화면에 ON 해두고 선택 후에는 다시 사라지게 만들기
    public void WakeupBehaivor() {
        setReport_visible(false);

        final List<String> ListItems = new ArrayList<>();
        ListItems.add("기저귀");
        ListItems.add("울기");
        ListItems.add("칭얼칭얼");
        ListItems.add("분유");
        ListItems.add("옹알이");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final List SelectedItems  = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("서준이가 일어나서 한 행동은?");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg="";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);

                            DBHelper dbHelper = new DBHelper(getApplicationContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            Cursor cursor = db.rawQuery("SELECT * FROM behaivor WHERE NAME = '" + msg + "';", null);

                            if (cursor.getCount() == 0) {
                                Toast.makeText(getApplicationContext(), "야 없대", Toast.LENGTH_SHORT).show();
                            } else {
                                while (cursor.moveToNext()) {
                                    int preference = cursor.getInt(2);
                                    // 업데이트
                                    preference++;
                                    db.execSQL("UPDATE BEHAIVOR SET CNT = " + preference + " WHERE NAME = '" + msg + "';");
                                }
                                Toast.makeText(getApplicationContext(), "행동패턴을 저장했습니다!", Toast.LENGTH_SHORT).show();
                            }
                            db.close();
                        }
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    public String sendState() {
        return BABY_STATE;
    }

    public void Initplaylist() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LULLABY ORDER BY preference desc", null);

        while (!q.isEmpty()) {
            q.poll();
        }
        while (cursor.moveToNext()) {
            // 0 1 2 3 4 5 6 7 8 9
            int i = Integer.parseInt(cursor.getString(0).toString()) - 1;
            // 큐에 들어간 건 문자열 (아트메가 전송용)
            // 0 1 2 3 4 5 6 7 8 9
            q.offer((sendTo[i]));
        }

        cursor.moveToFirst();
        db.close();
    }

    public void rankPlayList() {
        int cnt = 4;

        if (!s.empty()) {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            while (!s.isEmpty() && cnt-- > 1) {
                int id = Integer.parseInt(s.peek().toString()) + 1;

                Cursor cursor = db.rawQuery("SELECT * FROM LULLABY WHERE ID = " + id + ";", null);
                while (cursor.moveToNext()) {
                    int preference = cursor.getInt(4) + cnt;
                    db.execSQL("UPDATE LULLABY SET preference = " + preference + " WHERE ID = " + id + ";");
                    s.pop();
                }
            }
            db.close();
        }
    }
    Queue getQ(){
        return q;
    }

    // 아이상태 Notification
    public void stateNotification() {

        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // API 26이상 필수
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notifManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap mLargeIconForNoti = BitmapFactory.decodeResource(getResources(), R.drawable.largenoti);
        builder.setLargeIcon(mLargeIconForNoti).setSmallIcon(R.drawable.smallnoti).setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        // 아기 상태별 타이틀과 내용 설정

        // 기상 시 -> 몇분 몇시에 기상했음 총 몇시간 잤다
        if (BABY_STATE == "WAKEUP") {
            builder.setContentTitle("서준이가 일어났어요!").setContentText(WAKE_DATE + " " + WAKE_TIME + " 에 기상했으며, 총 " + (SLEEPING_TIME / 60) + "시간 00분 잤습니다.");
        }
        // 수면 시 -> 몇분 몇시에 잠이 들었습니다.
        else if (BABY_STATE == "SLEEP") {
            builder.setContentTitle("서준이가 잠이 들었어요!").setContentText(SLEEP_DATE + " " + SLEEP_TIME + "에 잠이 들었습니다");
        }
        // 기저귀
        else {
            builder.setContentTitle("서준이의 기저귀를 확인해주세요!").setContentText("기저귀를 확인해주세요!");
        }

        //Notification
        notifManager.notify(1, builder.build());
    }

    // 다음 음악 재생
    public void playNextMusic() {
        // 재생 큐가 비어있다면 initplaylist() 호출해
        if (q.isEmpty()) {
            Initplaylist();
        }
        is_play = true;
        if (!q.isEmpty()) {
            // 여기서 나오는 값은 0부터 9까지의 값
            play_index = Integer.parseInt(q.peek().toString());

            try {
                getBluetoothService().write(q.peek().toString());
            } catch (NullPointerException e) {

            }
            // 스택에 들어가는건 숫자! 저장하는 함수 (액티비티용)
            // 0 1 2 3 4 5 6 7 8 9
            q.poll();
            s.push(play_index);

            //playLullaby.setUI(play_index, true);
        }
        Toast.makeText(getApplicationContext(), "선호도별 자동재생 : "  + (play_index + 1) +"번 "+ titles[play_index] , Toast.LENGTH_SHORT).show();

    }

    // 0 1 2 3 4 5 6 7 8 9
    public void pushStack(int play_index) {
        s.push(play_index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase("baby.db"); // 없애도 됩니다.
        PAGE_INDEX = 0;
        play_index = 10;
        is_play = false;
        BABY_STATE = "WAKEUP";

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // 블루투스 지원여부를 확인
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
        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        setting = Setting.newInstance();
        home = Home.newInstance();
        sleepDiary = SleepDiary.newInstance();
        playLullaby = PlayLullaby.newInstance();

        // 뷰페이저 페이지 변경 리스너 등록
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

               // PAGE_INDEX = position;
                PAGE_INDEX = 0;
                // 페이지 선택 시 이벤트 작성
                switch (position) {
                    case 0:
                        home.setState(BABY_STATE);
                        home.setButtonState();

                        tabLayout.getTabAt(0).setIcon(R.drawable.profile);
                        // 페이지 번호 갱신
                        PAGE_INDEX = 0;
                        // Toast.makeText(getApplicationContext(), "0",  Toast.LENGTH_SHORT).show();
                        // 공모전을 위해 주석처리 했으니 나중에 풀어주기바람
                        //Toast.makeText(getApplicationContext(), "H/W MESSAGE:" + + "BABY STATE: " + BABY_STATE, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        setting.drawPieChart();
                       // Toast.makeText(getApplicationContext(), "1",  Toast.LENGTH_SHORT).show();
                        setting.setUI(SWING_SPEED, SEAT_TEMPERATURE, HEIGHT, BACK_SUPPORT,setting_enable);
                        tabLayout.getTabAt(1).setIcon(R.drawable.diary);
                        // 페이지 번호 갱신
                        PAGE_INDEX = 1;
                        break;
                    case 2:
                        //Toast.makeText(getApplicationContext(), "2",  Toast.LENGTH_SHORT).show();
                        tabLayout.getTabAt(2).setIcon(R.drawable.sleep);
                        // 페이지 번호 갱신
                        PAGE_INDEX = 2;
                        break;
                    case 3:
                       // Toast.makeText(getApplicationContext(), "3",  Toast.LENGTH_SHORT).show();
                        tabLayout.getTabAt(3).setIcon(R.drawable.lullaby);
                        // 페이지 번호 갱신
                        PAGE_INDEX = 3;

                        // 페이지가 바뀌었을 때 UI를 갱신한다.
                        if (is_play) {
                            playLullaby.setUI(play_index, true);
                        } else {
                            playLullaby.setUI(10, false);
                        }
                        // 자장가 리스트 갱신 (NotifyDataSetChanged)
                        break;

                    default:
                }
                testPageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        // tabLayout 아이콘 설정
        tabLayout.getTabAt(0).setIcon(R.drawable.profile_b);
        tabLayout.getTabAt(1).setIcon(R.drawable.diary_b);
        tabLayout.getTabAt(2).setIcon(R.drawable.sleep_b);
        tabLayout.getTabAt(3).setIcon(R.drawable.lulaby_b);
    }

    // 페이지 어답터
    public class TestPageAdapter extends FragmentStatePagerAdapter {
        private final int PAGE_NUMBER = 4;

        public TestPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //PAGE_INDEX = 0;
                    return home;
                case 1:
                    return setting;
                case 2:
                    return sleepDiary;
                case 3:
                    return playLullaby;
                default:
                    return null;
            }
        }

        public int getCount() {
            return PAGE_NUMBER;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
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
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

    }

    public void onStart() {
        PAGE_INDEX = 0;
        super.onStart();
        // 블루투스 활성화 확인
        // 안되어 있으면 강제 ON
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        // 블루투스서비스 객체 생성
        if (bluetoothService == null) {
            bluetoothService = new BluetoothService(this, bluetoothAdapter, mHandler);
        }
    }

    public void onResume() {
        super.onResume();
        Initplaylist();

        if (bluetoothService != null) {
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                bluetoothService.start();

                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                // Request discover from BluetoothAdapter
                bluetoothAdapter.startDiscovery();

                // 연결 가능한 디바이스 정보 갖고오기
                final Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
                if (deviceSet.size() > 0) {
                    final String[] deviceArray = new String[deviceSet.size()];
                    Iterator<BluetoothDevice> iterator = deviceSet.iterator();
                    int i = 0;
                    while (iterator.hasNext()) {
                        BluetoothDevice d = iterator.next();
                        // 연결가능한 디바이스 이름 갖고오기
                        deviceArray[i] = d.getName();
                        i++;
                    }

                    // alert 창 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle("블루투스 기기 선택");
                    // 블루투스 디바이스 선택 이벤트 리스너
                    builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String selectDeviceStr = deviceArray[i];
                            for (BluetoothDevice d : deviceSet) {
                                // 디바이스 선택
                                if (d.getName().equals(selectDeviceStr)) {
                                    // 선택한 디바이스로 device 초기화
                                    device = d;
                                    // 후 연결
                                    bluetoothService.connect(device);
                                }
                            }
                        }
                    });
                    // alert 창 show
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

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public void sleeptime() {
        if (!is_sleeping) {
            is_sleeping = true;
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

            Date currentTime = new Date();
            SLEEP_DATE = date.format(currentTime);
            SLEEP_TIME = time.format(currentTime);
            //

            Toast.makeText(getApplicationContext(), SLEEP_DATE + " " + SLEEP_TIME, Toast.LENGTH_SHORT).show();
        }
    }

    // 아기 기상 시
    // 기상시간 체크
    public void waketime() {
        if (is_sleeping) {
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

            Date currentTime = new Date();
            WAKE_DATE = date.format(currentTime);
            WAKE_TIME = time.format(currentTime);

            Toast.makeText(getApplicationContext(), WAKE_DATE + " " + WAKE_TIME, Toast.LENGTH_SHORT).show();
            is_sleeping = false;
            // 잠든시간 계산
            count_time();

        }
    }

    // 수면시각, 기상시각을 이용한 시간계산
    public void count_time() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        long h, m, s;
        try {
            Date starttime = sdf.parse(SLEEP_TIME);
            Date endtime = sdf.parse(WAKE_TIME);

            long diff = endtime.getTime() - starttime.getTime();
            // h = diff/1000*60*60;
            m = diff / 60000;
            s = diff / 1000;
            String str = "" + m + "." + s;
            float sleeping_time = Float.parseFloat(str);
            SLEEPING_TIME = (int) s * 60;
            //Toast.makeText(getApplicationContext(), "diff: " + diff + " m: " + m + " s: " + s + "잠잔시각: " + SLEEPING_TIME, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "시간체크 실패", Toast.LENGTH_SHORT).show();
        }

        //DB에 저장하기
        try {
            String SEP = "DEP";
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String start = "06:30:00";
            String end = "18:30:00";

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

            Date now = format.parse(SLEEP_TIME);
            Date start_t = format.parse(start);
            Date end_t = format.parse(end);

            if (start_t.getTime() < now.getTime() && now.getTime() < end_t.getTime()) {
                SEP = "낮";
            } else {
                SEP = "밤";
            }

            db.execSQL("INSERT INTO sleepdiary VALUES(null, '" + SLEEP_DATE + "', '" + WAKE_DATE + "', '" + SLEEP_TIME + "', '" + WAKE_TIME + "', '" + SEP + "', " + SLEEPING_TIME + ")");
            db.close();
            Toast.makeText(getApplicationContext(), "저장 완료! 기록: " + SLEEP_DATE + "~" + WAKE_DATE + ", " + SLEEP_TIME + "~" + WAKE_TIME + " " + SEP + " " + SLEEPING_TIME + " ", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "저장 실패", Toast.LENGTH_SHORT).show();
        }
    }
}

