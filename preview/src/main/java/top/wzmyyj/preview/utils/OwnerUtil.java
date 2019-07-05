package top.wzmyyj.preview.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created on 2019/07/04
 *
 * LifecycleOwner是fragment还是fragmentActivity。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class OwnerUtil {

    /**
     * @param owner fragment or fragmentActivity.
     * @return isFragment.
     */
    public static boolean isFragment(@Nullable LifecycleOwner owner) {
        if (owner == null) return false;
        return owner instanceof Fragment;
    }

    /**
     * @param owner fragment or fragmentActivity.
     * @return isActivity.
     */
    public static boolean isActivity(@Nullable LifecycleOwner owner) {
        if (owner == null) return false;
        return owner instanceof FragmentActivity;
    }

    /**
     * @param owner fragment or fragmentActivity.
     * @return fragment or null.
     */
    @Nullable
    public static Fragment getFragment(@Nullable LifecycleOwner owner) {
        if (owner == null) return null;
        if (isFragment(owner)) {
            return (Fragment) owner;
        }
        return null;
    }

    /**
     * @param owner fragment or fragmentActivity.
     * @return activity or null.
     */
    @Nullable
    public static FragmentActivity getActivity(@Nullable LifecycleOwner owner) {
        if (owner == null) return null;
        if (isActivity(owner)) {
            return (FragmentActivity) owner;
        } else if (isFragment(owner)) {
            return ((Fragment) owner).getActivity();
        }
        return null;
    }

    /**
     * @param owner fragment or fragmentActivity.
     * @return fragmentManger or null.
     */
    @Nullable
    public static FragmentManager getFragmentManager(@Nullable LifecycleOwner owner) {
        if (owner == null) return null;
        if (isActivity(owner)) {
            return ((FragmentActivity) owner).getSupportFragmentManager();
        } else if (isFragment(owner)) {
            return ((Fragment) owner).getChildFragmentManager();
        }
        return null;
    }


}
