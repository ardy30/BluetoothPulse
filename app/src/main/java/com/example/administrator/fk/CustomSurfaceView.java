package com.example.administrator.fk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Administrator on 2014/7/18.
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback,
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, View.OnTouchListener {

    public static DrawThread dThread;
    public static boolean isRun = true;
    public static int Pwidth;
    public static int Pheight;
    public static Paint ptbg = new Paint();
    private static SurfaceHolder holder;
    private static float dwidth;
    private static Path pth = new Path();
    private static Paint pt = new Paint();
    private static Paint ptfont = new Paint();
    private GestureDetector gd;


    //如果要做成控件，三个构造都必须
    public CustomSurfaceView(Context context) {
        super(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        holder = this.getHolder();
        holder.addCallback(this);
        dThread = new DrawThread();

        //背景
        pt.setColor(getResources().getColor(R.color.magenta));
        pt.setStrokeWidth(4f);
        pt.setStyle(Paint.Style.STROKE);


        //绘制字体
        ptfont.setColor(Color.DKGRAY);
        ptfont.setStrokeWidth(2f);
        ptfont.setTextSize(40f);

        //背景栅格的背景
        ptbg.setColor(Color.GRAY);
        ptbg.setStrokeWidth(1);
        ptbg.setStyle(Paint.Style.STROKE);


        //只有设置这个，才能处理不同的手势模式
        gd = new GestureDetector(this);
        gd.setIsLongpressEnabled(true);
        this.setLongClickable(true);
        this.setOnTouchListener(this);

        /**
         * 1.如果使用cv.drawColor(getResources().getColor(R.color.unname1), PorterDuff.Mode.SRC);模式
         *   则this.setZOrderOnTop(true)是必须的
         * 2.如果使用cv.drawColor(getResources().getColor(R.color.unname1), PorterDuff.Mode.CLEAR)则好像
         *   不是必须的
         * */
        this.setZOrderOnTop(true);

    }

    //单击
    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        CommonList.changeCord(CommonMsg.MSG_CLICK);
        return false;
    }

    //双击
    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        CommonList.changeCord(CommonMsg.MSG_DOUBLECLICK);
        return false;
    }

    //双击产生两次
    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    //来自于View.OnTouchListener
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gd.onTouchEvent(motionEvent);
        return false;
    }

    //start-继承自：GestureDetector.OnGestureListener

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    //手势判断
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        //右
        if (v > 0 && v > 2000) {
            CommonList.changeCord(CommonMsg.MSG_RIGHT);
        }
        //左
        if (v < 0 && Math.abs(v) > 2000) {
            CommonList.changeCord(CommonMsg.MSG_LEFT);
        }
        //下
        if (v2 > 0 && v2 > 2000) {
            CommonList.changeCord(CommonMsg.MSG_DOWN);
        }
        //上
        if (v2 < 0 && Math.abs(v2) > 2000) {
            CommonList.changeCord(CommonMsg.MSG_UP);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    //end-继承自：GestureDetector.OnGestureListener


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        dThread.start();
        dwidth = getWidth() / (CommonList.lenFactor * CommonList.comDataLen);
        Pwidth = getWidth();
        Pheight = getHeight();
        Canvas cv = null;
        try {
            cv = holder.lockCanvas();
            cv.drawColor(getResources().getColor(R.color.unname1), PorterDuff.Mode.SRC);
            drawBg(cv);

        } catch (Exception e) {

        } finally {
            holder.unlockCanvasAndPost(cv);
        }


    }

    //画背景
    public void drawBg(Canvas cv) {
        //画横轴
        for (int n = 0; n < getHeight(); n += 20) {
            cv.drawLine(0, n, Pwidth, n, ptbg);
        }

        //画纵轴
        for (int m = 0; m < getWidth(); m += 20) {
            cv.drawLine(m, 0, m, Pheight, ptbg);
        }
    }

    //画图例
    public void dragLegend(Canvas cv){
        //图例线段
        float lpax = Pwidth-300f;
        float lpay = Pheight-50f;
        float lpbx = Pwidth-200f;
        float lpby = Pheight-50f;
        cv.drawLine(lpax,lpay,lpbx,lpby,pt);

        //图例说明
        cv.drawText("脉搏波形",lpbx+20f,lpby+15f,ptfont);


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }


    class DrawThread extends Thread {
        @Override
        public void run() {
            super.run();
            Canvas cv = null;
            while (isRun) {
                //如果当前界面是主页，但是不想让该线程停止，只能让它循环
                if (CommonList.isStopDraw) {
                    try {
                        Thread.sleep(50);
                        continue;
                    } catch (InterruptedException ie) {
                        Log.e("异常：", ie.getMessage());
                    }
                    continue;
                }

                //如果数据已绘制，代表正在接受数据，这边线程等待中
                if (CommonList.isDrawed) {
                    try {
                        Thread.sleep(50);
                        continue;

                    } catch (InterruptedException ie) {
                        Log.e("异常：", ie.getMessage());
                    }
                }


                try {
                    cv = holder.lockCanvas();
                    //以下为清屏的代码，Surfaview不清屏，会导致画图叠加
                    cv.drawColor(getResources().getColor(R.color.unname1), PorterDuff.Mode.SRC);

                    //绘制背景
                    drawBg(cv);
                    //绘制图例
                    dragLegend(cv);

                    //取得返回的数据长度
                    int returnDataSize = CommonList.returnData.size();

                    //因为CommonList包含公共数据，所以要进行同步
                    pth.reset();
                    //如果没有这句，所有的点都从(0,0)开始
                    pth.moveTo(Pwidth, CommonList.returnData.get(returnDataSize - 1));

                    for(int i=1;i<returnDataSize-1;i++){
                        float px = Pwidth-i*dwidth;
                        float py = CommonList.returnData.get(i);
                        float ax = Pwidth-(i+1)*dwidth;
                        float ay = CommonList.returnData.get((i+1));
                        pth.quadTo(px,py,ax,ay);
                    }

                    cv.drawPath(pth, pt);
                    //如果绘制完成
                    CommonList.isDrawed = true;
                    //代表数据是空的，可以开始读取数据了
                    CommonList.dataFlags = false;

                } catch (Exception e) {
                    Log.e("错误：",e.getMessage());

                } finally {
                    //提交画布
                    if (cv != null) {
                        holder.unlockCanvasAndPost(cv);
                    }

                }
            }
        }
    }
}





