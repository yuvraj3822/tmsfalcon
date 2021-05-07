package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.accidentIntro;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class IntroPagerAdapter extends FragmentPagerAdapter {

    public IntroPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {

            case 0:
                return new Intro1AccFragment(); //ChildFragment1 at position 0
            case 1:
                return new Intro2AccFragment(); //ChildFragment2 at position 1
            case 2:
                return new Intro3AccFragment(); //ChildFragment3 at position 2
            case 3:
                return new Intro4AccFragment(); //ChildFragment3 at position 2
            case 4:
                return new Intro5AccFragment(); //ChildFragment3 at position 2

        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 5; //three fragments
    }
}