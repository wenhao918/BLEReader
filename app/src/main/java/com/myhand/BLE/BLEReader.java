package com.myhand.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.centerm.smartpos.util.HexUtil;
import com.example.vincent.blereader.MainActivity;

import java.net.Socket;
import java.util.List;

/**
 * Created by vincent on 2017/10/7.
 */

public class BLEReader {
    private static final String tag=BLEReader.class.getSimpleName();
    public static final String BLENAME_ZHICHENG="HCTAXI_ICR";

    public static final String Generic_UIID="00001800-0000-1000-8000-00805f9b34fb";
    public static final String Service_UIID="6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public static final String Cmd_ReadReader="0302fe02cf";
    public static final String Cmd_ReaderCard="0302fe06cb";

    //蓝牙状态
    private int errorCode;
    private String errorMessage;
    private byte[] receibuffer;

    //蓝牙操作工具
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    //发现的Gatt服务
    private List<BluetoothGattService> gattServiceList;

    //发送消息
    private Handler handler;

    //计次
    private  int sendCnt;
    private int receiveCnt;

    public int getSendCnt() {
        return sendCnt;
    }

    public void setSendCnt(int sendCnt) {
        this.sendCnt = sendCnt;
    }

    public int getReceiveCnt() {
        return receiveCnt;
    }

    public void setReceiveCnt(int receiveCnt) {
        this.receiveCnt = receiveCnt;
    }

    public void resetCnt(){
        sendCnt=0;
        receiveCnt=0;
    }

    public BLEReader() {
        receibuffer=new byte[256];
        //启动数据接收线程
        //new ThreadReadNotify().start();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public byte[] getReceibuffer() {
        return receibuffer;
    }

    public void setReceibuffer(byte[] receibuffer) {
        this.receibuffer = receibuffer;
    }

    public List<BluetoothGattService> getGattServiceList() {
        return gattServiceList;
    }

    public void setGattServiceList(List<BluetoothGattService> gattServiceList) {
        this.gattServiceList = gattServiceList;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 判读设备是否已连接
     * @return
     */
    public boolean isConnected(){
        if(bluetoothGatt==null
                ||bluetoothDevice==null
                ||bluetoothGatt.getConnectionState(bluetoothDevice)!=BluetoothProfile.STATE_CONNECTED){
            return false;
        }

        return true;
    }

    /**
     * 蓝牙服务处理回调
     */
    private BluetoothGattCallback bluetoothGattCallback=new BluetoothGattCallback() {
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(tag,"Read availble");
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED: {
                    Log.d(tag, String.format("BLE %s Connected",bluetoothDevice.getAddress()));
                    //蓝牙设备成功连接后发现服务
                    bluetoothGatt.discoverServices();
                    break;
                }
                case BluetoothProfile.STATE_DISCONNECTED:                    {
                    Log.d(tag, String.format("BLE %s Connected",bluetoothDevice.getAddress()));
                    break;
                }
                default:{
                    Log.d(tag,String.format("BLE new state is %d"));
                    break;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(tag,"Gatt successfull");
                gattServiceList=bluetoothGatt.getServices();
                if(gattServiceList.size()<3){
                    Log.d(tag,"Not the enought service find");
                    return;
                }
                BluetoothGattService service=gattServiceList.get(2);
                List<BluetoothGattCharacteristic> list=service.getCharacteristics();
                if(list.size()!=2){
                    Log.d(tag,"Not the right service");
                    return;
                }

                characteristicWrite=list.get(1);
                characteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

                characteristicNotify=list.get(0);
                bluetoothGatt.setCharacteristicNotification(characteristicNotify,true);
                List<BluetoothGattDescriptor> descriptorList = characteristicNotify.getDescriptors();
                if(descriptorList==null){
                    Log.d(tag,"No descriptor");
                }
                if(descriptorList != null && descriptorList.size() > 0) {
                    for(BluetoothGattDescriptor descriptor : descriptorList) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        bluetoothGatt.writeDescriptor(descriptor);
                    }
                }

                //通知找到了服务
                if(handler!=null){
                    Message msg=handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            } else {
                Log.d(tag, "onServicesDiscovered received: " + status);
            }
            Log.d(tag,"discover service");
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            Log.d(tag,"onPhyUpdate");
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            Log.d(tag,"onPhyRead");

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            byte[] data=characteristic.getValue();
            Log.d(tag,String.format("onCharacteristicWrite:%s",HexUtil.bytesToHexString(data)));
            sendCnt++;
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] mByte = characteristic.getValue();
            Log.d(tag,String.format("onCharacteristicChanged:%s",HexUtil.bytesToHexString(mByte)));
            receiveCnt++;
            if(handler!=null){
                Message message=handler.obtainMessage();
                Bundle data=new Bundle();
                data.putByteArray("Response",mByte);
                message.setData(data);
                handler.sendMessage(message);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);

            Log.d(tag,"onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(tag,"onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d(tag,"Send OK");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d(tag,"onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.d(tag,"onMtuChanged");
        }


    };

    public BluetoothGattCallback getBluetoothGattCallback() {
        return bluetoothGattCallback;
    }

    public void setBluetoothGattCallback(BluetoothGattCallback bluetoothGattCallback) {
        this.bluetoothGattCallback = bluetoothGattCallback;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void connect(Context context){
        bluetoothGatt=bluetoothDevice.connectGatt(context,false,getBluetoothGattCallback());
        if(bluetoothGatt.connect()){
            Log.d(tag,"Connect OK");
        }else{
            Log.d(tag,"Connect failure.");
        }
    }

    //uuid: 6e400003-b5a3-f393-e0a9-e50e24dcca9e
    private BluetoothGattCharacteristic characteristicNotify;
    private BluetoothGattCharacteristic characteristicWrite;
    public void writeData(byte[] data){
        if(characteristicWrite==null){
            return;
        }
        //开始写数据
        characteristicWrite.setValue(data);
        if(bluetoothGatt.writeCharacteristic(characteristicWrite)){
            Log.d(tag,"Write OK");
        }else{
            Log.d(tag,"Write failure");
        }
    }

/*
    class ThreadReadNotify extends Thread{
        @Override
        public void run() {
            while(true){
                if(characteristicNotify==null) {
                    //Log.d(tag,"No notify service found");
                }else{
                    while(true) {
                        bluetoothGatt.readCharacteristic(characteristicNotify);
                        byte[] value = characteristicNotify.getValue();
                        if (value != null) {
                            Log.d(tag, String.format("Received data:%s", HexUtil.bytesToHexString(value)));
                            characteristicNotify.setValue((byte[]) null);
                        }
                    }
                }
            }
        }
    }
*/
}
