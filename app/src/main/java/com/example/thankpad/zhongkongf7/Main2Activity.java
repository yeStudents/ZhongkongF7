package com.example.thankpad.zhongkongf7;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_tcpstart, bt_tcpsend1;
    private EditText et_text, et_text1;
    private static final String TAG = "Main2Activity-------->";
    private static final String addreeip = "192.168.1.16";
    private static final int port = 4662;
    private String app_text, receData;
    private Socket socket;
    // 获取输出流与输入流
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    et_text1.setText((CharSequence) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bt_tcpstart = findViewById(R.id.bt_tcpstart);
        bt_tcpsend1 = findViewById(R.id.bt_tcpsend1);
        et_text = findViewById(R.id.et_text);
        et_text1 = findViewById(R.id.et_text1);
        bt_tcpstart.setOnClickListener(this);
        bt_tcpsend1.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_tcpstart:
                String btContent = bt_tcpstart.getText().toString();
                if (btContent.equals("TCP启动")) {
                    bt_tcpstart.setText("TCP停止");
                    bt_tcpstart.setBackgroundColor(getResources().getColor(R.color.colorAccent1));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = new Socket(addreeip, port);
                                if (socket == null) {
                                    socket.isConnected();
                                    Log.i(TAG, "开始连接！！!");
                                }
                                Log.i(TAG, "连接成功！！!");
                                //连接超时
                                  socket.setSoTimeout(8000);

                                // 获取输入流接收信息
                                while (socket.isConnected() == true) {
                                    inputStream = socket.getInputStream();
                                    byte[] buf = new byte[1024];
                                    int len = inputStream.read(buf);
                                    //注意charset.forName 字符编码，utf-8中文。。。。。
                                    receData = new String(buf, 0, len, Charset.forName("ASCII"));
                                    Log.i(TAG, receData);
                                    Message message = Message.obtain();
                                    message.obj = receData;
                                    message.what = 0;
                                    handler.sendMessage(message);
                                    handler.sendEmptyMessage(1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.i(TAG, "连接失败！！!" + e.toString());
                            }
                        }
                    }).start();

                } else {
                    bt_tcpstart.setText("TCP启动");
                    bt_tcpstart.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bt_tcpsend1:

                tcp_start();
                break;
        }
    }

    private void tcp_start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                app_text = et_text.getText().toString().trim();
                try {
                    if (app_text.equals("")) {
                        Log.i(TAG, "输入不能为空");
                    } else {
                        // 获取输出流发送信息
                        outputStream = socket.getOutputStream();
                        //注意charset.forName 字符编码，utf-8中文。。。。。
                        byte[] sendData = app_text.getBytes(Charset.forName("ASCII"));
                        outputStream.write(sendData, 0, sendData.length);
                        outputStream.flush();
//                        socket.shutdownOutput();
//                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
            handler.removeCallbacksAndMessages(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
