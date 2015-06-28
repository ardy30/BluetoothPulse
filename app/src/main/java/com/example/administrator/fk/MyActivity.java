package com.example.administrator.fk;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;


public class MyActivity extends Activity {
    public boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //加载MainFragment
        MainFragment mf = new MainFragment();
        //MainFragment mf = MainFragment.newInstance(null,null);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //R.id.container是activity_my的id，这个可能需要自己设置
        ft.add(R.id.container,mf);
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            justExit();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void justExit(){
        if(!isExit){
            isExit = true;
            toastDisplay("两秒内再次按返回键将退出程序！");
            mHandler.sendEmptyMessageDelayed(0,2000);

        }else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            BluetoothAdapter.getDefaultAdapter().disable();
            System.exit(0);

        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    /**ActionBar的操作
     * 1.如果选择主页图标，先检查蓝牙有没有退
     * 2.如果选择搜索图标，也是执行这个操作。*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_main:
                toMainPage();
                break;

            case R.id.action_search:
                toMainPage();
                BlueToothInfo.btAdapter.startDiscovery();
                toastDisplay("请稍后，正在搜索设备...");
                break;

            case R.id.action_open:
                String path = Environment.getExternalStorageDirectory().toString()+File.separator+"MaiBO"+File.separator+
                        "maibodata.txt";
                openFile(path);
                break;
        }
        //含有return的语句放在最尾，否则放在最先
        return super.onOptionsItemSelected(item);
    }

    public void openFile(String path){


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File f = new File(path);

        if(f.exists()) {
            intent.setDataAndType(Uri.fromFile(f), "text/plain");
            startActivity(intent);
        }else{
            toastDisplay("文件不存在，请先记录！");
        }

        toMainPage();

    }
    //切换到MainFragment
    public void toMainPage(){
        MainFragment mf = new MainFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, mf);
        ft.addToBackStack(null);
        ft.commit();
        //停止绘制
        CommonList.isStopDraw = true;
        BlueToothInfo.btStatus = false;
    }

    //显示信息
    public void toastDisplay(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
