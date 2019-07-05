package top.wzmyyj.preview.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created on 2019/06/25.
 * <p>
 * ImageLoader.
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public interface PreviewImageLoader {

    /***
     * @param  imageView 容器
     * @param   path  图片路径
     * @param   simpleTarget   图片加载状态回调
     * ***/
    void displayImage(@NonNull ImageView imageView, @NonNull String path, @NonNull ImageLoadSimpleTarget<Bitmap> simpleTarget);

    /**
     * 停止
     *
     * @param imageView 容器
     **/
    void onStop(@NonNull ImageView imageView);

    /**
     * 清除
     *
     * @param context 容器
     **/
    void clearMemory(@NonNull Context context);
}
