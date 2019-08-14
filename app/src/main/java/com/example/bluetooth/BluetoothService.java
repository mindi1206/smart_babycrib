
package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    private final BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket = null;
    private int state;
    Handler writeHandler;
    private final Handler mHandler;
    private ClientThread clientThread;
    private ReadThread readThread;
    private WriteThread writeThread;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    public static final int STATE_NONE = 0;       // we're doing nothing 아무것도 안하는중
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections 지금 들어오는 연결을 듣고 있다.
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection 이제 나가는 연결 시작
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device 장치에 연결된 상태

    public BluetoothService(Context context,  BluetoothAdapter _bluetoothAdapter, Handler handler) {
        bluetoothAdapter =_bluetoothAdapter;
        state = STATE_NONE;
        mHandler = handler;

    }
    private void setState(int _state) {
        state = _state;
    }
    public int getState(){ return state; }

    public void start(){
        if (clientThread != null) {clientThread.cancel(); clientThread = null;}

        // Cancel any thread currently running a connection
       //  if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        setState(STATE_LISTEN);
    }

    public void connect(BluetoothDevice device){
        clientThread = new ClientThread(device);
        clientThread.start();
        setState(STATE_CONNECTING);
    }

    private class ClientThread extends Thread {
        private final BluetoothDevice device;

        public ClientThread(BluetoothDevice _device) {
            device = _device;
            try{
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch(IOException e){

            }
        }
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                // 블루투스 연결 시작!!!
                bluetoothSocket.connect();

            } catch (IOException e) {
                e.printStackTrace();
                // 연결실패
                setState(STATE_LISTEN);
                try{
                    bluetoothSocket.close();
                }
                catch (IOException e1){

                }
                BluetoothService.this.start();
                return;
            }
            clientThread = null;

            readThread = new ReadThread(bluetoothSocket);
            readThread.start();
            // 쓰기 쓰레드 시작!!
            if(writeThread != null){
                writeHandler.getLooper().quit();
            }
            writeThread = new WriteThread(bluetoothSocket);
            writeThread.start();
            setState(STATE_CONNECTED);
        }

        public void cancel(){
            try{
                bluetoothSocket.close();
            }
            catch (IOException e){

            }
        }
    }

    class ReadThread extends Thread {
       private final BluetoothSocket socket;
        private final InputStream in;

        public ReadThread(BluetoothSocket _socket) {
            socket = _socket;
            InputStream tmp =null;
            try {
                tmp = socket.getInputStream();
            } catch (Exception e) {

            }
            in = tmp;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    bytes = in.read(buffer);
                    String readStr = new String(buffer, 0, bytes);
                    if(!readStr.equals("")){
                        Message msg = new Message();
                        msg.obj = readStr;
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e) {
                }
            }
        }
    }

    public void write (String str){
        Message msg = new Message();
        msg.obj = str;
        writeHandler.sendMessage(msg);
    }

    class WriteThread extends Thread {
        BluetoothSocket socket = null;
        OutputStream out = null;

        public WriteThread(BluetoothSocket socket) {
            this.socket = socket;
            try {
                out = socket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Looper.prepare();
            writeHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        out.write(((String) msg.obj).getBytes());
                        out.flush();
                    } catch (Exception
                            e) {
                        e.printStackTrace();
                        writeHandler.getLooper().quit();
                        try {
                        } catch (Exception e1) {
                        }
                    }
                }
            };
            Looper.loop();
        }
    }
}

