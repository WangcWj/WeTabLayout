### `WeTabLayout`
 实战项目中运行正常，运行4年多暂无遇见意外问题。

#### 一 介绍：

导航功能几乎是所有的`APP`都具备的基础功能之一，Android系统提供了`TabLayout+ViewPager`的组合来实现该功能。再使用之初，发现该组合真是无敌了，但是。。。总有一些`UI`设计师觉得原生`TabLayout`的下划线样式不符合用户的审美，比如说，下划线的宽度要跟文本的宽度一样，下划线的样式要换成图片等等。`TabLayout`在这些需求面前显得那么无助，程序员被迫搬砖。

`WeTabLayout`继承自`HorizontalScrollView`，这是为了实现当有多个`Tab`的时候能够左右滑动，且当滑动的时候将选中的那个`Tab`移至屏幕中间。其直接父布局为`LinearLayout`，再设置`Tab`充满父布局，或者是水平自由排列的时候很方便。下划线由`Drawable`绘制，这样的话就可以随意的更改下划线的样式，设置宽高、设置图片、设置`Shape`等。其控件已经在应用中使用，目前来看相当稳定，之后打算用来替换原生的`TabLayout`；

**为什么使用`WeTabLayout`？** 

1. 实现了`TabLayout`的基本功能。
2. 扩展了在`ViewPager`+`WeTabLayout`有多个`Tab`时，`ViewPager`左右滑动的时候默认将当前选中的Tab移动到屏幕中间。
3. 完全自定义的`Indicator`指示器，通俗一点就是下划线。其宽度、高度、颜色、样式、图片全部支持。
4. 开关控制`Indicator`指示器的宽度是否跟`Tab`显示的文本宽度一样。
5. `TabView`支持自定义布局或者使用默认布局。其`TabView`在父布局中的位置，以及各个`TabView`之间的间距，完全自定义，也可选择`TabView`填充满父布局。
6. 支持`TabView`的左右padding。
7. 最重要的是`WeTabLayout`的源码简单，可以自行定制。
#### 注意：
1. 为了能够及时的发现并更正该库所存在的问题，现邀请大家加入该微信群中，三人行必有我师，Android技术是无止境的。
2. 当使用自定义Tab布局的时候，其设置的padding值，不要跟属性`wtl_tab_padding_left`和`wtl_tab_padding_right`的值冲突。
3. 默认可以不用设置`TabView`的自定义布局，也就是可以不用给`mTabLayout`属性赋值，如果设置自定义布局的话，自定义布局的背景色会被忽略掉。

有问题可以先加入`QQ`群` 684891631 `再转微信群~

##### 更新日志：

* 2020-04-02：

  更新了`TabView`对左右padding的支持，可以随意的设置左右的padding值，下划线的位置不会错乱。

#### 二 简单使用：

##### 2.1 `XML`布局准备：

```xml
  <WeTabLayout
        android:id="@+id/dil_tablayout"
        android:layout_width="match_parent"
        android:layout_height="56dp" />

    <androidx.viewpager.widget.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="56dp" />
    
   //自定义属性示例。
     <WeTabLayout
        android:id="@+id/dil_tablayout"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        app:wtl_default_text_color="@color/colorPrimary"
        app:wtl_indicator_margin_bottom="10dp"
        app:wtl_indicator_width_equal_title="true"
        app:wtl_selected_text_color="@color/colorAccent"
        app:wtl_indicator_color="@color/colorAccent"
        app:wtl_selected_text_size="16sp"
        app:wtl_default_text_size="12sp"
        app:wtl_indicator_height="1dp"
        app:wtl_indicator_width="50dp"
        app:wtl_selected_text_bold="true"
        app:wtl_tab_padding_left="30dp"
        app:wtl_tab_padding_right="16dp"
        app:wtl_indicator_corner_radius="4dp"
        app:wtl_tab_fill_container="false" />   
```

具体字段解释：

```xml
 <declare-styleable name="WeTabLayout">
        <!-- indicator -->
        //下划线的颜色。
        <attr name="wtl_indicator_color" format="color" />
        //下划线的高度。
        <attr name="wtl_indicator_height" format="dimension" />
        //下划线的宽度。
        <attr name="wtl_indicator_width" format="dimension" />
        //下划线距离底部的Margin。
        <attr name="wtl_indicator_margin_bottom" format="dimension" />
        //下划线的圆角。
        <attr name="wtl_indicator_corner_radius" format="dimension" />
        //下划线是否的宽度是否跟文本的宽度一样。 true是。
        <attr name="wtl_indicator_width_equal_title" format="boolean" />
        //Tab被选中时的文字大小。
        <attr name="wtl_selected_text_size" format="dimension" />
        //Tab默认时的文字大小。
        <attr name="wtl_default_text_size" format="dimension" />
        //Tab被选中时的文本的颜色。
        <attr name="wtl_selected_text_color" format="color" />
        //Tab默认时的文字大小。
        <attr name="wtl_default_text_color" format="color" />
        //Tab被选中时是否加粗 true 是。
        <attr name="wtl_selected_text_bold" format="boolean" />
        //Tab是否填充满父View，true 是。
        <attr name="wtl_tab_fill_container" format="boolean" />
        //Tab的左padding。 
        <attr name="wtl_tab_padding_left" format="dimension" />
        //Tab的右padding。
        <attr name="wtl_tab_padding_right" format="dimension" />
    </declare-styleable>
```

**属性使用注意：** 

1. `wtl_indicator_width_equal_title`跟`wtl_indicator_width`同时设置的时候，以前者为主。

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



