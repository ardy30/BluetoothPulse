package com.example.administrator.fk;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //ListView的配置
    public ListView mLV; //存储蓝牙配对列表
    public static ArrayAdapter<String> AAdapters;
    public static List<String> lstDevices = new ArrayList<String>();


    //获得MainFragment实例的方法
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //初始化ListView
        mLV = (ListView)rootView.findViewById(R.id.listViewDevice);
        AAdapters = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,lstDevices);
        mLV.setAdapter(AAdapters);

        //监听Item的单击事件
        mLV.setOnItemClickListener(new ItemClickEvent());

        //初始化蓝牙设备
        Broad_Init();

        //返回视图
        return rootView;
    }

    //如果本页中含有广播，那么必须注销广播
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReciver);
    }

    public void Broad_Init(){
        //打开蓝牙
        BluetoothAdapter.getDefaultAdapter().enable();

        //声明，如果系统发送查找的广播，我也要接受一份
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReciver, discoveryFilter);

        IntentFilter foundFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReciver, foundFilter);

        //搜索已经配对成功的蓝牙
        //Alt+Enter自动import所需的类
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if(pairedDevices.size()>0){
            for(BluetoothDevice device:pairedDevices){
                String toBeAdd = device.getName()+"\r\n"+device.getAddress();
                if(!lstDevices.contains(toBeAdd)){
                    lstDevices.add(toBeAdd);
                    AAdapters.notifyDataSetChanged();
                }
            }

        }
    }

    /**初始化广播
     * 如果搜到对应的广播，那么就事件交给对应的广播处理类处理*/
    BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据动作，判断执行方法
            String action = intent.getAction();

            //如果找到设备，并且设备不在列表中将设备显示在列表
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String ToBeAdd = device.getName()+"\r\n"+device.getAddress();
                if(device.getBondState()!=BluetoothDevice.BOND_BONDED&&!lstDevices.contains(ToBeAdd)){
                    lstDevices.add(ToBeAdd);
                    AAdapters.notifyDataSetChanged();
                }
            }

            /**如果搜索完成
             * 1.检查List<string>中的数目，如果等于0，代表没有搜到设备。
             * 2.如果大于0，代表搜索到了了设备。
             * 3.另外，语句：uetoothAdapter.getDefaultAdapter也会触发搜索完成的语句*/
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if(lstDevices.size()==0){
                    toastDisplay("没有搜索到设备！");
                }
                if(lstDevices.size()>0){
                    toastDisplay("搜索完成！");
                }
            }

        }
    };


     //如果单击Listview其中一项，那么返回到DetailFragment
     class ItemClickEvent implements  AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //获取该项目的信息
            String ItemInfo = lstDevices.get(i);

            /**
             *1.对于Java中，所有语言共有的转义只需要1个\,但是正则中的转义需要两个\\,比如
             *2.\\d,\\w,\\1等等。
             *3.单击栏目是，自动将地址和名称赋值给蓝牙类的信息中*/
            BlueToothInfo.btName = ItemInfo.split("\r\n")[0];
            BlueToothInfo.btAddress = ItemInfo.split("\r\n")[1];

            //Context代表的就是getActivity,不要加this，指的就是主MyActivity
            AlertDialog.Builder StopDialog = new AlertDialog.Builder(getActivity());
            StopDialog.setTitle("连接");
            StopDialog.setIcon(android.R.drawable.ic_dialog_info);
            StopDialog.setMessage(ItemInfo);
            //左边按钮，取消信息
            StopDialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            //直接进入
            StopDialog.setNeutralButton("直接进入",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    bt_detail();
                }
            });

            //
            StopDialog.setPositiveButton("记录信息",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    writeForm();
                }
            });

            StopDialog.show();
        }
    }

    //显示细节Fragment
    public void bt_detail(){
        DetailFragment df = new DetailFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container,df);
        ft.addToBackStack(null);
        ft.commit();

        //开始绘制
        CommonList.isStopDraw = false;

        //清空之前的数据
        CommonList.comData.clear();
        CommonList.returnData.clear();
    }

    //进入填写表单
    public void writeForm(){
        FormFragment ff = new FormFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container,ff);
        ft.addToBackStack(null);
        ft.commit();
    }

    //显示信息
    public void toastDisplay(String msg){
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
