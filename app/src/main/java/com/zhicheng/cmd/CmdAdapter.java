package com.zhicheng.cmd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vincent.blereader.R;

import java.util.List;

/**
 * Created by vincent on 2017/11/6.
 */

public class CmdAdapter extends BaseAdapter{
    private List<Command> listCommand;
    private Context context;
    private LayoutInflater inflater;

    private static class ViewHolder{
        public TextView textViewName;
        public TextView textViewCmd;
    }

    public CmdAdapter(List<Command> listCommand, Context context) {
        this.listCommand = listCommand;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(listCommand==null) {
            return 0;
        }

        return listCommand.size();
    }

    @Override
    public Object getItem(int position) {
        if(listCommand==null) {
            return null;
        }

        return listCommand.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.layout_item_command,null);
            holder.textViewName=(TextView)convertView.findViewById(R.id.textViewCmdName);
            holder.textViewCmd=(TextView)convertView.findViewById(R.id.textViewCmdCmd);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        Command cmd= (Command) getItem(position);
        holder.textViewName.setText(cmd.getName());
        holder.textViewCmd.setText(cmd.getCmd());

        return convertView;
    }
}
