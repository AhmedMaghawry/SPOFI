package com.ezzat.spofi.Control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ezzat.spofi.View.FirstRegFragment;
import com.ezzat.spofi.View.SecondRegFragment;
import com.ezzat.spofi.View.ThirdRegFragment;

public class PagerAdabter extends FragmentStatePagerAdapter {

    private static int NUM_ITEMS = 3;
    private FirstRegFragment first;
    private SecondRegFragment second;
    private ThirdRegFragment third;

        public PagerAdabter(FragmentManager fragmentManager) {
            super(fragmentManager);
            first = new FirstRegFragment();
            second = new SecondRegFragment();
            third = new ThirdRegFragment();
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return first;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return second;
                case 2: // Fragment # 1 - This will show SecondFragment
                    return third;
                default:
                    return first;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
}
