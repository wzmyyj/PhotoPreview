package top.wzmyyj.preview.base;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import top.wzmyyj.preview.utils.StatusBarUtil;


/**
 * Created on 2019/06/25.
 * <p>
 * preview photo abstract Activity.
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public abstract class PreviewActivity extends AppCompatActivity
        implements OnExitListener, PreviewHelper.Factory {

    private PreviewHelper previewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.initStatusBar(this, true, false, true);
        previewHelper = createHelper();
        // 设置布局。
        if (setContentLayout() == 0) {
            setContentView(previewHelper.getContentLayout());
        } else {
            setContentView(setContentLayout());
        }
        previewHelper.setOnExitListener(this);
        previewHelper.init(this);
        previewHelper.onCreate(getWindow().getDecorView());
    }


    @Override
    protected void onDestroy() {
        previewHelper.onDestroy();
        previewHelper = null;
        super.onDestroy();
    }


    /**
     * 按返回键时，执行退出动画。
     */
    @Override
    public void onBackPressed() {
        previewHelper.transformOut();
    }

    /**
     * 关闭页面
     */
    @Override
    public void exit() {
        finish();
        overridePendingTransition(0, 0);
    }

    /***
     * 自定义布局内容
     ***/
    public int setContentLayout() {
        return 0;
    }
}
