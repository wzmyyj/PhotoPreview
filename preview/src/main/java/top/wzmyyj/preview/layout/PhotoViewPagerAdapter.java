package top.wzmyyj.preview.layout;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import top.wzmyyj.preview.base.OnExitListener;
import top.wzmyyj.preview.base.OnLockListener;
import top.wzmyyj.preview.enitity.ThumbImageInfo;

/**
 * Created on 2019/07/01.
 * <p>
 * view pager adapter.
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class PhotoViewPagerAdapter extends PagerAdapter {

    private List<ThumbImageInfo> thumbImageInfoList;
    private int intoIndex;
    private boolean isSingleFling;
    private OnExitListener onExitListener;
    private OnLockListener onLockListener;

    private Map<View, Integer> viewIntegerMap = new WeakHashMap<>();

    public PhotoViewPagerAdapter(@NonNull List<ThumbImageInfo> thumbImageInfoList,
                                 int intoIndex, boolean isSingleFling,
                                 OnExitListener onExitListener,
                                 OnLockListener onLockListener) {
        this.thumbImageInfoList = thumbImageInfoList;
        this.intoIndex = intoIndex;
        this.isSingleFling = isSingleFling;
        this.onExitListener = onExitListener;
        this.onLockListener = onLockListener;
    }

    /**
     * @param position position
     * @return photoLayout
     */
    @Nullable
    public PhotoLayout getView(int position) {
        for (Map.Entry<View, Integer> entry : viewIntegerMap.entrySet()) {
            if (entry.getValue() == position) {
                return (PhotoLayout) entry.getKey();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return thumbImageInfoList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        if (position == intoIndex) {
            intoIndex = -1;
        }
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoLayout v = new PhotoLayout(container.getContext());
        v.setData(thumbImageInfoList.get(position).getUrl(),
                thumbImageInfoList.get(position).getRect(),
                position == intoIndex, isSingleFling);
        v.setOnExitListener(onExitListener);
        v.setOnLockListener(onLockListener);
        container.addView(v);
        viewIntegerMap.put(v, position);
        return v;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
