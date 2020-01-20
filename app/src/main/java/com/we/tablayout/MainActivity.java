package com.we.tablayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.we.lib.tablayout.WeTabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeTabLayout tabLayout = findViewById(R.id.dil_tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
        final String[] titles = {"移动", "四个字的", "小灵通", "NBA", "私密电影啊", "电影", "小知识", "篮球"};
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return CeshiFragment.newInstance();
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        });

        tabLayout.setTabLayout(R.layout.item_sliding_tab_layout);
        tabLayout.setIndicatorBottomMargin(10);
        tabLayout.setIndicatorEqualTabText(true);
        tabLayout.setTabFillContainer(false);
        tabLayout.attachToViewPager(viewPager, titles);
    }
}
