package top.wzmyyj.preview;


import android.support.annotation.NonNull;

import top.wzmyyj.preview.base.PreviewActivity;
import top.wzmyyj.preview.base.PreviewHelper;
import top.wzmyyj.preview.fragment.FPreviewHelperImpl;

/**
 * Created on 2019/06/25.
 * <p>
 * preview photo Activity. with fragment.
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class FPreviewActivity extends PreviewActivity {

    @NonNull
    @Override
    public PreviewHelper createHelper() {
        return new FPreviewHelperImpl();
    }
}
