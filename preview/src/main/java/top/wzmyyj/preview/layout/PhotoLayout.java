package top.wzmyyj.preview.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import top.wzmyyj.photoview.OnOutsidePhotoTapListener;
import top.wzmyyj.photoview.OnPhotoTapListener;
import top.wzmyyj.photoview.OnScaleChangedListener;
import top.wzmyyj.preview.R;
import top.wzmyyj.preview.base.OnExitListener;
import top.wzmyyj.preview.base.OnLockListener;
import top.wzmyyj.preview.loader.ImageLoadSimpleTarget;
import top.wzmyyj.preview.utils.PreviewManager;
import top.wzmyyj.preview.weight.SmoothPhotoView;

/**
 * Created on 2019/06/25.
 * <p>
 * 图片预览单个图片的view。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */

public class PhotoLayout extends FrameLayout {

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


    public PhotoLayout(Context context) {
        this(context, null);
    }

    public PhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_image_photo_layout, null, false);
        addView(view);
        initView(view);
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (!gainFocus) {
            PreviewManager.getInstance().getImageLoader().onStop(photoView);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        PreviewManager.getInstance().getImageLoader().clearMemory(photoView.getContext());
        release();
        super.onDetachedFromWindow();

    }

    /**
     * 清空数据。
     */
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
     * 初始化控件。
     *
     * @param view root view.
     */
    private void initView(@NonNull View view) {
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
     * @param url            image url.
     * @param rect           rect.
     * @param isCurrentIndex isCurrentIndex.
     * @param isSingleFling  isSingleFling.
     */
    public void setData(String url, Rect rect, boolean isCurrentIndex, boolean isSingleFling) {
        //地址
        imgUrl = url;
        //位置
        if (rect != null) {
            photoView.setThumbRect(rect);
        }
        photoView.setTag(imgUrl);
        //是否从上一个Activity的图片点击进入
        isTransPhoto = isCurrentIndex;
        photoView.setTransformImage(isTransPhoto);

        //加载原图
        PreviewManager.getInstance().getImageLoader().displayImage(photoView, imgUrl, mySimpleTarget);

        if (isSingleFling) {
            photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
                @Override
                public void onOutsidePhotoTap(ImageView imageView) {
                    if (photoView.checkMinOrMaxScale() && getContext() != null) {
                        transformOut();
                    }
                }
            });
        } else {
            photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    if (photoView.checkMinOrMaxScale() && getContext() != null) {
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
