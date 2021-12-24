package cn.we.tablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created to : 一款自定义下划线的仿TabLayout。
 *
 * @author cc.wang
 * @date 2020/1/13
 */
public class WeTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    public WeTabLayout(Context context) {
        this(context, null, 0);
    }

    public WeTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public static final String TAB_TAG = "tab_text";


    private Context mContext;

    /**
     * Tab内容的集合。
     */
    private List<String> mTitles;

    /**
     * 控件是否已经初始化，true的话表示{@link #initView(Context, AttributeSet)}已经走过了。
     */
    private boolean mHaveInit = false;

    /**
     * ViewPager。
     */
    private ViewPager mViewPager;

    /**
     * TabView的父布局。是个LinearLayout。
     */
    private LinearLayout mTabContainer;
    private int mTabContainerGravity = Gravity.CENTER;
    private int mCurrentScrollTab = 0;
    private int mCurrentTab = 0;

    /**
     * TabView相关的配置。
     */
    private int mTabCount;
    private int mTabLayout = -1;
    private int mSelectedTabTextColor;
    private int mDefaultTabTextColor;
    private float mSelectedTabTextSize;
    private float mDefaultTabTextSize;
    private boolean mSelectedTabTextStyleBold = false;
    private boolean mTabFillContainer = true;

    /**
     * 下划线相关的配置。
     */
    private Drawable mIndicatorDrawable;
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private boolean mIndicatorEqualTabText = false;
    private float mIndicatorBottomMargin;
    private int mIndicatorColor = Color.RED;
    private float mIndicatorCorner;
    private Rect mIndicatorRect;

    /**
     * {@link IHandleTab}TabView创建的时候，会执行该回调接口，可以另外的对TabView做一些别的操作。
     */
    private IHandleTab mHandleTab;

    /**
     * 记录一下滑动的相对距离，如果跟最新的一样的话就不做操作。
     */
    private int mLastScrollX = 0;

    /**
     * 可以给下划线设置一个资源图片。
     */
    private int mResId;

    /**
     * 用来测量TextView的宽度，一般是文本的显示。
     */
    private Paint mTextPaint;

    /**
     * ViewPager滑动中的偏移量。
     */
    private float mPositionOffset;

    private boolean mAttachSuccess = false;

    private WeTabSelectedListener mTabSelectedListener;

    private float mTabPaddingLeft = 0;
    private float mTabPaddingRight = 0;

    /**
     * Tab设置左右上下的Icon。
     */
    private Map<String, WeTabDrawable> mTabDrawables;

    public WeTabLayout addTabDrawable(WeTabDrawable drawable) {
        if (null == drawable) {
            return this;
        }
        if (null == mTabDrawables) {
            mTabDrawables = new HashMap<>();
        }
        mTabDrawables.put(drawable.getTabName(), drawable);
        return this;
    }

    public void setIndicatorResId(@DrawableRes int id) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            if (null != bitmap) {
                mIndicatorDrawable = new BitmapDrawable(getResources(), bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIndicatorCorner(float indicatorCorner) {
        this.mIndicatorCorner = mIndicatorCorner;
        if (mIndicatorDrawable instanceof GradientDrawable) {
            ((GradientDrawable) mIndicatorDrawable).setCornerRadius(mIndicatorCorner);
        }
    }

    public void setTabLayoutIds(int mTabLayout) {
        this.mTabLayout = mTabLayout;
    }

    public void addHandleTabCallBack(@NonNull IHandleTab mHandleTab) {
        this.mHandleTab = mHandleTab;
    }

    public void setTabSelectedListener(WeTabSelectedListener mTabSelectedListener) {
        this.mTabSelectedListener = mTabSelectedListener;
    }

    public void setTabContainerGravity(int mTabContainerGravity) {
        this.mTabContainerGravity = mTabContainerGravity;
    }

    public void setCurrentTab(int mCurrentTab) {
        this.mCurrentTab = mCurrentTab;
        if (haveInit() && mAttachSuccess) {
            mViewPager.setCurrentItem(mCurrentTab);
        }
    }

    public void setTabFillContainer(boolean fill) {
        this.mTabFillContainer = fill;
    }

    public void setIndicatorColor(int mIndicatorColor) {
        this.mIndicatorColor = mIndicatorColor;
    }

    public void setIndicatorColorRes(@IdRes int mIndicatorColor) {
        this.mIndicatorColor = getColorResource(mIndicatorColor);
    }

    public void setIndicatorHeight(int mIndicatorHeight) {
        this.mIndicatorHeight = mIndicatorHeight;
    }

    public void setIndicatorWidth(int mIndicatorWidth) {
        if (mIndicatorEqualTabText) {
            mIndicatorWidth = 0;
        }
        this.mIndicatorWidth = mIndicatorWidth;
    }

    public void setSelectedTabTextColor(int mSelectedTabTextColor) {
        this.mSelectedTabTextColor = mSelectedTabTextColor;
    }

    public void setDefaultTabTextColor(int mDefaultTabTextColor) {
        this.mDefaultTabTextColor = mDefaultTabTextColor;
    }

    public void setIndicatorBottomMargin(int margin) {
        this.mIndicatorBottomMargin = margin;
    }

    public void setIndicatorEqualTabText(boolean mIndicatorEqualTabText) {
        if (mIndicatorEqualTabText) {
            mIndicatorWidth = 0;
        }
        this.mIndicatorEqualTabText = mIndicatorEqualTabText;
    }

    private void initView(Context context, AttributeSet attrs) {
        loadAttribute(context, attrs);
        inflateOther(context);
    }

    private void loadAttribute(Context context, AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = context.obtainStyledAttributes(attrs, R.styleable.WeTabLayout);
            mIndicatorColor = array.getColor(R.styleable.WeTabLayout_wtl_indicator_color, Color.BLACK);
            mIndicatorHeight = array.getDimension(R.styleable.WeTabLayout_wtl_indicator_height, 1);
            mIndicatorWidth = array.getDimension(R.styleable.WeTabLayout_wtl_indicator_width, 0);
            mIndicatorBottomMargin = array.getDimension(R.styleable.WeTabLayout_wtl_indicator_margin_bottom, 0);
            mIndicatorEqualTabText = array.getBoolean(R.styleable.WeTabLayout_wtl_indicator_width_equal_title, false);
            mIndicatorCorner = array.getDimension(R.styleable.WeTabLayout_wtl_indicator_corner_radius, 0);
            mTabPaddingLeft = array.getDimension(R.styleable.WeTabLayout_wtl_tab_padding_left, 0);
            mTabPaddingRight = array.getDimension(R.styleable.WeTabLayout_wtl_tab_padding_right, 0);
            if (mIndicatorEqualTabText) {
                mIndicatorWidth = 0;
            }
            mSelectedTabTextColor = array.getColor(R.styleable.WeTabLayout_wtl_selected_text_color, Color.BLACK);
            mDefaultTabTextColor = array.getColor(R.styleable.WeTabLayout_wtl_default_text_color, Color.GRAY);
            mDefaultTabTextSize = array.getDimension(R.styleable.WeTabLayout_wtl_default_text_size, sp2px(12));
            mSelectedTabTextSize = array.getDimension(R.styleable.WeTabLayout_wtl_selected_text_size, sp2px(14));
            mSelectedTabTextStyleBold = array.getBoolean(R.styleable.WeTabLayout_wtl_selected_text_bold, false);
            mTabFillContainer = array.getBoolean(R.styleable.WeTabLayout_wtl_tab_fill_container, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != array) {
                array.recycle();
            }
        }
    }

    private void inflateOther(Context context) {
        initScrollView();
        mContext = context;
        mTabContainer = new LinearLayout(context);
        addView(mTabContainer);
        mIndicatorDrawable = new GradientDrawable();
        if (mIndicatorCorner > 0) {
            ((GradientDrawable) mIndicatorDrawable).setCornerRadius(mIndicatorCorner);
        }
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorRect = new Rect();
        mHaveInit = true;
    }

    private void initScrollView() {
        //设置滚动视图是否可以伸缩其内容以填充视口
        setFillViewport(true);
        //重写onDraw方法,需要调用这个方法来清除flag,要不然不会执行重写的onDraw().
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
    }

    /**
     * 结合ViewPager，ViewPager必须设置好Adapter。
     *
     * @param viewPager
     * @param titles
     */
    public void attachToViewPager(ViewPager viewPager, String[] titles) {
        if (null == titles || titles.length <= 0) {
            return;
        }
        List<String> list = Arrays.asList(titles);
        attachToViewPager(viewPager, list);
    }

    public void attachToViewPager(ViewPager viewPager, List<String> titles) {
        if (!checkInitState(viewPager)) {
            return;
        }
        if (null == titles || titles.size() <= 0) {
            return;
        }
        mTitles.clear();
        mTitles.addAll(titles);
        this.mViewPager = viewPager;
        mViewPager.setCurrentItem(mCurrentTab);
        viewPager.removeOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        mTabContainer.removeAllViews();
        mTabCount = mTitles.size();
        createTab();
        mAttachSuccess = true;
    }

    private boolean checkInitState(ViewPager viewPager) {
        if (!haveInit()) {
            return false;
        }
        if (null == viewPager) {
            return false;
        }
        if (mTitles == null) {
            mTitles = new ArrayList<>();
        }
        return true;
    }

    /**
     * 创建TabView。如果有设置Tab的布局文件，就使用布局文件，没有的话就自己创建TextView。
     */
    private void createTab() {
        View childView;
        for (int i = 0; i < mTabCount; i++) {
            if (mTabLayout <= 0) {
                childView = new TextView(mContext);
            } else {
                childView = View.inflate(mContext, mTabLayout, null);
            }
            if (null == childView) {
                continue;
            }
            childView.setPadding((int) mTabPaddingLeft, 0, (int) mTabPaddingRight, 0);
            if (childView instanceof TextView) {
                setStyle((TextView) childView, childView, i);
            } else {
                TextView tabView = childView.findViewWithTag(TAB_TAG);
                setStyle(tabView, childView, i);
            }
            if (null != mHandleTab) {
                mHandleTab.addTab(childView, i);
            }
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mTabContainer.indexOfChild(v);
                    if (position >= 0) {
                        if (mViewPager.getCurrentItem() != position) {
                            mViewPager.setCurrentItem(position);
                        }
                    }
                }
            });
        }
    }

    /**
     * 选中Tab时，更改Tab的文本样式。
     *
     * @param selectedIndex
     */
    private void selectedTab(int selectedIndex) {
        if (null == mTabContainer || mTabContainer.getChildCount() == 0) {
            return;
        }
        int childCount = mTabContainer.getChildCount();
        View cView = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = mTabContainer.getChildAt(i);
            TextView selected;
            if (childAt instanceof TextView) {
                selected = (TextView) childAt;
            } else {
                selected = findTabTextView(childAt);
            }
            if (null != selected) {
                if (i == selectedIndex) {
                    cView = childAt;
                }
                setSelectedTabStyle(selected, i == selectedIndex);
            }
        }
        if (null != mTabSelectedListener) {
            if (null != cView) {
                mTabSelectedListener.onTabSelected(cView, selectedIndex);
            }
            if (mCurrentTab >= 0 && mCurrentTab < mTabContainer.getChildCount()) {
                View childAt = mTabContainer.getChildAt(mCurrentTab);
                if (null != childAt) {
                    mTabSelectedListener.onPreTabSelected(childAt, mCurrentTab);
                }
            }
        }
    }

    private void setSelectedTabStyle(TextView selected, boolean isSelected) {
        selected.setTextColor(isSelected ? mSelectedTabTextColor : mDefaultTabTextColor);
        selected.setTextSize(TypedValue.COMPLEX_UNIT_PX, isSelected ? mSelectedTabTextSize : mDefaultTabTextSize);
        if (mSelectedTabTextStyleBold) {
            selected.getPaint().setFakeBoldText(isSelected);
        }
    }

    private TextView findTabTextView(View parent) {
        return parent.findViewWithTag(TAB_TAG);
    }

    /**
     * 设置Tab的样式 ：
     * 1. 选中状态： 字体大小，字体颜色，加粗。
     * 2. 默认状态：字体大小，字体颜色，加粗。
     *
     * @param childView
     * @param tabView
     * @param index
     */
    private void setStyle(TextView childView, View tabView, int index) {
        if (null == childView || null == tabView || null == mTabContainer) {
            return;
        }

        String title = mTitles.get(index);
        if (null != mTabDrawables) {
            WeTabDrawable weTabDrawable = mTabDrawables.get(title);
            if (null != weTabDrawable) {
                childView.setCompoundDrawables(
                        measureDrawable(weTabDrawable.getDrawableByGravity(Gravity.LEFT)),
                        measureDrawable(weTabDrawable.getDrawableByGravity(Gravity.TOP)),
                        measureDrawable(weTabDrawable.getDrawableByGravity(Gravity.RIGHT)),
                        measureDrawable(weTabDrawable.getDrawableByGravity(Gravity.BOTTOM)));
            }
        }
        //如果布局有背景的时候，要清掉，不清掉的时候会遮挡住绘制的指示器。
        clearBackground(tabView);
        clearBackground(childView);

        childView.setText(mTitles.get(index));
        childView.setGravity(Gravity.CENTER);
        setSelectedTabStyle(childView, index == mCurrentTab);
        LinearLayout.LayoutParams tabLayoutParams = getTabLayoutParams();
        mTabContainer.setGravity(mTabContainerGravity);
        mTabContainer.addView(tabView, index, tabLayoutParams);
    }

    public Drawable measureDrawable(Drawable drawable) {
        if (null != drawable) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        return drawable;
    }

    /**
     * TabView不允许有背景的，有的话会遮挡下划线的。
     *
     * @param tabView
     */
    private void clearBackground(View tabView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tabView.setBackground(null);
        }
    }

    /**
     * 给tabView默认的LayoutParams,如果子View是默认填充满布局的也就是{@link #mTabFillContainer = true}，
     * 这时就要设置weight = 1；
     *
     * @return
     */
    private LinearLayout.LayoutParams getTabLayoutParams() {
        if (mTabFillContainer) {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        } else {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void lear() {
        if (!haveInit()) {
            return;
        }
        mAttachSuccess = false;
        mTabContainer.removeAllViews();
        mViewPager.setAdapter(null);
    }

    /**
     * 绘制下划线，下划线的本身是一个Drawable对象，其本身大小用Rect来约束。
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!haveInit()) {
            return;
        }
        if (isInEditMode() || mTabCount <= 0) {
            return;
        }
        computeIndicatorRect();

        if (mIndicatorDrawable instanceof GradientDrawable) {
            ((GradientDrawable) mIndicatorDrawable).setColor(mIndicatorColor);
        }
        mIndicatorDrawable.setBounds(mIndicatorRect);
        mIndicatorDrawable.draw(canvas);
    }

    /**
     * HorizontalScrollView滚到当前tab,并且居中显示
     */
    private void scrollToCurrentTab() {

        if (mTabCount <= 0 || mPositionOffset <= 0) {
            return;
        }
        View childAt = mTabContainer.getChildAt(mCurrentScrollTab);
        if (null == childAt) {
            return;
        }

        int offset = (int) (mPositionOffset * childAt.getWidth());
        int newScrollX = childAt.getLeft() + offset;
        //布局的中心距离。
        int centerDistance = getWidth() / 2;

        int herf = 0;
        if (mCurrentScrollTab < mTabCount - 1) {
            View nextTab = mTabContainer.getChildAt(mCurrentScrollTab + 1);
            if (null != nextTab) {
                float leftDistance = childAt.getLeft() + (nextTab.getLeft() - childAt.getLeft()) * mPositionOffset;
                float rightDistance = childAt.getRight() + (nextTab.getRight() - childAt.getRight()) * mPositionOffset;
                herf = (int) ((rightDistance - leftDistance) / 2);
            }
        }

        if (mCurrentScrollTab > 0 || offset > 0) {
            //这个算的当前的Tab距离中心点的位置
            newScrollX -= centerDistance;
            //后面这个是下一个Tab距离中心点的位置。
            newScrollX += herf;
        }
        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    /**
     * 计算下划线的大小，用Rect来表示。该Rect用来决定Drawable的大小。
     */
    private void computeIndicatorRect() {
        View childAt = mTabContainer.getChildAt(mCurrentScrollTab);
        int childCount = mTabContainer.getChildCount();
        if (null == childAt) {
            return;
        }
        int left = childAt.getLeft();
        int right = childAt.getRight();
        int bottom = childAt.getBottom();
        if (mIndicatorWidth > 0) {
            left += getTabInsert(childAt);
            right -= getTabInsert(childAt);
        }

        //当前的Tab。计算文本的宽度的时候要排除掉左右的padding。
        int margin = measureText(childAt, left + (int) mTabPaddingLeft, right - (int) mTabPaddingRight);
        int marginLeft = handlerPadding(true, margin);
        int marginRight = handlerPadding(false, margin);

        //滑动中的。
        if (mCurrentScrollTab < childCount - 1) {
            View nextChild = mTabContainer.getChildAt(mCurrentScrollTab + 1);
            if (null == nextChild) {
                return;
            }
            //这个是要移动的距离。
            float leftDistance = (nextChild.getLeft() - left);
            float rightDistance = (nextChild.getRight() - right);
            if (mIndicatorWidth <= 0) {
                leftDistance *= mPositionOffset;
                rightDistance *= mPositionOffset;
                int nextMargin = measureText(nextChild, nextChild.getLeft() + (int) mTabPaddingLeft, nextChild.getRight() - (int) mTabPaddingRight);

                int nextMarginLeft = handlerPadding(true, nextMargin);
                int nextMarginRight = handlerPadding(false, nextMargin);

                marginLeft = (int) ((nextMarginLeft - marginLeft) * mPositionOffset + marginLeft);
                marginRight = (int) ((nextMarginRight - marginRight) * mPositionOffset + marginRight);
            } else if (!mIndicatorEqualTabText) {
                int tabInsert = getTabInsert(nextChild);
                leftDistance = (leftDistance + tabInsert) * mPositionOffset;
                rightDistance = (rightDistance - tabInsert) * mPositionOffset;
            }

            left += leftDistance;
            right += rightDistance;
        }
        mIndicatorRect.left = left + marginLeft;
        mIndicatorRect.top = bottom - (int) mIndicatorHeight - (int) mIndicatorBottomMargin;
        mIndicatorRect.right = right - marginRight;
        mIndicatorRect.bottom = bottom - (int) mIndicatorBottomMargin;
    }

    /**
     * 处理TabView的Padding问题。
     *
     * @param left   是否是左padding。
     * @param margin 该值是当下划线的宽度要跟文本的宽度一致的时候，TabView左右的剩余间距。
     * @return
     */
    private int handlerPadding(boolean left, int margin) {
        int nextMarginRight = 0;
        int nextMarginLeft = 0;
        if (!left) {
            nextMarginRight = Math.max(margin, (int) mTabPaddingRight);
            if (margin > (int) mTabPaddingRight) {
                nextMarginRight += mTabPaddingRight;
            }
        } else {
            nextMarginLeft = Math.max(margin, (int) mTabPaddingLeft);
            if (margin > (int) mTabPaddingLeft) {
                nextMarginLeft += mTabPaddingLeft;
            }
        }
        return left ? nextMarginLeft : nextMarginRight;

    }

    private int getTabInsert(View view) {
        return (view.getWidth() - (int) mIndicatorWidth - (int) mTabPaddingLeft - (int) mTabPaddingRight) / 2;
    }

    /**
     * 测量文本的宽度，包括其文本内部的DrawableLeft的图片。
     *
     * @param childAt
     * @param tabLeft
     * @param tabRight
     * @return
     */
    private int measureText(View childAt, int tabLeft, int tabRight) {
        //指示器要跟文本的宽度相等。
        if (mIndicatorEqualTabText) {
            TextView tabView;
            //childAt 可能是个ViewGroup，也可能是个TextView。
            if (childAt instanceof TextView) {
                tabView = (TextView) childAt;
            } else {
                tabView = childAt.findViewWithTag(TAB_TAG);
            }
            if (null != tabView) {
                mTextPaint.setTextSize(tabView.getTextSize());
                float tabTextWidth = mTextPaint.measureText(tabView.getText().toString().trim());
                int drawableWidth = getTextViewCompoundDrawables(tabView);
                tabTextWidth += drawableWidth;
                int tabWidth = tabRight - tabLeft;
                return (int) ((tabWidth - tabTextWidth) / 2);
            }
        }
        return 0;
    }

    /**
     * 如果TabView有DrawableLeft的图片的时候，也要加上图片的宽度。
     *
     * @param textView
     * @return
     */
    private int getTextViewCompoundDrawables(TextView textView) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        if (compoundDrawables.length > 0) {
            for (int i = 0; i < compoundDrawables.length; i++) {
                Drawable drawable = compoundDrawables[i];
                if (null != drawable) {
                    int width = drawable.getBounds().width();
                    if (width > 0) {
                        return width;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentScrollTab = position;
        this.mPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        selectedTab(position);
        mCurrentTab = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //拖动结束。
        if (state == ViewPager.SCROLL_STATE_IDLE) {

        }
    }

    private boolean haveInit() {
        return mHaveInit && null != mTabContainer;
    }

    private int dp2px(int px) {
        return (int) (getContext().getResources().getDisplayMetrics().density * px + 0.5f);
    }

    private int sp2px(int px) {
        return (int) (getContext().getResources().getDisplayMetrics().scaledDensity * px + 0.5f);
    }

    private int getColorResource(int color) {
        return getContext().getResources().getColor(color);
    }
}
