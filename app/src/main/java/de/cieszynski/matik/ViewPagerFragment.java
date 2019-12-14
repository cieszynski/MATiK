package de.cieszynski.matik;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ViewPagerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        final ViewPager2 viewPager2 = view.findViewById(R.id.view_pager);
        ViewPagerFragmentStateAdapter viewPagerFragmentStateAdapter = new ViewPagerFragmentStateAdapter(getActivity());
        viewPager2.setAdapter(viewPagerFragmentStateAdapter);
        viewPager2.setUserInputEnabled(false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        viewPager2.setCurrentItem(0, false);
                        break;
                    case R.id.nav_formular:
                        viewPager2.setCurrentItem(1, false);
                        break;
                    case R.id.nav_bookmark:
                        viewPager2.setCurrentItem(2, false);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {

            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                // prevent reload
            }
        });
        return view;
    }

    private class ViewPagerFragmentStateAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            WebViewFragment webViewFragment = new WebViewFragment();
            Bundle bundle = new Bundle();

            switch (position) {
                case 2:
                    bundle.putString("url", "file:///android_asset/bookmarks.html");
                    break;
                case 1:
                    bundle.putString("url", "file:///android_asset/dictionary.html");
                    break;
                default:
                    bundle.putString("url", "file:///android_asset/index.html");
            }
            webViewFragment.setArguments(bundle);
            return webViewFragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}