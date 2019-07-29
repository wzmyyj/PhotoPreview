package top.wzmyyj.preview.fragment;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import top.wzmyyj.preview.R;
import top.wzmyyj.preview.base.OnExitListener;
import top.wzmyyj.preview.base.OnLockListener;
import top.wzmyyj.preview.loader.ImageLoadSimpleTarget;
import top.wzmyyj.preview.utils.PreviewManager;
import top.wzmyyj.preview.weight.SmoothPhotoView;
import top.wzmyyj.preview.weight.photoview.*;

/**
 * Created on 2019/06/25.
 * <p>
 * 图片预览单个图片的fragment。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */

public class PhotoFragment extends Fragment {
    /**
     * 预览图片 类型
     */
    public static final String KEY_START_BOUND = "startRect";
    public static final String KEY_TRANS_PHOTO = "isTransPhoto";
    public static final String KEY_SING_FILING = "isSingleFling";
    public static final String KEY_PATH = "key_path";
    //图片地址
    private String imgUrl;
    // 是否是以动画进入的Fragment
    private boolean isTransPhoto = false;
    //图片
    private SmoothPhotoView photoView;
    //图片的外部控件
    private View rootView;
    //进度条
    private ProgressBar loading;
    private ImageLoadSimpleTarget<Bitmap> mySimpleTarget;


    /**
     * @param url            image url.
     * @param rect           rect.
     * @param isCurrentIndex isCurrentIndex.
     * @param isSingleFling  isSingleFling.
     * @return photoFragment.
     */
    public static PhotoFragment getInstance(String url, Rect rect, boolean isCurrentIndex, boolean isSingleFling) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PhotoFragment.KEY_PATH, url);
        bundle.putParcelable(PhotoFragment.KEY_START_BOUND, rect);
        bundle.putBoolean(PhotoFragment.KEY_TRANS_PHOTO, isCurrentIndex);
        bundle.putBoolean(PhotoFragment.KEY_SING_FILING, isSingleFling);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_photo_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initDate();
    }

    @Override
    public void onStop() {
        super.onStop();
        PreviewManager.getInstance().getImageLoader().onStop(this.photoView);
    }


    @Override
    public void onDestroyView() {
        PreviewManager.getInstance().getImageLoader().clearMemory(rootView.getContext());
        release();
        super.onDestroyView();
    }


    public void release() {
        mySimpleTarget = null;
        if (photoView != null) {
            photoView.setImageBitmap(null);
            photoView.setOnViewTapListener(null);
            photoView.setOnPhotoTapListener(null);
            photoView.setOnTransFormListener(null);
            photoView = null;
            rootView = null;
            isTransPhoto = false;
        }
    }

    /**
     * 初始化控件
     */
    private void initView(View view) {
        loading = view.findViewById(R.id.loading);
        photoView = view.findViewById(R.id.photoView);
        rootView = view.findViewById(R.id.rootView);
        loading.setVisibility(View.GONE);
        rootView.setDrawingCacheEnabled(false);
        photoView.setDrawingCacheEnabled(false);
        mySimpleTarget = new ImageLoadSimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap) {
                if (photoView.getTag().toString().equals(imgUrl)) {
                    photoView.setImageBitmap(bitmap);
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadFailed(Drawable errorDrawable) {
                loading.setVisibility(View.GONE);
                if (errorDrawable != null) {
                    photoView.setImageDrawable(errorDrawable);
                }
            }

            @Override
            public void onLoadStarted() {
                loading.setVisibility(View.GONE);
            }
        };
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        Bundle bundle = getArguments();
        boolean isSingleFling = true;
        if (bundle != null) {
            isSingleFling = bundle.getBoolean(KEY_SING_FILING);
            //地址
            imgUrl = bundle.getString(KEY_PATH);
            //位置
            Rect startBounds = bundle.getParcelable(KEY_START_BOUND);
            if (startBounds != null) {
                photoView.setThumbRect(startBounds);
            }
            photoView.setTag(imgUrl);
            //是否从上一个Activity的图片点击进入
            isTransPhoto = bundle.getBoolean(KEY_TRANS_PHOTO, false);
            photoView.setTransformImage(isTransPhoto);

            //加载原图
            PreviewManager.getInstance().getImageLoader().displayImage(this.photoView, imgUrl, mySimpleTarget);
        }

        if (isSingleFling) {
            photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
                @Override
                public void onOutsidePhotoTap(ImageView imageView) {
                    if (photoView.checkMinOrMaxScale() && getActivity() != null) {
                        transformOut();
                    }
                }
            });
        } else {
            photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    if (photoView.checkMinOrMaxScale() && getActivity() != null) {
                        transformOut();
                    }
                }
            });
        }

        photoView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                if (onLockListener != null) {
                    onLockListener.lock(true);
                }
            }

            @Override
            public boolean onScaleBegin() {
                return true;
            }

            @Override
            public void onScaleEnd() {
                if (onLockListener != null) {
                    onLockListener.lock(false);
                }
            }
        });


        photoView.setOnTransFormListener(new SmoothPhotoView.OnTransFromListener() {
            @Override
            public void onTransFormIn(float progress) {
                if (onLockListener != null) {
                    onLockListener.lock(progress < 0.99f);
                }
            }

            @Override
            public void onTransFormOut(float progress) {
                if (progress < 0.0001f && onExitListener != null) {
                    onExitListener.exit();
                }
                if (onLockListener != null) {
                    onLockListener.lock(progress < 0.99f);
                }
            }
        });
    }

    private OnExitListener onExitListener;

    public void setOnExitListener(OnExitListener onExitListener) {
        this.onExitListener = onExitListener;
    }

    private OnLockListener onLockListener;

    public void setOnLockListener(OnLockListener onLockListener) {
        this.onLockListener = onLockListener;
    }

    public void transformIn() {
        photoView.transformIn();
    }

    public void transformOut() {
        photoView.transformOut();
    }

}
