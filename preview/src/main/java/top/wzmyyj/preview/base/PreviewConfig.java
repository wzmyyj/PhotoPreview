package top.wzmyyj.preview.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import top.wzmyyj.preview.FPreviewActivity;
import top.wzmyyj.preview.enitity.ThumbImageInfo;

/**
 * Created on 2019/07/04
 * <p>
 * 添加配置。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public final class PreviewConfig {

    public enum IndicatorType {
        Dot, // 小圆点
        Number, // 数字
    }

    /**
     * private constructor.
     */
    private PreviewConfig() {

    }

    // 图片信息。
    private ArrayList<ThumbImageInfo> imgUrls;
    // 指示器类型。
    private IndicatorType indicatorType;
    // 当前位置。
    private int currentIndex;
    // 是否点击黑色退出。
    private boolean isSingleFling;

    //----------------set and get-------------//

    public ArrayList<ThumbImageInfo> getImgUrls() {
        return imgUrls;
    }

    public IndicatorType getIndicatorType() {
        return indicatorType;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isSingleFling() {
        return isSingleFling;
    }

    /**
     * Created on 2019/06/25
     * <p>
     * 配置。
     *
     * @author feling
     */
    public static final class Builder {
        private FragmentActivity activity;
        private PreviewConfig config;
        private Class className;
        private PreviewDialogFragment dialog;


        /**
         * private constructor.
         *
         * @param activity fragmentActivity.
         */
        private Builder(@NonNull FragmentActivity activity) {
            this.activity = activity;
            config = new PreviewConfig();
        }

        /**
         * 设置开始启动预览
         *
         * @param activity 启动.
         */
        public static Builder from(@NonNull FragmentActivity activity) {
            return new Builder(activity);
        }

        /**
         * 自定义预览activity 类名.
         *
         * @param className 继承PreviewActivity.
         */
        public Builder to(@NonNull Class className) {
            this.className = className;
            return this;
        }

        /**
         * 自定义预览activity 类名.
         *
         * @param dialog 继承PreviewDialog.
         **/
        public Builder toDialog(@NonNull PreviewDialogFragment dialog) {
            this.dialog = dialog;
            return this;
        }

        /**
         * 设置数据源.
         *
         * @param imgUrls 数据.
         * @return Builder.
         */
        public Builder setData(@NonNull ArrayList<ThumbImageInfo> imgUrls) {
            this.config.imgUrls = imgUrls;
            return this;
        }

        /**
         * 设置默认索引
         *
         * @param currentIndex 数据
         * @return Builder
         */
        public Builder setCurrentIndex(int currentIndex) {
            this.config.currentIndex = currentIndex;
            return this;
        }

        /**
         * 设置指示器类型.
         *
         * @param indicatorType 枚举
         * @return Builder
         **/
        public Builder setType(@NonNull IndicatorType indicatorType) {
            this.config.indicatorType = indicatorType;
            return this;
        }

        /**
         * 设置超出内容点击退出（黑色区域）.
         *
         * @param isSingleFling true 可以 false.
         * @return Builder
         */
        public Builder setSingleFling(boolean isSingleFling) {
            this.config.isSingleFling = isSingleFling;
            return this;
        }

        /**
         * 启动.
         */
        public void start() {
            // dialog 模式。
            if (dialog != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("imagePaths", config.imgUrls);
                bundle.putInt("currentIndex", config.currentIndex);
                bundle.putSerializable("indicatorType", config.indicatorType);
                bundle.putBoolean("isSingleFling", config.isSingleFling);
                dialog.setArguments(bundle);

                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                dialog.show(ft, "dialog");

            } else {// 启动新的activity。
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("imagePaths", config.imgUrls);
                intent.putExtra("currentIndex", config.currentIndex);
                intent.putExtra("indicatorType", config.indicatorType);
                intent.putExtra("isSingleFling", config.isSingleFling);
                if (className == null) {
                    intent.setClass(activity, FPreviewActivity.class);
                } else {
                    intent.setClass(activity, className);
                }
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
            activity = null;
            dialog = null;
            className = null;
        }

        /**
         * 上个页面传递过来的数据组装成PreviewConfig.
         *
         * @param activity preview activity.
         * @return config.
         */
        public static PreviewConfig get(@NonNull Activity activity) {
            PreviewConfig config = new PreviewConfig();

            Intent intent = activity.getIntent();
            config.imgUrls = intent.getParcelableArrayListExtra("imagePaths");
            config.currentIndex = intent.getIntExtra("currentIndex", 0);
            config.indicatorType = (PreviewConfig.IndicatorType) intent.getSerializableExtra("indicatorType");
            config.isSingleFling = intent.getBooleanExtra("isSingleFling", true);
            return config;
        }

        /**
         * getArguments的数据组装成PreviewConfig
         *
         * @param fragment preview dialog fragment.
         * @return config.
         */
        public static PreviewConfig get(@NonNull Fragment fragment) {
            PreviewConfig config = new PreviewConfig();

            Bundle bundle = fragment.getArguments();
            if (bundle == null) {
                return config;
            }
            config.imgUrls = bundle.getParcelableArrayList("imagePaths");
            config.currentIndex = bundle.getInt("currentIndex", 0);
            config.indicatorType = (PreviewConfig.IndicatorType) bundle.getSerializable("indicatorType");
            config.isSingleFling = bundle.getBoolean("isSingleFling", true);
            return config;
        }
    }


}
