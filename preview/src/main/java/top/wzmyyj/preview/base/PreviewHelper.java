package top.wzmyyj.preview.base;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created on 2019/07/03
 * <p>
 * PreviewHelper 抽象接口。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public interface PreviewHelper {

    /**
     * @param owner fragment or fragmentActivity.
     */
    void init(@NonNull LifecycleOwner owner);

    /**
     * @param onExitListener .
     */
    void setOnExitListener(@NonNull OnExitListener onExitListener);

    /**
     * @return layout id.
     */
    int getContentLayout();

    /**
     * when create view.
     *
     * @param v root view.
     */
    void onCreate(@NonNull View v);

    /**
     * when destroy.
     */
    void onDestroy();

    /**
     * transform out.
     */
    void transformOut();

    /**
     * 工厂接口。
     */
    interface Factory {

        /**
         * @return new PreviewHelper.
         */
        PreviewHelper createHelper();
    }
}
