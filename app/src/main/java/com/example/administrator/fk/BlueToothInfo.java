package com.example.administrator.fk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2014/7/17.
 */
public class BlueToothInfo {

    //name of bluetooth
    public static String btName = null;

    //address of bluetooth
    public static String btAddress = null;

    //uuid of bluetooth
    public static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //status of bluetooth, false means disconnected,true means connected
    public static boolean btStatus = false;

    //input stream of bt socket
    public static InputStream btInputStream = null;

    //socket of bt
    public static BluetoothSocket btSocket = null;

    //remote bluetooth device
    public static BluetoothDevice btDevice = null;

    //local bluetooth device
    public static BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

}
