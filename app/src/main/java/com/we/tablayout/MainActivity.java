package com.we.tablayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import cn.we.tablayout.WeTabLayout;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WeTabLayout tabLayout = findViewById(R.id.dil_tablayout);
        final WeTabLayout tabLayoutTwo = findViewById(R.id.dil_tablayout2);
        final WeTabLayout tabLayoutThree = findViewById(R.id.dil_tablayout3);



        ViewPager viewPager = findViewById(R.id.viewpager);
        final String[] titles = {"移动", "四个字的", "小灵通", "这个很长电影啊", "NBA", "电影", "小知识", "篮球"};
        final String[] titlesTwo = {"移动", "四个字的", "小灵通"};


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

        tabLayout.setTabLayoutIds(R.layout.item_sliding_tab_layout);


        tabLayoutThree.attachToViewPager(viewPager,titlesTwo);
        tabLayoutTwo.attachToViewPager(viewPager,titlesTwo);
        tabLayout.attachToViewPager(viewPager, titles);

    }
}
