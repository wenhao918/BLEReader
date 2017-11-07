package com.zhicheng.cmd;

/**
 * Created by vincent on 2017/11/6.
 */

public class Command {
    private int id;
    private String name;
    private String cmd;
    private int packCnt;
    private int order;

    public Command() {
    }

    public Command(String name, String cmd) {
        this.id=0;
        this.name = name;
        this.cmd = cmd;
        packCnt=1;
        order=0;
    }

    public Command(String name, String cmd, int packCnt, int order) {
        this.id=0;
        this.name = name;
        this.cmd = cmd;
        this.packCnt = packCnt;
        this.order = order;
    }

    public Command(int id, String name, String cmd, int packCnt, int order) {
        this.id = id;
        this.name = name;
        this.cmd = cmd;
        this.packCnt = packCnt;
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getPackCnt() {
        return packCnt;
    }

    public void setPackCnt(int packCnt) {
        this.packCnt = packCnt;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
