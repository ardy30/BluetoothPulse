package com.example.administrator.fk;

/**
 * Created by Administrator on 2014/7/19.
 * 一般来说，常量的命令方式为大写
 */
public class CommonMsg {

    /***************以下是蓝牙状态方面的信息***********/
    //请求连接蓝牙
    public static final int MSG_CONNECT = 0;

    //蓝牙已经连接
    public static final int MSG_CONNECTED = 1;

    //代表发送的是状态信息
    public static final int MSG_STATUS = 2;

    //显示脉搏数
    public static final int MSG_MAIBOSHU= 3;


    /***********以下是调节坐标的信息******************/
    public static final int MSG_DOWN = 20;

    public static final int MSG_UP = 21;

    public static final int MSG_LEFT = 22;

    public static final int MSG_RIGHT = 23;

    public static final int MSG_CLICK = 24;

    public static final int MSG_DOUBLECLICK = 25;

}
