package per.macc.ahback.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import per.macc.ahback.R;
import per.macc.ahback.fragment.PageFragment;

/**
 * 页面的适配器
 * Created by Macc on 2016/5/31.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    //3个Tab
    private String tabTitles[] = new String[3];
    private Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    //设置Tab名称
    public void setTitle(String [] titles)
    {
        tabTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}