package com.example.administrator.fk;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class DetailFragment extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tvStatus = null;
    private TextView tvMaiBo = null;

    private Switch connectToggle = null;
    public Timer tm = null;

    private File maiBoFile = null;
    private String path = "";
    private long[] vib = {1000,2000};

    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //获得状态通知控件
        tvStatus = (TextView)rootView.findViewById(R.id.textViewDetail1);
        tvMaiBo = (TextView)rootView.findViewById(R.id.textViewDetail2);

        //获取状态转换控件
        connectToggle = (Switch)rootView.findViewById(R.id.switchDetail1);
        connectToggle.setOnClickListener(MyClick);
        connectToggle.setChecked(true);

        //请求连接蓝牙
        mHandler.sendEmptyMessageDelayed(CommonMsg.MSG_CONNECT,1000);

        //不能停止绘制
        CommonList.isStopDraw = false;

        //根据条件记录脉搏波数目的表头
        initWriteFile();

        return  rootView;
    }

    //private class
    private View.OnClickListener MyClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.switchDetail1){
                //从运行状态切换到停止状态
                if(!connectToggle.isChecked()){
                    sendUIMessage("状态：已断开连接！",CommonMsg.MSG_STATUS);
                    btDisconnect();

                    //同时注销定时器
                    if(tm!=null){
                        task.cancel();
                    }
                }else{
                    //从停止状态切换至运行状态
                    btConnect();
                    sendUIMessage("状态：连接成功！",CommonMsg.MSG_STATUS);
                    task.run();
                }
            }

        }
    };

    //初始化读写情况
    public void initWriteFile(){
        //只有确认需要记录，才会记录以及判断一下内容
        if(PersonInfo.isRecord){

            //代表可以读写，而且初始化了一切
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                PersonInfo.canBeWrited = true;
                path = Environment.getExternalStorageDirectory().toString()+File.separator+"MaiBO"+File.separator+
                        "maibodata.txt";
                maiBoFile = new File(path);
                //如果父文件夹不存在就创建该文件夹
                if(!maiBoFile.getParentFile().exists())
                    maiBoFile.getParentFile().mkdirs();
                String header = "\n姓名："+ PersonInfo.name+"\t年龄："+PersonInfo.age+
                        "\t性别："+ PersonInfo.gender+"\n时间\t脉率\n"+
                        "---------------------------\n";
                OutputStreamWriter optw = null;
                try{
                    optw = new OutputStreamWriter(new FileOutputStream(maiBoFile,true));
                    optw.append(header);
                    optw.close();
                }catch (IOException ioe){

                }

            }else{
                toastDisplay("存储卡不存在或者不刻度！");
                //代表文件不可读
                PersonInfo.canBeWrited = false;
            }

        }
    }

    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
    public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }


    public void saveMaiBoNums(String msg){
        //需要记录，且SD卡可写
        if(PersonInfo.isRecord&&PersonInfo.canBeWrited){
            SimpleDateFormat sDateFormat  =  new  SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
            String date =  sDateFormat.format(new java.util.Date());
            String res = date+"\t"+msg+"\n";
            OutputStreamWriter ostw = null;
            //写入文件头
            try {
                ostw = new OutputStreamWriter(new FileOutputStream(maiBoFile,true));
                ostw.append(res);
                ostw.close();
            }catch (IOException ioe){
                Log.e("异常：",ioe.getMessage());
            }
            //写入文件中
        }
    }

    //发送包含内容的信息
    private void sendUIMessage(String str_msg,int flags){
        Message msg = new Message();
        msg.what = flags;
        msg.obj = str_msg;
        mHandler.sendMessage(msg);
    }

    //发送空信息
    private void sendUIMessage(int flags){
        mHandler.sendEmptyMessage(flags);
    }


    public static String bytesToHexString(byte[] bytes,int Len) {
        String result = "";
        for (int i = 0; i < Len; i++) {
            String hexString = Integer.toHexString(0xFF-bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }


    public void btDisconnect(){
         CommonList.isStopDraw = true;
    }
    
    public void btConnect(){
        CommonList.comData.clear();
        CommonList.returnData.clear();
        CommonList.isStopDraw = false;
    }

    //启动定时器
    public void startTimer(){
        tm = new Timer();
        tm.schedule(task,CommonList.maiBoDelay*1000,CommonList.maiBoDelay*1000);

    }

    //定时器任务，发送信息到Handler请求更新脉率
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            sendUIMessage(CommonMsg.MSG_MAIBOSHU);
        }
    };

    //注意该函数由于设计到了更改主UI,所以必须放在Handler中调用，否则出错
    public void calcMaiBoNums(){
        int maiBoNums = CommonList.maiBoNums.size();
        //如果脉率不大于0,代表处于没有接受数据的状态
        if(maiBoNums>0) {
            CommonList.maiBoNums.clear();
            //显示脉搏到界面中
            CommonList.addPerMaiBoNums(maiBoNums);
            int totalMaiBo = CommonList.maiBoNumsTimes*CommonList.getTotalMaiBoNums();
            tvMaiBo.setText("脉率：" + String.valueOf(totalMaiBo));
            setButtonColor(tvMaiBo,true);
            //将数值写入文档
            saveMaiBoNums(String.valueOf(totalMaiBo));
            if(totalMaiBo>120){
                Vibrate(getActivity(),vib,true);
                tvMaiBo.setText("脉率："+ String.valueOf(totalMaiBo)+" 心动过速！");
                setButtonColor(tvMaiBo,false);
            }
            if(totalMaiBo<50){
                Vibrate(getActivity(),vib,true);
                tvMaiBo.setText("脉率："+ String.valueOf(totalMaiBo)+" 心动过缓！");
                setButtonColor(tvMaiBo,false);
            }
        }else{
            tvMaiBo.setText("脉率：-");
        }
    }

    public void setButtonColor(TextView tv,boolean flags){
        if(flags){
            tv.setTextColor(getResources().getColor(R.color.darkcyan));
        }else{
            tv.setTextColor(Color.RED);
        }
    }

    //重新连接时，应该重置一下
    public void btResetConnect(){
        if (BlueToothInfo.btInputStream != null) {
            try{
                BlueToothInfo.btInputStream.close();
                BlueToothInfo.btInputStream = null;
            }catch (IOException ioe){
                Log.e("异常：",ioe.getMessage());
            }
        }
        if(BlueToothInfo.btSocket!=null){
            try{
                BlueToothInfo.btSocket.close();
                BlueToothInfo.btSocket = null;
            }catch (IOException ioe){
                Log.e("异常：",ioe.getMessage());
            }
        }

    }

    //显示信息
    public void toastDisplay(String msg){
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    //处理中心
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                //请求连接
                case CommonMsg.MSG_CONNECT:
                    linkThread.start();
                    break;

                //蓝牙已经连接
                case CommonMsg.MSG_CONNECTED:
                    readThread.start();
                    startTimer();
                    break;

                //显示脉率
                case CommonMsg.MSG_MAIBOSHU:
                    calcMaiBoNums();
                    break;

                //发送的是状态信息
                case CommonMsg.MSG_STATUS:
                    tvStatus.setText((String)msg.obj);
                    break;

            }
        }
    };


    //数据连接线程
    private Thread linkThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {

                //注意每次断开时，不要将socket和inputstream断开
                //否则重新连的时候无法连接，不过读的时候进行重置即可。
                btResetConnect();

                //取得设备的接口
                BlueToothInfo.btDevice = BlueToothInfo.btAdapter.getRemoteDevice(
                        BlueToothInfo.btAddress
                );

                BlueToothInfo.btSocket = BlueToothInfo.btDevice.
                        createRfcommSocketToServiceRecord(
                                BlueToothInfo.BT_UUID);

                //如果进行到这一步，代表连接成功
                BlueToothInfo.btSocket.connect();

                //代表蓝牙运行状态
                BlueToothInfo.btStatus = true;

                //发送状态信息代表连接成功
                sendUIMessage("状态：连接成功！",CommonMsg.MSG_STATUS);

                //发送空信息，请求进行数据的读取
                sendUIMessage(CommonMsg.MSG_CONNECTED);

            }catch (IOException ioe){
                sendUIMessage("连接异常!",CommonMsg.MSG_STATUS);
                Log.e("连接异常：",ioe.getMessage());
            }
        }
    });

    //数据读取线程
    private Thread readThread = new Thread(new Runnable() {
        @Override
        public void run() {
            int nRecieve = 0;
            byte[] buffer = new byte[1024];

            try{
                BlueToothInfo.btInputStream = BlueToothInfo.btSocket.getInputStream();
            }catch (IOException e){
                sendUIMessage("数据读取错误！",CommonMsg.MSG_STATUS);
                System.out.println("数据读取错误："+e.getMessage());
            }

            while(BlueToothInfo.btStatus){

                //就算停止绘制，但是数据必须时刻都在读，否则会堵塞程序
                try {
                    //如果数据绘制完成，开始读取数据，nRecieve通常一次读的在1-3个左右
                    if ((nRecieve = BlueToothInfo.btInputStream.read(buffer, 0, 1)) < 1) {
                        try {
                            Thread.sleep(50);
                            continue;
                        } catch (InterruptedException ie) {
                            Log.e("错误：", ie.getMessage());
                        }
                    }
                }catch (IOException ioe){
                    Log.e("错误：", ioe.getMessage());
                }

                //如果是在主页，此线程循环，但是不能死掉，否则不易重启
                if(CommonList.isStopDraw){
                    try{
                        Thread.sleep(50);
                        continue;
                    }catch (InterruptedException ie){
                        Log.e("数据读取错误：",ie.getMessage());
                    }

                }

                //首先判断数据有没有绘制完成，没有的画就不进行读取了
                if(!CommonList.isDrawed){
                    try{
                        Thread.sleep(50);
                        continue;
                    }catch (InterruptedException ie){
                        Log.e("错误：",ie.getMessage());
                    }

                }

                //转换读取的数据格式,nRecieve代表数据的长度
                String data = bytesToHexString(buffer,nRecieve);

                //因为CommonList.class包含很多的公共数据，那么多进行同步更好
                //可以是公共数据中的某一个数据同步也可以的

                //如果Flags为true，那么可知数据量够了，可以进行画图了
                if(CommonList.dataFlags){
                    CommonList.isDrawed = false;
                    CommonList.dataFlags = false;
                }else{
                    CommonList cl = new CommonList();
                    cl.add(data);
                }
            }
        }
    });
}
