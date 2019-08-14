package com.example.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {
    Thread counter;
    int mCount = 0;
    boolean bCount = true;
    private final IBinder mBinder = new MyService.LocalBinder();

    public class LocalBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        counter = new Thread(new Runnable(){
            public void run() {
                while (bCount) {
                    try {
                        Thread.sleep(1000);
                        mCount++;
                    } catch (Exception e) {
                    }
                }
            }
        });
        counter.start();

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bCount = false;
        return super.onUnbind(intent);
    }
    public int getCount(){
        return mCount;
    }
}
