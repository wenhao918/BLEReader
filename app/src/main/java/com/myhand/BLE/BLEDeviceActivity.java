package com.myhand.BLE;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.vincent.blereader.R;

public class BLEDeviceActivity extends AppCompatActivity {
    private static final String tag=BLEDeviceActivity.class.getSimpleName();
    private TextView textViewBleBluetoothDeviceName;
    private ExpandableListView bleExpandableListView;
    private BLEAdapter bleAdapter;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bleAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bledevice);
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

        //bleDevice
        textViewBleBluetoothDeviceName=(TextView)findViewById(R.id.textViewDevice);
        textViewBleBluetoothDeviceName.setText(String.format("%s %s",BLEApplication.instance.bleReader.getBluetoothDevice().getName(),
                BLEApplication.instance.bleReader.getBluetoothDevice().getAddress()));

        bleExpandableListView=(ExpandableListView)findViewById(R.id.expandableListViewDevice);
        bleAdapter=new BLEAdapter(this,BLEApplication.instance.bleReader);
        bleExpandableListView.setAdapter(bleAdapter);

        //接收服务通知
        BLEApplication.instance.bleReader.setHandler(handler);
    }

}
