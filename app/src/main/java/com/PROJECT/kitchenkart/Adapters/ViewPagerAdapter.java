package com.PROJECT.kitchenkart.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.PROJECT.kitchenkart.Fragments.CurrentOrdersFragment;
import com.PROJECT.kitchenkart.Fragments.PastOrdersFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new CurrentOrdersFragment();
        } else {
            return new PastOrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // We have 2 tabs: Current Orders and Past Orders
    }
}
