package com.example.bluetooth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentB extends Fragment {
    ImageView stateimage;
    TextView statetext;
    String name;

    String text;
    int imageID;

    public FragmentB() {
        // Required empty public constructor
    }
    public static FragmentB newInstance(){
        Bundle args = new Bundle();
        FragmentB fragment = new FragmentB();

        fragment.setArguments(args);
        return fragment;
    }
    public void onCreate(Bundle b){
        super.onCreate(b);
//        name = "서준이가 ";
//        text = name + "활동중이에요!";
//        imageID =  R.drawable.toy;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);
        stateimage =  view.findViewById(R.id.stateimage);
        statetext = view.findViewById(R.id.statetext);
        Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.babyanimation);
        stateimage.startAnimation(animation);

        if(savedInstanceState != null){
            text = savedInstanceState.getString("TEXT");
            imageID = savedInstanceState.getInt("IMAGE_ID");
        }
        //setChange();
        return view;
    }

    public void setState(String str){
        if(str.equals("WAKEUP")){
            text = name + "활동중이에요!";
            imageID =  R.drawable.toy;
            setChange(text, imageID);
        }
        else if(str.equals("SLEEP")){
            text = name + "잠들었어요!";
            imageID =  R.drawable.moon;
            setChange(text, imageID);
        }
        else if(str.equals("DIAPER")){
            text = name + "소변을 했어요!";
            imageID =  R.drawable.diaper;
            setChange(text, imageID);
        }
    }
    public void setChange(){
        Practice practice =  (Practice)getActivity();
        practice.sendState();
    }
    public void setChange(String text, int imageID){
        stateimage.setImageResource(imageID);
        statetext.setText(text);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putInt("IMAGE_ID", imageID);
        outState.putString("TEXT", text);
    }
}
