package com.example.vtec.myapplication;

import java.util.ArrayList;

/**
 * Created by VTEC on 3/14/2016.
 */
public class HeaderInfo {
    private String daytime;
    private ArrayList<DetailInfo> actionList = new ArrayList<DetailInfo>();
    private String detailTime;

    public String getdaytime() {
        return daytime;
    }
    public void setdaytime(String daytime) {
        this.daytime = daytime;
    }
    public ArrayList<DetailInfo> getActionList() {
        return actionList;
    }
    public void setActionList(ArrayList<DetailInfo> actionList) {
        this.actionList = actionList;
    }
    public String getDetailTime() {
        return detailTime;
    }
    public void setDetailTime(String detailTime) {
        this.detailTime = detailTime;
    }
}
