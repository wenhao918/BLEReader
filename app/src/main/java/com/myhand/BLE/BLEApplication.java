package com.myhand.BLE;

import android.Manifest;
import android.app.Application;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 2017/10/7.
 */

public class BLEApplication extends Application{
    private static final String tag=BLEApplication.class.getSimpleName();

    //蓝牙适配器--本地蓝牙
    public BluetoothAdapter bluetoothAdapter;
    //已发现蓝牙
    public List<BluetoothDevice> bluetoothDeviceList;

    public BLEReader bleReader;

    public static BLEApplication instance;

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bluetoothDeviceList=new ArrayList<BluetoothDevice>();
        if(!initBLEDevice()){
            Log.d(tag,String.format("设备初始化失败：(%d)%s",bleReader.getErrorCode(),bleReader.getErrorMessage()));
        }

        instance=this;
    }

    private boolean initBLEDevice(){
        //初始化BLE
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter==null||!bluetoothAdapter.isEnabled()){
            Intent intentEnableBLE=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intentEnableBLE);
        }

        bleReader=new BLEReader();
        bleReader.setBluetoothAdapter(bluetoothAdapter);

        return true;
    }

    public boolean suportBLE(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public class BLEService extends Service{

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
