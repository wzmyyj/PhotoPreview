package top.wzmyyj.preview;


import android.support.annotation.NonNull;

import top.wzmyyj.preview.base.PreviewActivity;
import top.wzmyyj.preview.base.PreviewHelper;
import top.wzmyyj.preview.layout.LPreviewHelperImpl;

/**
 * Created on 2019/06/25.
 * <p>
 * preview photo Activity. with layout.
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class LPreviewActivity extends PreviewActivity {

    @NonNull
    @Override
    public PreviewHelper createHelper() {
        return new LPreviewHelperImpl();
    }
}
