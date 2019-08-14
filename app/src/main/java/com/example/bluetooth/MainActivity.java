package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;
    UUID MY_UUID;
    BluetoothSocket socket = null;
    OutputStream out = null;
    InputStream inputStream = null;

    ReadThread readThread = null;
    WriteThread writeThread = null;

    Handler writeHandler;

    EditText led;
    TextView switchText, name, mac;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        led = findViewById(R.id.led);
        switchText = findViewById(R.id.switchtext);
        send = findViewById(R.id.send);
        mac = findViewById(R.id.mac);
        name = findViewById(R.id.name);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            // Request discover from BluetoothAdapter
            bluetoothAdapter.startDiscovery();
        }
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
                            name.setText(d.getName());
                            mac.setText(d.getAddress());

                            ClientThread clientThread=new ClientThread();
                            clientThread.start();
                        }
                    }
//                    try {
//                        bluetoothAdapter.cancelDiscovery();
//                        socket = device.createRfcommSocketToServiceRecord(MY_UUID);
//                        socket.connect();
//                        out = socket.getOutputStream();
//                        inputStream = socket.getInputStream();
//
//                        readThread = new ReadThread(socket);
//                        readThread.start();
//                        if(writeThread != null){
//                            writeHandler.getLooper().quit();
//                        }
//                        writeThread=new WriteThread(socket);
//                        writeThread.start();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            });
            builder.create().show();
        }
        else{
            Toast.makeText(getApplicationContext(), "연결가능기기없음", Toast.LENGTH_SHORT).show();
        }
    }

    // 2. 선택한 기기와 연결
    class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                //
                bluetoothAdapter.cancelDiscovery();

                // 블루투스 연결 시작!!!
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();

                // 읽기 쓰레드 시작
                readThread = new ReadThread(socket);
                readThread.start();

                // 쓰기 쓰레드 시작!!
                if(writeThread != null){
                    writeHandler.getLooper().quit();
                }
                writeThread = new WriteThread(socket);
                writeThread.start();


            } catch (IOException e) {
                e.printStackTrace();
                switchText.setText("IOException");

            }
        }
    }


    //
    class ReadThread extends Thread {
        BluetoothSocket socket;
        //BufferedInputStream in = null;
        InputStream in = null;
        public ReadThread(BluetoothSocket socket) {
            this.socket = socket;
            try {
               // in = new BufferedInputStream(socket.getInputStream());
                in =  socket.getInputStream();
            } catch (Exception e) {
            }
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
                        switchText.setText(readStr);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    // 전송 버튼을 눌렀을때
    public void onClick(View v) {
        if (!led.getText().toString().trim().equals("")) {
            Message msg = new Message();
            msg.obj = led.getText().toString();
            writeHandler.sendMessage(msg);
            led.setText("");
        }
    }

    // 쓰기 쓰레드
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

    @Override
    public void onStart() {
        super.onStart();
        // 블루투스 활성화 확인
        // 안되어 있으면 강제 ON
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }
}
