package top.wzmyyj.preview.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import top.wzmyyj.preview.R;
import top.wzmyyj.preview.base.OnExitListener;
import top.wzmyyj.preview.base.OnLockListener;
import top.wzmyyj.preview.base.PreviewConfig;
import top.wzmyyj.preview.base.PreviewHelper;
import top.wzmyyj.preview.enitity.ThumbImageInfo;
import top.wzmyyj.preview.utils.OwnerUtil;
import top.wzmyyj.preview.utils.PreviewManager;
import top.wzmyyj.preview.weight.BezierBannerView;
import top.wzmyyj.preview.weight.PhotoViewPager;

/**
 * Created on 2019/07/03
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class FPreviewHelperImpl implements PreviewHelper {
    //图片的地址
    private List<ThumbImageInfo> imgUrls;
    //当前图片的位置
    private int currentIndex;
    //是否点击黑色退出。
    private boolean isSingleFling;
    //图片的展示的Fragment
    private List<PhotoFragment> fragments = new ArrayList<>();
    //展示图片的viewPager
    private PhotoViewPager viewPager;
    //显示图片数
    private TextView ltAddDot;
    private BezierBannerView bezierBannerView;
    private PreviewConfig.IndicatorType type;
    private OnExitListener onExitListener;
    private PreviewConfig config;
    private WeakReference<LifecycleOwner> ownerWeakReference;


    @Override
    public void setOnExitListener(@NonNull OnExitListener onExitListener) {
        this.onExitListener = onExitListener;
    }

    @Override
    public void init(@NonNull LifecycleOwner owner) {
        ownerWeakReference = new WeakReference<>(owner);
        if (OwnerUtil.isFragment(owner)) {
            this.config = PreviewConfig.Builder.get((Fragment) owner);
        } else if (OwnerUtil.isActivity(owner)) {
            this.config = PreviewConfig.Builder.get((Activity) owner);
        } else {
            throw new RuntimeException("owner is not Fragment or FragmentActivity!");
        }
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_image_preview_photo;
    }

    @Override
    public void onCreate(@NonNull View v) {
        initData();
        initView(v);
    }

    @Override
    public void onDestroy() {
        FragmentActivity activity = OwnerUtil.getActivity(ownerWeakReference.get());
        if (activity != null) {
            PreviewManager.getInstance().getImageLoader().clearMemory(activity);
        }
        if (viewPager != null) {
            viewPager.setAdapter(null);
            viewPager.clearOnPageChangeListeners();
            viewPager.removeAllViews();
            viewPager = null;
        }
        if (fragments != null) {
            fragments.clear();
            fragments = null;
        }
        if (imgUrls != null) {
            imgUrls.clear();
            imgUrls = null;
        }
        onExitListener = null;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        imgUrls = config.getImgUrls();
        currentIndex = config.getCurrentIndex();
        type = config.getIndicatorType();
        isSingleFling = config.isSingleFling();
        if (imgUrls == null) {
            onExitListener.exit();
        }
    }

    /**
     * 初始化控件
     */
    @SuppressLint("SetTextI18n")
    private void initView(@NonNull View v) {
        viewPager = v.findViewById(R.id.viewPager);
        //viewPager的适配器
        FragmentManager fragmentManager = OwnerUtil.getFragmentManager(ownerWeakReference.get());
        if (fragmentManager == null) {
            throw new RuntimeException("FragmentManager is nell!");
        }
        for (int i = 0; i < imgUrls.size(); i++) {
            PhotoFragment photoFragment = PhotoFragment.getInstance(imgUrls.get(i).getUrl(),
                    imgUrls.get(i).getRect(),
                    currentIndex == i,
                    isSingleFling);
            photoFragment.setOnExitListener(onExitListener);
            photoFragment.setOnLockListener(new OnLockListener() {
                @Override
                public void lock(boolean isLock) {
                    viewPager.setLock(isLock);
                }
            });
            fragments.add(photoFragment);
        }
        PhotoFragmentPagerAdapter adapter = new PhotoFragmentPagerAdapter(fragmentManager, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
        viewPager.setOffscreenPageLimit(3);
        if (type == PreviewConfig.IndicatorType.Dot) {
            bezierBannerView = v.findViewById(R.id.bezierBannerView);
            bezierBannerView.setVisibility(View.VISIBLE);
            bezierBannerView.attachToViewpager(viewPager);
        } else {
            ltAddDot = v.findViewById(R.id.ltAddDot);
            ltAddDot.setVisibility(View.VISIBLE);
            ltAddDot.setText(currentIndex + 1 + "/" + imgUrls.size());
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //当被选中的时候设置小圆点和当前位置
                    if (ltAddDot != null) {
                        ltAddDot.setText((position + 1) + "/" + imgUrls.size());
                    }
                    currentIndex = position;
                    viewPager.setCurrentItem(currentIndex, true);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                PhotoFragment fragment = fragments.get(currentIndex);
                fragment.transformIn();
            }
        });


    }


    /**
     * 退出预览的动画。
     */
    @Override
    public void transformOut() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < imgUrls.size()) {
            PhotoFragment fragment = fragments.get(currentItem);
            if (ltAddDot != null) {
                ltAddDot.setVisibility(View.GONE);
            } else {
                bezierBannerView.setVisibility(View.GONE);
            }
            fragment.transformOut();
        } else {
            onExitListener.exit();
        }
    }

}
