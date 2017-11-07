package com.zhicheng.cmd;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 2017/11/7.
 */

public class BLEReaderDatabase extends AppDatabase{
    private static String tag=BLEReaderDatabase.class.getSimpleName();

    public static String TBCommand="TBCommand";
    public String SQLCreateTBCommand=String.format("Create Table %s(id integer primary key autoincrement,"
            +"name varchar2(30),"
            +"cmd varchar2(60) not null,"
            +"packCnt decimal(2) not null default 1,"
            +"order decimal(2) not null default 0)",TBCommand);
    public BLEReaderDatabase(Context context, String databaseName, int version) {
        super(context, databaseName, version);
        // TODO Auto-generated constructor stub
    }

    public boolean insertCommand(Command cmd){
        String sqlInsert=String.format("Insert into %s values(null,'%s','%s',%d,%d)",
                TBCommand,cmd.getName(),cmd.getCmd(),cmd.getPackCnt(),cmd.getOrder());

        return ExecCommand(sqlInsert);
    }

    public boolean updateCommand(Command cmd){
        String sqlUpdate=String.format("update %s set name='%s',cmd='%s',packCnt=%d,order=%d\n"
                +"where id=%d",
                TBCommand,cmd.getName(),cmd.getCmd(),cmd.getPackCnt(),cmd.getOrder(),cmd.getId());

        return ExecCommand(sqlUpdate);
    }

    public List<Command> getCmdList(){
        List<Command> result=new ArrayList<Command>();
        String sqlSelect=String.format("Select * from %s",TBCommand);
        Cursor cursor=ExecQuery(sqlSelect);
        while(cursor!=null&&cursor.moveToNext()){
            Command cmd=new Command(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getInt(4));
            result.add(cmd);
        }

        return result;
    }
}
