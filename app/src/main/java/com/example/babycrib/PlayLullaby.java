package com.example.babycrib;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayLullaby extends Fragment {

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    MainActivity mainActivityActivity;
    ProgressBar progressBar;
    ImageView audio_albumart, refresh;
    LullabyListAdapter adapter;
    TextView audio_title;

    // 순위별로 정렬된 리스트
    Queue q = new LinkedList();
    // 재생된 음악 리스트
    Stack s = new Stack();
    // 현재 재생중인 음악 인덱스
    int play_index;
    // 음악재생이 첫 시작인 경우
    boolean music_start, is_play;
    String playtext;

    ImageView play, stop, next, pre;
    // 자장가 이미지
    private Integer[] images = {
            R.drawable.lulaby2, R.drawable.lulaby3, R.drawable.lulaby4, R.drawable.lulaby5, R.drawable.lulaby10,
            R.drawable.lulaby6, R.drawable.lulaby7, R.drawable.lulaby8, R.drawable.lulaby9, R.drawable.lulaby10, R.drawable.samplealbum
    };
    // 자장가 타이틀
    private String[] titles = {
            "핑크퐁 자장가", "슈만 자장가", "섬집아기", "브람스 자장가", "캐롤",
            "모짜르트 자장가", "오르골 자장가", "잠자는 아기", "명상음악", "아기를 위한 클래식", "음악을 선택해주세요"
    };
    // 자장가별 HW 전송 신호
    private String[] sendTo = {
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9"
    };

    public PlayLullaby() {
        // Required empty public constructor
    }

    // viewpager fragment 생성 후 return
    public static PlayLullaby newInstance() {
        Bundle args = new Bundle();
        PlayLullaby fragment = new PlayLullaby();

        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM LULLABY ORDER BY preference desc", null);

        playtext = "";
        play_index = 0;
        music_start = true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.play_lullaby, container, false);
        play = view.findViewById(R.id.audio_play);
        stop = view.findViewById(R.id.audio_stop);
        pre = view.findViewById(R.id.audio_pre);
        next = view.findViewById(R.id.audio_next);
        refresh = view.findViewById(R.id.refresh);

        audio_albumart = view.findViewById(R.id.audio_albumart);
        audio_title = view.findViewById(R.id.audio_title);

        stop.setEnabled(false);
        play.setEnabled(true);

        mainActivityActivity = (MainActivity) getActivity();

        if (savedInstanceState != null) {
            play_index = savedInstanceState.getInt("PLAY_INDEX");
            audio_albumart.setImageResource(images[play_index]);
            audio_title.setText(titles[play_index]);
        }
        else{

        }

        ListView list = view.findViewById(R.id.list);
        adapter = new LullabyListAdapter(mainActivityActivity);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.moveToPosition(i);
                // 0 1 2 3 4 5 7 8 9
                play_index = Integer.parseInt(cursor.getString(0).toString()) - 1;
                // 타이틀
                String title = cursor.getString(1);
                mainActivityActivity.is_play = true;

                play.setEnabled(false);
                stop.setEnabled(true);
                audio_albumart.setImageResource(images[play_index]);
                audio_title.setText(title);

                try {
                    mainActivityActivity.getBluetoothService().write(sendTo[play_index]);
                    s.push(i);

                    if (music_start) music_start = false;
                } catch (NullPointerException e) {

                }

                Toast.makeText(getActivity(), "사용자 선택재생 : " + (play_index + 1) + "번 " +  titles[play_index] , Toast.LENGTH_SHORT).show();

            }
        });

        // 자장가 순위 갱신
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cursor = db.rawQuery("SELECT * FROM LULLABY ORDER BY preference desc", null);
                // 애니메이션
                Animation moonanimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.refresh_animation);
                refresh.startAnimation(moonanimation);
                // 리스트뷰 갱신
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();
            }
        });


        // 음악 일시정지
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                play.setEnabled(true);
                stop.setEnabled(false);
                try {
                    mainActivityActivity.getBluetoothService().write("!");
                } catch (NullPointerException e) {

                }
            }
        });

        // 음악을 다시 재생함
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                play.setEnabled(false);
                stop.setEnabled(true);

                // 만약 음악을 처음 재생하는 거라면
                if (music_start) {
                    music_start = false;

                    // 액티비티의 큐 의 음악 실행 함수 호출
                    mainActivityActivity.playNextMusic();
                }

                // 이전에 재생한 곡이 있다면
                // 일시정지 했던 음악을 다시 재생
                else {
                    try {
                        mainActivityActivity.getBluetoothService().write("!");
                    } catch (NullPointerException e) {

                    }
                }
            }
        });
        return view;
    }


    // play_index 는 반드시 0 부터 9까지의 값으로 해야함미다
    // 액티비티에서 호출하는 함수
    public void setUI(int play_index, boolean isplay) {
        // isplay = true -> 음악이 재생중이므로 재생버튼 비활성화, 정지버튼 활성화
        play.setEnabled(!isplay);
        stop.setEnabled(isplay);
        audio_albumart.setImageResource(images[play_index]);
        audio_title.setText(titles[play_index]);
    }

    class LullabyListAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public LullabyListAdapter(Activity activity) {
            inflater = activity.getLayoutInflater();
        }

        public int getCount() {
            return cursor.getCount();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return 0;

        }

        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.lullabylist, null);
            }

            TextView t_index = convertView.findViewById(R.id.index);
            TextView preference = convertView.findViewById(R.id.preference);
            ImageView image = convertView.findViewById(R.id.image);
            TextView title = convertView.findViewById(R.id.title);
            TextView singer = convertView.findViewById(R.id.singer);
            TextView playtime = convertView.findViewById(R.id.playtime);

            cursor.moveToPosition(position);

            int index = Integer.parseInt(cursor.getString(0).toString());
            t_index.setText(cursor.getString(0));
            preference.setText(cursor.getString(4));
            image.setImageResource(images[index - 1]);
            title.setText(cursor.getString(1));
            playtime.setText(cursor.getString(2));
            singer.setText(cursor.getString(3));

            return convertView;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("PLAY_INDEX", play_index);
    }

}
