package top.wzmyyj.preview.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created on 2019/07/04
 * <p>
 * StatusBar 相关工具类。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class StatusBarUtil {

    /**
     * @param context       上下文.
     * @param isTint        沉浸式.
     * @param isDark        文字是否黑.
     * @param isTransparent 是否透明.
     */
    @SuppressLint("InlinedApi")
    public static void initStatusBar(Activity context, boolean isTint, boolean isDark, boolean isTransparent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Window window = context.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int flag = window.getDecorView().getWindowSystemUiVisibility();
        // 是否沉浸式。
        if (isTint) {
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        } else {
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        }

        // 文字是否黑色。
        if (isDark) {
            flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            flag |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flag);
        // 背景颜色是否透明。
        if (isTransparent) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }

    /**
     * 获取状态栏高度。
     *
     * @param context 上下文。
     * @return 高度（px）
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resultId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resultId > 0) {
            result = context.getResources().getDimensionPixelSize(resultId);
        }
        return result;
    }
}
