package bo.ara.com.kidstravel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bo.ara.com.kidstravel.TravelsFragment;
import bo.ara.com.kidstravel.UserFragment;

/**
 * Created by LENOVO on 8/19/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    boolean adminAccount;
    String travelStatus;

    TravelsFragment travelsFragment;
    UserFragment userFragment;

    public ViewPagerAdapter(FragmentManager fm, boolean admin, String travelStatus) {
        super(fm);

        this.adminAccount = admin;
        this.travelStatus = travelStatus;
    }

    @Override
    public Fragment getItem(int position) {

        if(adminAccount){
            switch(position){
                case 0:
                    //travelsFragment = new TravelsFragment();
                    travelsFragment = TravelsFragment.newInstance(adminAccount, travelStatus);
                    return travelsFragment;
                case 1:
                    userFragment = new UserFragment();
                    return userFragment;
                default:
                    return null;
            }
        }
        else{
            switch(position){
                case 0:
                    //travelsFragment = new TravelsFragment();
                    travelsFragment = TravelsFragment.newInstance(adminAccount, travelStatus);
                    return travelsFragment;
                default:
                    return null;
            }
        }

    }

    @Override
    public int getCount() {
        if(adminAccount)
            return 2;
        else
            return 1;
    }

    public TravelsFragment getTravelsFragment() {
        return travelsFragment;
    }

    public UserFragment getUserFragment() {
        return userFragment;
    }
}