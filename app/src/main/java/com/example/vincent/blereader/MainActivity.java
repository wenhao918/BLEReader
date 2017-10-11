package com.example.vincent.blereader;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.myhand.BLE.BLEApplication;
import com.myhand.BLE.BLEBluetoothDeviceAdapter;
import com.myhand.BLE.BLEDeviceActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    private static final String tag=MainActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT=1;

    private ListView bluetoothDeviceListView;
    private BLEBluetoothDeviceAdapter bluetoothDeviceAdapter;

    private Button btnScan;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        bluetoothDeviceAdapter=new BLEBluetoothDeviceAdapter(this,BLEApplication.instance.bluetoothDeviceList);
        bluetoothDeviceListView=(ListView)findViewById(R.id.listViewBluetoothDevice);
        bluetoothDeviceListView.setAdapter(bluetoothDeviceAdapter);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        initBlueTooth();

        btnScan=(Button)findViewById(R.id.buttonScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice(true);
            }
        });

        Button btnShowDevice=(Button)findViewById(R.id.buttonShowDevice);
        btnShowDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this, BLEDeviceActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initBlueTooth(){
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter=BLEApplication.instance.getBluetoothAdapter();
        if(bluetoothAdapter==null||!bluetoothAdapter.isEnabled()){
            Intent intentEnableBLE=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentEnableBLE,REQUEST_ENABLE_BT);
        }

        //Android6.0需要动态申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //判断是否跟用户做一个说明
                //DialogUtils.shortT(getApplicationContext(), "需要蓝牙权限");
            }
        }

/*
        IntentFilter filter = new IntentFilter();
//发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
//设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);
*/

/*
        mBluetoothAdapter=BLEApplication.instance.getBluetoothAdapter();
        mBluetoothAdapter.startDiscovery();

        Set<BluetoothDevice> devices = BLEApplication.instance.getBluetoothAdapter().getBondedDevices();
        Log.d(tag, "bonded device size ="+devices.size());
        for(BluetoothDevice bonddevice:devices){
            Log.d(tag, "bonded device name ="+bonddevice.getName()+" address"+bonddevice.getAddress());
        }
*/
    }


    Handler mHandler=new Handler();
    boolean mScanning;
    /* 搜索蓝牙设备
     *
             * @param enable
     */
    private void scanLeDevice(boolean enable) {
        // TODO Auto-generated method stub
        if (enable) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mScanning = false;
                    BLEApplication.instance.bluetoothAdapter.stopLeScan(leScanbCallback);
                }
            }, 2000);
            mScanning = true;
            BLEApplication.instance.bluetoothAdapter.startLeScan(leScanbCallback);
        } else {
            mScanning = false;
            BLEApplication.instance.bluetoothAdapter.stopLeScan(leScanbCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback leScanbCallback=new BluetoothAdapter.LeScanCallback(){

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            //Toast.makeText(MainActivity.this,device.getName(),Toast.LENGTH_SHORT).show();
            bluetoothDeviceAdapter.addDevice(device);
            bluetoothDeviceAdapter.notifyDataSetChanged();

/*
            if(device!=null&&device.getName().compareTo(BLEDevice.BLENAME_READER)==0){
                Log.d(tag,String.format("Find device %s %s",device.getName(),device.getAddress()));

                bleDevice.setBluetoothDevice(device);
                bleDevice.getBluetoothAdapter().stopLeScan(this);

                BluetoothGatt gatt=device.connectGatt(MainActivity.this,false,bleDevice.getBluetoothGattCallback());
                if(gatt.connect()){
                    Log.d(tag,String.format("Device %s connected successful",device.getName()));
                }else{
                    Log.d(tag,String.format("Device %s connect failure",device.getName()));
                }
                bleDevice.setBluetoothGatt(gatt);
            }
            Log.d(tag,"'"+device.getName()+"'");
*/
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
