package com.example.bluetooth;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayLullaby extends Fragment {

    Practice practiceActivity;
    Button btn;
    ProgressBar progressBar;
    ImageView audio_albumart;
    TextView audio_title;
    Intent intent;
    int play_index;

    ImageView play, stop;
    private Integer[] images = {
            R.drawable.lulaby2, R.drawable.lulaby3, R.drawable.lulaby4, R.drawable.lulaby5,
            R.drawable.lulaby6, R.drawable.lulaby7, R.drawable.lulaby8, R.drawable.lulaby9, R.drawable.lulaby10,
    };
    private String[] titles = {
            "핑크퐁 자장가", "섬집아기", "브람스 자장가", "캐롤", "모짜르트 자장가",
            "오르골 자장가", "잠자는 아기", "명상음악", "아기를 위한 클래식", "슈만 자장가"
    };

    private String[] sendTo = {
            "MUSIC_ONE", "MUSIC_TWO", "MUSIC_THREE", "MUSIC_FOUR", "MUSIC_FIVE",
            "MUSIC_SIX", "MUSIC_SEVEN", "MUSIC_EIGHT", "MUSIC_NINE", "MUSIC_TEN"
    };

    public PlayLullaby() {
        // Required empty public constructor
    }
    public static PlayLullaby newInstance() {
        Bundle args = new Bundle();
        PlayLullaby fragment = new PlayLullaby();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.play_lullaby, container, false);
        play = view.findViewById(R.id.audio_play);
        stop = view.findViewById(R.id.audio_stop);
        progressBar = view.findViewById(R.id.fragment2_progressbar);
        audio_albumart = view.findViewById(R.id.audio_albumart);
        audio_title = view.findViewById(R.id.audio_title);
        GridView gridView = (GridView) view.findViewById(R.id.gridview01);
        gridView.setAdapter(new ImageAdapter(getLayoutInflater().getContext()));
        play_index = 0;

        stop.setEnabled(false);
        play.setEnabled(true);

        practiceActivity = (Practice) getActivity();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast toast = Toast.makeText(getActivity(), titles[i], Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, Gravity.CENTER, 440); //토스트 메시지 위치 설정
                toast.show(); //토스트 메시지 띄우기

                audio_albumart.setImageResource(images[i]);
                play.setEnabled(false);
                stop.setEnabled(true);
                audio_title.setText(titles[i]);

                practiceActivity.getBluetoothService().write(sendTo[i]);
            }
        });

        return view;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;
            if (view == null) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);

            } else {
                imageView = (ImageView) view;
            }
            imageView.setImageResource(images[i]);
            return imageView;
        }
    }

}
