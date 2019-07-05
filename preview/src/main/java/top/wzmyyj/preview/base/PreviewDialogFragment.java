package top.wzmyyj.preview.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import top.wzmyyj.preview.R;

/**
 * Created on 2019/07/02
 * <p>
 * preview photo abstract DialogFragment.
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public abstract class PreviewDialogFragment extends DialogFragment
        implements OnExitListener, PreviewHelper.Factory {

    private PreviewHelper previewHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        previewHelper = createHelper();
        previewHelper.setOnExitListener(this);
        previewHelper.init(this);
        // 设置布局。
        int layoutId;
        if (setContentLayout() == 0) {
            layoutId = previewHelper.getContentLayout();
        } else {
            layoutId = setContentLayout();
        }
        final View root = inflater.inflate(layoutId, null, false);

        // 设置dialog样式。
        Dialog dialog = getDialog();
        if (dialog != null) {
            // 设置全屏。
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(R.color.transparent);
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                wlp.dimAmount = 0f;// 取消半透明背景。
                wlp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(wlp);
            }
            // 监听back键。
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        previewHelper.transformOut();
                        return true;
                    }
                    return false;
                }
            });

        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        previewHelper.onCreate(view);
    }


    @Override
    public void onDestroyView() {
        previewHelper.onDestroy();
        previewHelper = null;
        super.onDestroyView();
    }


    /**
     * 关闭Dialog。
     */
    @Override
    public void exit() {
        dismiss();
    }

    /***
     * 自定义布局内容。
     ***/
    public int setContentLayout() {
        return 0;
    }
}
