package com.example.prana.numberplaterecognition;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")

public class NotificationData{
    @SerializedName("detail")
    private String mDetail;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("upi")
    private String mupi;

    public String getMupi() {
        return mupi;
    }

    public void setMupi(String mupi) {
        this.mupi = mupi;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDetail(String detail){
        mDetail=detail;
    }

    public String getDetail() {
        return mDetail;
    }

    public String getTitle(){
        return mTitle;
    }


}
