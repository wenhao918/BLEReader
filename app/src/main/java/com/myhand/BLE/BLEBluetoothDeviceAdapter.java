package com.myhand.BLE;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.LayoutInflaterFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vincent.blereader.R;

import java.util.List;

/**
 * Created by vincent on 2017/10/11.
 */

public class BLEBluetoothDeviceAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater inflater;
    private List<BluetoothDevice> bluetoothDeviceList;

    public BLEBluetoothDeviceAdapter(Context context,List<BluetoothDevice> bluetoothDeviceList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.bluetoothDeviceList = bluetoothDeviceList;
    }

    public void addDevice(BluetoothDevice device){
        for(BluetoothDevice bleDevice:bluetoothDeviceList) {
            if(bleDevice.getAddress().compareTo(device.getAddress())==0){
                return;
            }
        }

        bluetoothDeviceList.add(device);
    }

    private static class ViewHolder{
        public TextView textViewName;
        public TextView textViewAddress;
    }

    @Override
    public int getCount() {
        if(bluetoothDeviceList==null){
            return 0;
        }
        return bluetoothDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.layout_item_bluetoothdevice,null);

            holder=new ViewHolder();
            holder.textViewAddress=convertView.findViewById(R.id.textViewAddress);
            holder.textViewName=convertView.findViewById(R.id.textViewDeviceName);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }

        BluetoothDevice bluetoothDevice=(BluetoothDevice) getItem(position);
        holder.textViewAddress.setText(bluetoothDevice.getAddress());
        holder.textViewName.setText(bluetoothDevice.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEApplication.instance.bleReader.setBluetoothDevice((BluetoothDevice) getItem(position));
                BLEApplication.instance.bleReader.connect(context);
                Intent intent=new Intent();
                intent.setClass(context,BLEDeviceActivity.class);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
