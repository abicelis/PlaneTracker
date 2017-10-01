package ve.com.abicelis.planetracker.ui.tracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by abice on 30/1/2017.
 */

public class TrackerViewPagerAdapter extends FragmentPagerAdapter {

    //DATA
    private List<Fragment> mFragmentList;                                                   //Holds all of the fragments
    private SparseArray<FlightFragment> mRegisteredFragmentList = new SparseArray<>();      //Holds only registered fragments


    public TrackerViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }




    /* These methods are for maintaining and getting registered fragments */

    public FlightFragment getRegisteredFragment(int position) {
        return mRegisteredFragmentList.get(position);
    }

    public SparseArray<FlightFragment> getRegisteredFragments() {
        return mRegisteredFragmentList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragmentList.put(position, (FlightFragment) fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragmentList.remove(position);
        super.destroyItem(container, position, object);
    }
}
