package top.wzmyyj.preview.enitity;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created on 2019/06/25.
 * <p>
 * 图片预览实体类。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class ThumbImageInfo implements Parcelable {

    private String url;  //图片地址
    private Rect rect; // 记录坐标

    /**
     * @param url image url.
     */
    public ThumbImageInfo(String url) {
        this.url = url;
    }

    /**
     * @param url  image url.
     * @param rect view rect.
     */
    public ThumbImageInfo(String url, Rect rect) {
        this.url = url;
        this.rect = rect;
    }

    /**
     * @param url  image url.
     * @param view view.
     */
    public ThumbImageInfo(String url, View view) {
        this.url = url;
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        this.rect = rect;
    }


    //----------------set and get-------------------------//
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }


    //----------------for Parcelable----------------------//
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.rect, 0);
    }

    protected ThumbImageInfo(Parcel in) {
        this.url = in.readString();
        this.rect = in.readParcelable(Rect.class.getClassLoader());
    }

    public static final Creator<ThumbImageInfo> CREATOR = new Creator<ThumbImageInfo>() {
        public ThumbImageInfo createFromParcel(Parcel source) {
            return new ThumbImageInfo(source);
        }

        public ThumbImageInfo[] newArray(int size) {
            return new ThumbImageInfo[size];
        }
    };
}
