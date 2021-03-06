package com.myhand.BLE;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.database.DataSetObserver;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.example.vincent.blereader.R;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Inflater;

/**
 * Created by vincent on 2017/10/10.
 */

public class BLEAdapter extends BaseExpandableListAdapter{
    private static final String tag=BLEAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater  inflater;
    private BLEReader bleReader;
    private List<BluetoothGattService> listService;

    private static class GroupViewHolder{
        public TextView textViewUUID;
    };
    private static class ChildViewHolder{
        public TextView textViewUUID;
    }

    public BLEAdapter(Context context, BLEReader bleReader) {
        this.context = context;
        inflater= LayoutInflater.from(context);

        this.bleReader = bleReader;
        listService=bleReader.getGattServiceList();
    }

    @Override
    public int getGroupCount() {
        if(bleReader==null) {
            return 0;
        }

        if(listService==null){
            return 0;
        }

        if(listService.size()==0) {
            return 1;
        }

        return listService.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        BluetoothGattService service=listService.get(groupPosition);
        List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList=service.getCharacteristics();
        if(bluetoothGattCharacteristicList==null||bluetoothGattCharacteristicList.size()==0){
            return 0;
        }
        return bluetoothGattCharacteristicList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if(listService.size()==0) {
            return null;
        }

        return listService.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        BluetoothGattService service=listService.get(groupPosition);
        List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList=service.getCharacteristics();
        if(bluetoothGattCharacteristicList==null||bluetoothGattCharacteristicList.size()==0){
            return null;
        }
        return bluetoothGattCharacteristicList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition+groupPosition*1000;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        convertView=inflater.inflate(R.layout.layout_item_gattservice,null);
        TextView textViewUUID=convertView.findViewById(R.id.textViewUUID);
        if(listService.size()==0){
            textViewUUID.setText("No Service found");
        }else {
            BluetoothGattService service = (BluetoothGattService) getGroup(groupPosition);
            textViewUUID.setText(String.format("TYPE:%d UUID:%s",service.getType(),service.getUuid().toString()));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        BluetoothGattCharacteristic characteristic=(BluetoothGattCharacteristic) getChild(groupPosition,childPosition);

        ChildViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.layout_item_characteristic,null);
            viewHolder=new ChildViewHolder();
            viewHolder.textViewUUID=convertView.findViewById(R.id.textViewCharactericUUID);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ChildViewHolder) convertView.getTag();
        }
        if(characteristic==null) {
            viewHolder.textViewUUID.setText("无");
        }else{
            List<BluetoothGattDescriptor> descriptorList=characteristic.getDescriptors();
            String text="";
/*
            for(BluetoothGattDescriptor descriptor:descriptorList){
                text=new String(descriptor.getValue());
            }
*/
            viewHolder.textViewUUID.setText(text+characteristic.getUuid().toString());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    //定义一个TextView
    private TextView getTextView(){
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,40);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setPadding(36, 0, 0, 0);
        textView.setTextSize(20);
        return textView;
    }

}
