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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_tcpstart, bt_tcpsend1;
    private EditText et_text, et_text1;
    private final static String TAG = "Main2Activity-------->";
    private final static String addreeip = "192.168.2.13";
    private final static int port = 8080;
    private String app_text, receData;
    private Socket socket = null;
    // 获取输出流与输入流
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

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
                                inputStream = socket.getInputStream();
                                outputStream = socket.getOutputStream();
                                if (socket.isConnected()) {
                                    Log.i(TAG, "连接成功！！!");
                                }
                              new Thread(new Runnable() {
                                  @Override
                                  public void run() {
                                      //连接超时
                                      //   socket.setSoTimeout(5000);
                                      // 获取输入流接收信息
                                      while (socket.isConnected() == true) {
                                          byte[] buf = new byte[1024];
                                          int len = 0;
                                          try {
                                              len = inputStream.read(buf);
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }
                                          //注意charset.forName 字符编码，utf-8中文。。。。。
                                          receData = new String(buf, 0, len, Charset.forName("ASCII"));
                                          Log.i(TAG, receData);
                                          runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  et_text1.setText(receData);
                                              }
                                          });


                                      }
                                  }
                              }).start();
                                //连接超时
                                //   socket.setSoTimeout(5000);
                                // 获取输入流接收信息
                                while (socket.isConnected() == true) {
                                    byte[] buf = new byte[1024];
                                    int len = inputStream.read(buf);
                                    //注意charset.forName 字符编码，utf-8中文。。。。。
                                    receData = new String(buf, 0, len, Charset.forName("ASCII"));
                                    Log.i(TAG, receData);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            et_text1.setText(receData);
                                        }
                                    });


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
                        //注意charset.forName 字符编码，utf-8中文。。。。。
                        if(outputStream !=null){
                            byte[] sendData = app_text.getBytes(Charset.forName("ASCII"));
                            outputStream.write(sendData, 0, sendData.length);
                            outputStream.flush();
                        }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
