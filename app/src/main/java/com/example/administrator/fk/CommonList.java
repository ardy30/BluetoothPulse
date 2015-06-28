package com.example.administrator.fk;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/20.
 */
public class CommonList {
    //设置公共数据
    public static List<String> comData = new ArrayList<String>();

    //设置公共数据的最大长度
    public static int comDataLen = 10;

    //返回的总的数据的大小乘积因子
    public static int lenFactor = 10;

    //数据放大的倍数
    public static int dataMagnity = 200;

    //如果false代表不满数据长度
    public static boolean dataFlags = false;

    //下位机传来的数据，要先除以转换精度，然后再乘以最高幅值，才能得到正确的结果
    //12位单片机的精度最大值2^CommonList.maiBoDelayNums=4096;2^8=256 McuPrecision
    //由于12位的机器，一次只能发8位，导致发一个数据要发两次，接收算法比较麻烦，现在把它算成一位
    public static int  mcuPrecision = 255;

    public static float mcuVoltage = 2.5f;

    //表示数据是否已经被画
    public static boolean isDrawed = true;

    //这个数据是用来画图用的
    public static List<Float> returnData = new ArrayList<Float>();

    //存储脉搏数的
    public static List<Integer> maiBoNums = new ArrayList<Integer>();

    //脉搏的标志
    public static int maiBoFlags = 0;

    //
    public static float dataOffset = 50f;

    //脉搏个数寄存器
    public static List<Integer> maiBoNumsDeposit = new ArrayList<Integer>();

    //停止绘制和采集数据的线程
    public static boolean isStopDraw = false;
    
    ////key paramerter
    public static final int maiBoDelay = 5;

    //key paramerter
    public static final int maiBoUpdateTime = 20;

    //一分钟计算的脉搏次数
    public static final int maiBoDelayNums = CommonList.maiBoUpdateTime/CommonList.maiBoDelay;

    public static final int maiBoNumsTimes = 60/CommonList.maiBoUpdateTime;

    //调整水平或者数值方向的坐标
    public static void changeCord(int msg){
        switch (msg){
            case CommonMsg.MSG_UP:
                CommonList.dataOffset-=20f;
                break;
            case CommonMsg.MSG_DOWN:
                CommonList.dataOffset+=20f;
                break;
            case CommonMsg.MSG_LEFT:
                if(CommonList.lenFactor>4){
                    CommonList.lenFactor-=2;
                }
                break;
            case CommonMsg.MSG_RIGHT:
                if(CommonList.lenFactor<20){
                    CommonList.lenFactor+=2;
                }
                break;
            case CommonMsg.MSG_CLICK:
                if(CommonList.dataMagnity>50){
                    CommonList.dataMagnity-=40;
                }
                break;
            case CommonMsg.MSG_DOUBLECLICK:
                if(CommonList.dataMagnity<400){
                    CommonList.dataMagnity+=40;
                }
                break;
        }
    }


    public void add(String str_add){
        //只有幅值从小于125到大于125的情况下，才会让添加1
        Integer intTmp = Integer.parseInt(str_add,16);
        Log.e("信息：",String.valueOf(intTmp));
        if(intTmp<170){

            if(CommonList.maiBoFlags==0){
                CommonList.maiBoNums.add(1);
            }
            CommonList.maiBoFlags = 1;
        }else{
            CommonList.maiBoFlags = 0;
        }

        //普通的添加数据
        if(CommonList.comData.size()<CommonList.comDataLen-1){
            //如果数据长度小于20，添加数据
            CommonList.comData.add(str_add);
        }else{
            //如果数据长度等于25，改变数据格式
            CommonList.comData.add(str_add);
            //格式化数据
            formatData();
            //清除数据
            CommonList.comData.clear();
            //注意调用ReturnData之后要将Flags设置成false
            CommonList.dataFlags = true;

        }
    }

    //每5秒钟添加一次脉搏数
    public static void addPerMaiBoNums(Integer toBeAdd){
        //由于5秒加入一次，那么12次就可以了
        if(CommonList.maiBoNumsDeposit.size()==CommonList.maiBoDelayNums){
            CommonList.maiBoNumsDeposit.remove(0);
            CommonList.maiBoNumsDeposit.add(toBeAdd);
        }else{
            CommonList.maiBoNumsDeposit.add(toBeAdd);
        }
    }

    //每5秒钟取一次总的脉搏数
    public static Integer getTotalMaiBoNums(){
        int res=0;
        for(int i=0;i<CommonList.maiBoNumsDeposit.size();i++){
            res+=CommonList.maiBoNumsDeposit.get(i);
        }
        return res*CommonList.maiBoDelayNums/CommonList.maiBoNumsDeposit.size();
    }


    public void formatData(){
        int tmpDataLen= CommonList.comDataLen*CommonList.lenFactor;
        for(int i=0;i<CommonList.comDataLen;i++){
            if(CommonList.returnData.size()==CommonList.comDataLen*CommonList.lenFactor){
                for(int j=0;j<CommonList.comDataLen;j++){
                    CommonList.returnData.remove(tmpDataLen-j-1);
                }
            }
            //此时期望的是str_tmp是FCC0这种格式
            String strTmp = CommonList.comData.get(i);
            //parseInt("CC0",16)这种格式
            Integer intTmp = Integer.parseInt(strTmp,16);
            //float float_tmp代表实际数据
            Float floatTmp = (float)((CommonList.dataMagnity *intTmp*CommonList.mcuVoltage)/(CommonList. mcuPrecision))
                    +CommonList.dataOffset;
            //返回的格式要转换
            CommonList.returnData.add(0, floatTmp);
        }
    }
}
