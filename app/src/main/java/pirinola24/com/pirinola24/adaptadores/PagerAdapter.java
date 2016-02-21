package pirinola24.com.pirinola24.adaptadores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import pirinola24.com.pirinola24.fragments.FragmentGeneric;

/**
 * Created by geovanny on 9/01/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter
{
    private  List<FragmentGeneric> data;

    public PagerAdapter(FragmentManager fm,List<FragmentGeneric> data)
    {
        super(fm);
        this.data=data;
    }

    @Override
    public Fragment getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public int getCount()
    {
        return data.size();
    }


}
