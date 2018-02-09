package com.example.vtec.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by VTEC on 3/14/2016.
 */
public class DetailInfo implements Parcelable{
    private String Info = "";
    private String name = "";
    int img;

    public String getInfo() {
        return Info;
    }
    public void setInfo(String Info) {
        this.Info = Info;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getImg() {
        return img;
    }
    public void setImg(int img) {
        this.img = img;
    }

    public static final Parcelable.Creator<DetailInfo> CREATOR = new Creator<DetailInfo>() {
        @Override
        public DetailInfo[] newArray(int size) {
            return null;
        }

        @Override
        public DetailInfo createFromParcel(Parcel source) {
            DetailInfo result = new DetailInfo();
            result.name = source.readString();
            result.Info = source.readString();
            result.img = source.readInt();
            return result;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(Info);
        dest.writeInt(img);
    }

    public boolean equals(Object obj) {
        if (obj instanceof DetailInfo) {
            DetailInfo detailInfo = (DetailInfo) obj;
            return this.name.equals(detailInfo.name)
                    && this.Info.equals(detailInfo.Info);
        }
        return super.equals(obj);
    }
}
