package com.myhand.BLE;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.centerm.smartpos.util.HexUtil;
import com.example.vincent.blereader.R;
import com.zhicheng.cmd.CommandActivity;

public class BLEDeviceActivity extends AppCompatActivity {
    private static final String tag=BLEDeviceActivity.class.getSimpleName();

    private static final int REQUEST_SELECTCMD = 1;

    private TextView textViewBleBluetoothDeviceName;

    private Button buttonRefresh;
    private Button buttonWrite;
    private Button buttonClear;
    private Button buttonSelectCmd;

    private ExpandableListView bleExpandableListView;
    private BLEAdapter bleAdapter;

    private EditText editTextSend;
    private EditText editTextReceive;

    private TextView textViewSendCnt;
    private TextView textViewReceiveCnt;
    private Button buttonReset;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bleAdapter.notifyDataSetChanged();
            Log.d(tag,String.format("Service discovered nofified,service count is %d.",
                    BLEApplication.instance.bleReader.getGattServiceList().size()));

            Bundle data=msg.getData();
            if(data!=null){
                byte[] value=data.getByteArray("Response");
                if(value!=null){
                    String lf="";
                    if(!editTextReceive.getText().toString().isEmpty())
                    {
                        lf="\n";
                    }
                    editTextReceive.append(lf+HexUtil.bytesToHexString(value));

                    textViewReceiveCnt.setText(String.format("%d",BLEApplication.instance.bleReader.getReceiveCnt()));
                    textViewSendCnt.setText(String.format("%d",BLEApplication.instance.bleReader.getSendCnt()));
                }
            }
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
        textViewBleBluetoothDeviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEApplication.instance.bleReader.writeData(BLEReader.Cmd_ReadReader.getBytes());
            }
        });
        textViewBleBluetoothDeviceName.setText(String.format("%s %s",BLEApplication.instance.bleReader.getBluetoothDevice().getName(),
                BLEApplication.instance.bleReader.getBluetoothDevice().getAddress()));

        bleExpandableListView=(ExpandableListView)findViewById(R.id.expandableListViewDevice);
        bleAdapter=new BLEAdapter(this,BLEApplication.instance.bleReader);
        bleExpandableListView.setAdapter(bleAdapter);

        //接收服务通知
        BLEApplication.instance.bleReader.setHandler(handler);

        buttonRefresh=(Button)findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleAdapter=new BLEAdapter(BLEDeviceActivity.this,BLEApplication.instance.bleReader);
                bleExpandableListView.setAdapter(bleAdapter);
            }
        });

        buttonWrite=(Button)findViewById(R.id.buttonWrite);
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd=editTextSend.getText().toString();
                if(cmd.isEmpty()){
                    cmd=BLEReader.Cmd_ReadReader;
                    editTextSend.setText(cmd);
                }

                BLEApplication.instance.bleReader.writeData(HexUtil.hexStringToByte(cmd));
                //BLEApplication.instance.bleReader.writeData(BLEReader.Cmd_ReadReader.getBytes());
            }
        });

        editTextSend=(EditText)findViewById(R.id.editTextSend);
        editTextSend.setText(BLEApplication.instance.bleReader.Cmd_ReadReader);
        editTextReceive=(EditText)findViewById(R.id.editTextReceive);

        textViewReceiveCnt=(TextView)findViewById(R.id.textViewReceiveCnt);
        textViewSendCnt=(TextView)findViewById(R.id.textViewSendCnt);

        buttonReset=(Button)findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEApplication.instance.bleReader.resetCnt();
                textViewSendCnt.setText("0");
                textViewReceiveCnt.setText("0");
            }
        });

        buttonClear=(Button)findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextReceive.setText("");
            }
        });

        buttonSelectCmd=(Button)findViewById(R.id.buttonSelectCmd);
        buttonSelectCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(BLEDeviceActivity.this, CommandActivity.class);

                startActivityForResult(intent,REQUEST_SELECTCMD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_SELECTCMD:{
                if(resultCode==1){
                    editTextSend.setText(data.getStringExtra("Cmd"));
                }
            }
        }
    }
}
