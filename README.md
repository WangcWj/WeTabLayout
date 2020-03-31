### `WeTabLayout`

#### 一 介绍：

导航功能几乎是所有的`APP`都具备的基础功能之一，Android系统提供了`TabLayout+ViewPager`的组合来实现该功能。再使用之初，发现该组合真是无敌了，但是。。。总有一些`UI`设计师觉得原生`TabLayout`的下划线样式不符合用户的审美，比如说，下划线的宽度要跟文本的宽度一样，下划线的样式要换成图片等等。`TabLayout`在这些需求面前显得那么无助，程序员被迫搬砖。

`WeTabLayout`继承自`HorizontalScrollView`，这是为了实现当有多个`Tab`的时候能够左右滑动，且当滑动的时候将选中的那个`Tab`移至屏幕中间。其直接父布局为`LinearLayout`，再设置`Tab`充满父布局，或者是水平自由排列的时候很方便。下划线由`Drawable`绘制，这样的话就可以随意的更改下划线的样式，设置宽高、设置图片、设置`Shape`等。其控件已经在应用中使用，目前来看相当稳定，之后打算用来替换原生的`TabLayout`；

**为什么使用`WeTabLayout`？** 

1. 实现了`TabLayout`的基本功能。
2. 扩展了在`ViewPager`+`WeTabLayout`有多个`Tab`时，`ViewPager`左右滑动的时候默认将当前选中的Tab移动到屏幕中间。
3. 完全自定义的`Indicator`指示器，通俗一点就是下划线。其宽度、高度、颜色、样式、图片全部支持。
4. 开关控制`Indicator`指示器的宽度是否跟`Tab`显示的文本宽度一样。
5. `TabView`支持自定义布局或者使用默认布局。其`TabView`在父布局中的位置，以及各个`TabView`之间的间距，完全自定义，也可选择`TabView`填充满父布局。
6. 最重要的是`WeTabLayout`的源码简单，可以自行定制。

#### 二 简单使用：

##### 2.1 `XML`布局准备：

```java
  <com.we.lib.tablayout.WeTabLayout
        android:id="@+id/dil_tablayout"
        android:layout_width="match_parent"
        android:layout_height="56dp" />

    <androidx.viewpager.widget.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="56dp" />
```

##### 2.2 代码设置：

```java
//Step 1 :查找对象
WeTabLayout tabLayout = findViewById(R.id.dil_tablayout);
ViewPager viewPager = findViewById(R.id.viewpager);
String[] titles = {"移动", "四个字的", "小灵通", "这个很长电影啊", "NBA", "电影", "小知识", "篮球"};
//重要的一步。
tabLayout.attachToViewPager(viewPager, titles);
```

**注意：** 调用 `tabLayout.setCurrentTab()`方法设置默认选中的`Tab`的时候，必须在`ViewPager`设置`Adapter`之后。

##### 2.3 更多属性设置：

```java
   //设置tabView的布局id，比如:R.layout.item_sliding_tab_layout. 
   public void setTabLayoutIds(int mTabLayout) {}
   //这是一个接口回调，当WeTabLayout创建每个TabView的时候，会调用IHandleTab接口的方法。
   //可以再改回调中改变一下TabView的一些属性。
   public void addHandleTabCallBack(@NonNull IHandleTab mHandleTab)}
   //设置Tab选中之后的回调。
   public void setTabSelectedListener(WeTabSelectedListener mTabSelectedListener) {}
   //设置选中的Tab根据传入的index。
   public void setCurrentTab(int mCurrentTab) {}
   //设置TabView填充满WeTabLayout。其原理就是给每个TabView设置whight = 1。
   public void setTabFillContainer(boolean fill) {}
   //设置下划线的颜色。
   public void setIndicatorColor(int mIndicatorColor) {}
   //设置下划线的高度，不需要转换成dp。
   public void setIndicatorHeight(int mIndicatorHeight) {}
   //设置下划线的宽度，不需要转换成dp。
   public void setIndicatorWidth(int mIndicatorWidth) {}
   //设置tab选中时文本的颜色。
   public void setSelectedTabTextColor(int mSelectedTabTextColor) {}
   //设置tab未选中时文本的颜色。
   public void setDefaultTabTextColor(int mDefaultTabTextColor) {}
   //设置下划线距离底部的margin。
   public void setIndicatorBottomMargin(int margin) {}
   //true 下划线的宽度跟Tab文本的宽度一样。 false的话 下划线的宽度跟TabView的宽度一样。
   public void setIndicatorEqualTabText(boolean mIndicatorEqualTabText) {}
```

##### 2.4 属性应用Demo：

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WeTabLayout tabLayout = findViewById(R.id.dil_tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
        final String[] titles = {"移动", "四个字的", "小灵通", "这个很长电影啊", "NBA", "电影", "小知识", "篮球"};
        findViewById(R.id.selected_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.setCurrentTab(3);
            }
        });
        tabLayout.setTabLayoutIds(R.layout.item_sliding_tab_layout);
        tabLayout.setIndicatorBottomMargin(10);
        tabLayout.setIndicatorEqualTabText(true);
        tabLayout.setTabFillContainer(false);
        tabLayout.setCurrentTab(2);
        tabLayout.addHandleTabCallBack(new IHandleTab() {
            @Override
            public void addTab(View tab, int index) {
                //该方法再创建TabView的时候调用。
            }
        });
        tabLayout.setTabSelectedListener(new WeTabSelectedListener() {
            @Override
            public void onTabSelected(View currentTab, int position) {
                //TabView被选中的时候调用。
            }
            @Override
            public void onPreTabSelected(View preTab, int prePosition) {
                 //上一个选中的TabView。
            }
        });
    
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
        tabLayout.attachToViewPager(viewPager, titles);
    }
}
```



