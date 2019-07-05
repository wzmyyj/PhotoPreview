package top.wzmyyj.preview.loader;


import android.graphics.drawable.Drawable;

/**
 * Created on 2019/06/25.
 * <p>
 * 图片加载回调状态。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public interface ImageLoadSimpleTarget<T> {
    /**
     * Callback when an image has been successfully loaded.
     *
     * @param t image type. such as bitmap.
     */
    void onResourceReady(T t);

    /**
     * Callback indicating the image could not be successfully loaded.
     *
     * @param errorRes errorRes
     */
    void onLoadFailed(Drawable errorRes);

    /**
     * Callback invoked right before your request is submitted.
     */
    void onLoadStarted();

}
