package com.zh.shen.bean;

/**
 * Created by Administrator on 2017/4/30.
 */
public class BarcodeBean {

    private String id;
    private String tableNumber;         // 表号
    private String oldTitleNumber;      // 原铅封号
    private String newTitleNumber;      // 新铅封号
    private String time;                // 时间


    /** id */
    public String getId() {
        return id;
    }
    /** id */
    public void setId(String id) {
        this.id = id;
    }

    /** 表号 */
    public String getTableNumber() {
        return tableNumber;
    }
    /** 表号 */
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    /** 新铅封号 */
    public String getNewTitleNumber() {
        return newTitleNumber;
    }
    /** 新铅封号 */
    public void setNewTitleNumber(String newTitleNumber) {
        this.newTitleNumber = newTitleNumber;
    }

    /** 原铅封号 */
    public String getOldTitleNumber() {
        return oldTitleNumber;
    }
    /** 原铅封号 */
    public void setOldTitleNumber(String oldTitleNumber) {
        this.oldTitleNumber = oldTitleNumber;
    }

    /** 时间 */
    public String getTime() {
        return time;
    }
    /** 时间 */
    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "BarcodeBean{" +
                "id='" + id + '\'' +
                ", tableNumber='" + tableNumber + '\'' +
                ", oldTitleNumber='" + oldTitleNumber + '\'' +
                ", newTitleNumber='" + newTitleNumber + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
