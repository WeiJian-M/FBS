package com.example.fbsappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private Button btn_MOTO;
    private Button btn_OPEN;
    private Button btn_CLOSE;
    private TextView tv_myIP;
    private TextView tv_data;
    private String DATA = "";
    private String CODE = "";
    //创建锁对象
    Object obj = new Object();
    private ServerSocket ss;
    private Socket s;
    public static final int UPDATE_TEXT = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_TEXT:
                    tv_data.setText(DATA);
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "服务已开启", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "客户端已连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_MOTO = (Button) findViewById(R.id.btn_MOTO);
        btn_OPEN = (Button) findViewById(R.id.btn_OPEN);
        btn_CLOSE = (Button) findViewById(R.id.btn_CLOSE);
        tv_myIP = (TextView) findViewById(R.id.tv_myIP);
        tv_data = (TextView) findViewById(R.id.tv_data);



        // 显示IPV4地址
        tv_myIP.setText(IPUtils.getIpAddress());


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ss = new ServerSocket(8888);
                    System.out.println("服务器已启动");
                    Message message1 = new Message();
                    message1.what = 2;
                    handler.sendMessage(message1);
                    s = ss.accept();
                    System.out.println("客户端已连接");
                    Message message2 = new Message();
                    message2.what = 3;
                    handler.sendMessage(message2);

                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                while(true){
                                    synchronized(obj){
                                        obj.wait();
                                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                                        if("".equals(CODE)){
//                                System.out.println(CODE);
                                        }else{
                                            System.out.println(CODE);
                                            bw.write(CODE+"\n");
                                            bw.flush();
                                        }
                                    }
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                while(true){
                                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                    String mess = br.readLine();

                                    System.out.println(mess);
                                    DATA = mess;
                                    Message message = new Message();
                                    message.what = UPDATE_TEXT;
                                    handler.sendMessage(message);
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        // MOTO
        btn_MOTO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        synchronized(obj) {
                            CODE = "MOTO";
//                            System.out.println(CODE);
                            obj.notify();
                        }
                    }
                }).start();
            }
        });

        // OPEN
        btn_OPEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {

                        synchronized(obj) {
                            CODE = "OPEN";
//                            System.out.println(CODE);
                            obj.notify();
                        }
                    }
                }).start();
            }
        });

        // CLOSE
        btn_CLOSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        synchronized(obj) {
                            CODE = "CLOSE";
//                            System.out.println(CODE);
                            obj.notify();
                        }
                    }
                }).start();
            }
        });
    }
}