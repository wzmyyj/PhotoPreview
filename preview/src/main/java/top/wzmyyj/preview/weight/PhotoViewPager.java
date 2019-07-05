package top.wzmyyj.preview.weight;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created on 2019/06/27
 * <p>
 * 图片 ViewPager
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class PhotoViewPager extends ViewPager {
    public PhotoViewPager(Context context) {
        super(context);
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private boolean isLock;

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if(isLock){
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
