package top.wzmyyj.preview.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import top.wzmyyj.preview.weight.photoview.PhotoView;


/**
 * Created on 2019/06/27
 * <p>
 * 可缩放图片的ImageView。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class SmoothPhotoView extends PhotoView {

    public enum Status {
        STATE_NORMAL,
        STATE_IN,
        STATE_OUT,
    }

    private Status mStatus = Status.STATE_NORMAL;
    private static final int TRANSFORM_DURATION = 300;

    private static final float MAX_SCALE = 3f;
    private static final float MIN_SCALE = 1f;

    private Matrix matrix;
    private Transform startTransform;
    private Transform endTransform;
    private Transform animTransform;
    private Rect thumbRect;
    private boolean transformStart;
    private int bitmapWidth;
    private int bitmapHeight;
    private ValueAnimator animator;

    private boolean isTransformImage = true;

    public boolean isTransformImage() {
        return isTransformImage;
    }

    public void setTransformImage(boolean transformImage) {
        isTransformImage = transformImage;
        if (transformImage) {
            getBackground().setAlpha(0);
        } else {
            getBackground().setAlpha(255);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bitmapWidth = 0;
        bitmapHeight = 0;
        thumbRect = null;
        matrix = null;
        startTransform = null;
        endTransform = null;
        animTransform = null;
        if (animator != null) {
            animator.cancel();
            animator.clone();
            animator = null;
        }
    }

    private class Transform implements Cloneable {
        float left, top, width, height;
        int alpha;
        float scale;

        public Transform clone() {
            Transform obj = null;
            try {
                obj = (Transform) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return obj;
        }
    }

    public SmoothPhotoView(Context context) {
        this(context, null);
    }

    public SmoothPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SmoothPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        initSmoothImageView();
    }

    private void initSmoothImageView() {
        setMinimumScale(MIN_SCALE);
        setMaximumScale(MAX_SCALE);
        matrix = new Matrix();
        setBackgroundColor(Color.BLACK);
        getBackground().setAlpha(0);
        setScaleType(ImageView.ScaleType.FIT_CENTER);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mStatus == Status.STATE_OUT || mStatus == Status.STATE_IN) {
            if (getDrawable() == null) {
                // 没有图片载出来的退出情况。
                if (transformStart) {
                    startNoDrawableOut();
                }
                super.onDraw(canvas);
                return;
            }
            if (startTransform == null || endTransform == null || animTransform == null) {
                initTransform();
            }

            if (transformStart) {
                changeEndTransform();
            }

            if (animTransform == null) {
                super.onDraw(canvas);
                return;
            }
            int saveCount = canvas.getSaveCount();
            matrix.setScale(animTransform.scale, animTransform.scale);

            float translateX = -(bitmapWidth * animTransform.scale - animTransform.width) / 2;
            float translateY = -(bitmapHeight * animTransform.scale - animTransform.height) / 2;
            matrix.postTranslate(translateX, translateY);

            canvas.translate(animTransform.left, animTransform.top);
            canvas.clipRect(0, 0, animTransform.width, animTransform.height);
            canvas.concat(matrix);
            getDrawable().draw(canvas);
            canvas.restoreToCount(saveCount);

            if (transformStart) {
                startTransform();
            }
        } else {
            getBackground().setAlpha(255);
            super.onDraw(canvas);
        }
    }

    /**
     * 没有图片载出来的退出情况。
     */
    private void startNoDrawableOut() {
        if (mStatus != Status.STATE_OUT) {
            return;
        }
        transformStart = false;

        animator = new ValueAnimator();
        animator.setDuration(TRANSFORM_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("animAlpha", 255, 0);
        animator.setValues(alphaHolder);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int alpha = (Integer) animation.getAnimatedValue("animAlpha");
                getBackground().setAlpha(alpha);
                if (onTransFromListener != null) {
                    onTransFromListener.onTransFormOut(1.0f * alpha / 255);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onTransFromListener != null) {
                    onTransFromListener.onTransFormOut(0.0f);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (onTransFromListener != null) {
                    onTransFromListener.onTransFormOut(1.0f);
                }
            }
        });
        animator.start();

    }

    /**
     * 有图片时的进出动画
     */
    private void startTransform() {
        transformStart = false;
        if (animTransform == null || getDrawable() == null) {
            return;
        }
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        animator = new ValueAnimator();
        animator.setDuration(TRANSFORM_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (mStatus == Status.STATE_IN) {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("animScale", startTransform.scale, endTransform.scale);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("animAlpha", startTransform.alpha, endTransform.alpha);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("animLeft", startTransform.left, endTransform.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("animTop", startTransform.top, endTransform.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("animWidth", startTransform.width, endTransform.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("animHeight", startTransform.height, endTransform.height);
            animator.setValues(scaleHolder, alphaHolder, leftHolder, topHolder, widthHolder, heightHolder);
        } else if (mStatus == Status.STATE_OUT) {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("animScale", endTransform.scale, startTransform.scale);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("animAlpha", endTransform.alpha, startTransform.alpha);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("animLeft", endTransform.left, startTransform.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("animTop", endTransform.top, startTransform.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("animWidth", endTransform.width, startTransform.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("animHeight", endTransform.height, startTransform.height);
            animator.setValues(scaleHolder, alphaHolder, leftHolder, topHolder, widthHolder, heightHolder);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animTransform.alpha = (Integer) animation.getAnimatedValue("animAlpha");
                animTransform.scale = (float) animation.getAnimatedValue("animScale");
                animTransform.left = (float) animation.getAnimatedValue("animLeft");
                animTransform.top = (float) animation.getAnimatedValue("animTop");
                animTransform.width = (float) animation.getAnimatedValue("animWidth");
                animTransform.height = (float) animation.getAnimatedValue("animHeight");

                getBackground().setAlpha(animTransform.alpha);

                if (!isTransformImage && getDrawable() != null) {
                    getDrawable().setAlpha(animTransform.alpha);
                }

                invalidate();

                if (onTransFromListener != null) {
                    if (mStatus == Status.STATE_IN) {
                        onTransFromListener.onTransFormIn(1.0f * animTransform.alpha / 255);
                    } else if (mStatus == Status.STATE_OUT) {
                        onTransFromListener.onTransFormOut(1.0f * animTransform.alpha / 255);
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if (onTransFromListener != null) {
                    if (mStatus == Status.STATE_IN) {
                        onTransFromListener.onTransFormIn(1.0f);
                    } else if (mStatus == Status.STATE_OUT) {
                        onTransFromListener.onTransFormOut(0.0f);
                    }
                }

                if (mStatus == Status.STATE_IN) {
                    mStatus = Status.STATE_NORMAL;
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (onTransFromListener != null) {
                    if (mStatus == Status.STATE_IN) {
                        onTransFromListener.onTransFormIn(0.0f);
                    } else if (mStatus == Status.STATE_OUT) {
                        onTransFromListener.onTransFormOut(1.0f);
                    }
                }
            }
        });
        animator.start();

    }


    public boolean isAnimatorRunning(){
        return animator != null && animator.isRunning();
    }

    public void transformIn() {
        if (isAnimatorRunning()) {
            return;
        }
        transformStart = true;
        if (onTransFromListener != null) {
            onTransFromListener.onTransFormIn(0f);
        }
        mStatus = Status.STATE_IN;
        invalidate();
    }

    public void transformOut() {
        if (isAnimatorRunning()) {
            return;
        }
        transformStart = true;
        if (onTransFromListener != null) {
            onTransFromListener.onTransFormOut(1f);
        }
        mStatus = Status.STATE_OUT;
        invalidate();
    }

    /**
     * 设置起始位置图片的Rect
     *
     * @param thumbRect 参数
     */
    public void setThumbRect(Rect thumbRect) {
        this.thumbRect = thumbRect;
    }

    private void initTransform() {
        if (getDrawable() == null) {
            return;
        }
        if (startTransform != null && endTransform != null && animTransform != null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        if (getDrawable() instanceof BitmapDrawable) {
            Bitmap mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            bitmapWidth = mBitmap.getWidth();
            bitmapHeight = mBitmap.getHeight();
        } else {
            Bitmap mBitmap = Bitmap.createBitmap(getDrawable().getIntrinsicWidth(),
                    getDrawable().getIntrinsicHeight(), Bitmap.Config.RGB_565);
            bitmapWidth = mBitmap.getWidth();
            bitmapHeight = mBitmap.getHeight();
        }
        startTransform = new Transform();
        startTransform.alpha = 0;
        if (thumbRect == null) {// 默认原图位置在中间。大小按比例宽或高最多屏幕宽度的1/4。
            float proportion = 1.0f * bitmapWidth / bitmapHeight;
            int maxWidthOrHeight = getWidth() / 4;
            int thumbDefaultWidth = proportion >= 1 ? maxWidthOrHeight : (int) (maxWidthOrHeight * proportion);
            int thumbDefaultHeight = proportion < 1 ? maxWidthOrHeight : (int) (maxWidthOrHeight / proportion);
            thumbRect = new Rect(getWidth() / 2 - thumbDefaultWidth / 2,
                    getHeight() / 2 - thumbDefaultHeight / 2,
                    getWidth() / 2 + thumbDefaultWidth / 2,
                    getHeight() / 2 + thumbDefaultHeight / 2);
            setTransformImage(false);
        }

        int[] location = new int[2];
        this.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        this.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标

        startTransform.left = thumbRect.left - location[0];
        startTransform.top = thumbRect.top - location[1];
        startTransform.width = thumbRect.width();
        startTransform.height = thumbRect.height();


        //开始时以CenterCrop方式显示，缩放图片使图片的一边等于起始区域的一边，另一边大于起始区域
        float startScaleX = (float) thumbRect.width() / bitmapWidth;
        float startScaleY = (float) thumbRect.height() / bitmapHeight;
        startTransform.scale = startScaleX > startScaleY ? startScaleX : startScaleY;
        //结束时以fitCenter方式显示，缩放图片使图片的一边等于View的一边，另一边大于View
        float endScaleX = (float) getWidth() / bitmapWidth;
        float endScaleY = (float) getHeight() / bitmapHeight;

        endTransform = new Transform();

        endTransform.scale = endScaleX < endScaleY ? endScaleX : endScaleY;
        endTransform.alpha = 255;
        int endBitmapWidth = (int) (endTransform.scale * bitmapWidth);
        int endBitmapHeight = (int) (endTransform.scale * bitmapHeight);
        endTransform.left = (getWidth() - endBitmapWidth) / 2f;
        endTransform.top = (getHeight() - endBitmapHeight) / 2f;
        endTransform.width = endBitmapWidth;
        endTransform.height = endBitmapHeight;


        if (mStatus == Status.STATE_IN) {
            animTransform = startTransform.clone();
        } else if (mStatus == Status.STATE_OUT) {
            animTransform = endTransform.clone();
        }

    }

    private void changeEndTransform() {
        if (mStatus != Status.STATE_OUT || endTransform == null || getDrawable() == null) {
            return;
        }
        Matrix matrix = getImageMatrix();
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        rectF.set(drawable.getBounds());
        matrix.mapRect(rectF);
        endTransform.scale *= getScale();
        endTransform.left = rectF.left;
        endTransform.top = rectF.top;
        endTransform.width = rectF.width();
        endTransform.height = rectF.height();
        animTransform = endTransform.clone();

//        Log.d("hhh", "hhh" +
//                "  scale=" + endTransform.scale +
//                "  left=" + endTransform.left +
//                "  top=" + endTransform.top +
//                "  width=" + endTransform.width +
//                "  height=" + endTransform.height);

    }

    public interface OnTransFromListener {
        void onTransFormIn(float progress);// 0.0f -> 1.0f

        void onTransFormOut(float progress);// 1.0f -> 0.0f
    }

    private OnTransFromListener onTransFromListener;


    public void setOnTransFormListener(SmoothPhotoView.OnTransFromListener onTransFormListener) {
        this.onTransFromListener = onTransFormListener;
    }
}
