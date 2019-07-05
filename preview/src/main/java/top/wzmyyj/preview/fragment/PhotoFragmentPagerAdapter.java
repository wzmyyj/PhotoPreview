package top.wzmyyj.preview.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created on 2019/07/01.
 * <p>
 * fragment pager adapter.
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class PhotoFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<PhotoFragment> fragments;

    public PhotoFragmentPagerAdapter(FragmentManager fm, List<PhotoFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }
}