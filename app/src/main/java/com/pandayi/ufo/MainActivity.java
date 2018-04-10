package com.pandayi.ufo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private static byte data[] = new byte[34];
    private boolean bool = false;
    private Socket socket;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //数据初始化
        initdata();

        //油门SeekBar
        SeekBar youmenSB = findViewById(R.id.youmen);
        youmenSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView tv = findViewById(R.id.youmentv);
                tv.setText("油门值" + progress);
                data[3] = (byte) (progress >> 8 & 0xff);
                data[4] = (byte) (progress & 0xff);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //航向SeekBar
        SeekBar hangxiangSB = findViewById(R.id.hangxiang);
        hangxiangSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView tv = findViewById(R.id.hangxiangtv);
                tv.setText("航向（顺逆）" + progress);
                data[5] = (byte) (progress >> 8 & 0xff);
                data[6] = (byte) (progress & 0xff);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //横滚SeekBar
        SeekBar henggunSB = findViewById(R.id.henggun);
        henggunSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView tv = findViewById(R.id.hengguntv);
                tv.setText("横滚（右左）" + progress);
                data[7] = (byte) (progress >> 8 & 0xff);
                data[8] = (byte) (progress & 0xff);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //俯仰SeekBar
        SeekBar fuyangSB = findViewById(R.id.fuyang);
        fuyangSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView tv = findViewById(R.id.fuyangtv);
                tv.setText("俯仰（后前）" + progress);
                data[9] = (byte) (progress >> 8 & 0xff);
                data[10] = (byte) (progress & 0xff);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void connect(View view) {
        new Thread(new ConnectUFO()).start();
    }

    public void ss(View view) {
        Button bt = findViewById(R.id.ss);
        if (!bool) {
            bool = true;
            bt.setText("Stop");
            new Thread(new Senddata()).start();
        } else {
            bt.setText("Start");
            bool = false;
        }
    }

    //连接线程
    private class ConnectUFO implements Runnable {
        @Override
        public void run() {
            try {
                socket = new Socket("192.168.4.1", 333);
                OutputStream out = socket.getOutputStream();//调用输出流
                out.write("GEC\r\n".getBytes());//发送连接数据
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //控制线程
    private class Senddata implements Runnable {

        @Override
        public void run() {

            try {
                OutputStream out = socket.getOutputStream();//调用输出流
                while (bool) {
                    out.write(data);//发送数据
                    Thread.sleep(5);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //上下左右button
    public void doclick(View view) {

        switch (view.getId()) {
            case R.id.qian:
                SeekBar qiansb = findViewById(R.id.fuyang);
                qiansb.setProgress(qiansb.getProgress() + 100);
                break;
            case R.id.hou:
                SeekBar housb = findViewById(R.id.fuyang);
                housb.setProgress(housb.getProgress() - 100);
                break;
            case R.id.zuo:
                SeekBar zuosb = findViewById(R.id.henggun);
                zuosb.setProgress(zuosb.getProgress() + 100);
                break;
            case R.id.you:
                SeekBar yousb = findViewById(R.id.henggun);
                yousb.setProgress(yousb.getProgress() - 100);
                break;
        }
    }

    private void initdata() {
        data[0] = (byte) 0xAA;//通信协议固定数据
        data[1] = (byte) 0xC0;//通信协议固定数据
        data[2] = (byte) 0x1C;//通信协议固定数据
        //油门值（控制飞行速度和高度）
        data[3] = (byte) 0x00;//油门值的高八位
        data[4] = (byte) 0x00;//油门值低八位
        //   航向值（控制旋转角度）
        data[5] = (byte) 0x00;//航向值高八位
        data[6] = (byte) 0x00;//航向值低八位
        //   横滚值（控制左右方向）
        data[7] = (byte) 0x00;//横滚值高八位
        data[8] = (byte) 0x00;//横滚值低八位
        //  俯仰值（控制前后方向）
        data[9] = (byte) 0x00;//俯仰值高八位
        data[10] = (byte) 0x00;//俯仰值低八位
        //   剩余其他位置的数据由于默认就是0，并且加油时用，所以省略掉
        data[31] = (byte) 0x1C;//通信协议固定数据
        data[32] = (byte) 0x0D;//通信协议固定数据
        data[33] = (byte) 0x0A;//通信协议固定数据
    }

    public static byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
