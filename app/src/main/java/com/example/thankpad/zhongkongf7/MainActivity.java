package com.example.thankpad.zhongkongf7;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
        new  Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection  connection = DriverManager.getConnection("jdbc:mysql://192.168.1.15/3306/android_test", "root", "123456");

                    if(connection != null){
                        System.out.println("mysql连接成功");
                    }
                    System.out.println("mysql连接失败");
                    connection.close();

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = new Bundle();
            data = msg.getData();
            System.out.println("name" + data.get("name").toString());
            System.out.println("sex" + data.get("sex").toString());
            System.out.println("age" + data.get("age").toString());
        }
    };

    private void connt_mysql() {
        Runnable runnable = new Runnable() {
             @Override
            public void run() {
//                try {
//                    Class.forName("com.mysql.jdbc.Driver");
//                    Connection  connection = DriverManager.getConnection("jdbc:mysql://192.168.1.100/3306/android_test", "root", "123456");
//                    System.out.println("mysql连接成功");
//                    connection.close();
//
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }


//                try {
//                    test(connection);    //测试数据库连接
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
            }
        };
        new Thread(runnable).start();

    }

    public void test(Connection con1) throws java.sql.SQLException {
        try {
            String sql = "select * from user";        //查询表名为“user”的所有内容
            Statement stmt = con1.createStatement();        //创建Statement
            ResultSet rs = stmt.executeQuery(sql);          //ResultSet类似Cursor

            //<code>ResultSet</code>最初指向第一行
            Bundle bundle = new Bundle();
            while (rs.next()) {
                bundle.clear();
                bundle.putString("name", rs.getString("name"));
                bundle.putString("sex", rs.getString("sex"));
                bundle.putString("age", rs.getString("age"));
                Message msg = new Message();
                msg.setData(bundle);
                myHandler.sendMessage(msg);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {

        } finally {
            if (con1 != null)
                try {
                    con1.close();
                } catch (SQLException e) {
                }
        }
    }

    public void bt_start(View view) {
       // connt_mysql();


}
}
