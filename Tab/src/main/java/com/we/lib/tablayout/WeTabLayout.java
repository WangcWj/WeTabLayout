package com.we.lib.tablayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created to :
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
    private List<String> mTitles;
    private boolean mHaveInit = false;
    private ViewPager mViewPager;

    private LinearLayout mTabContainer;
    private int mTabContainerGravity = Gravity.CENTER;
    private int mCurrentTab = 0;

    private int mTabCount;
    private int mTabLayout = -1;
    private int mTabTextSize = 14;
    private int mSelectedTabTextColor = Color.RED;
    private int mDefaultTabTextColor = Color.BLACK;
    private boolean mTabTextStyleBold = false;
    private boolean mTabFillContainer = true;

    private int mIndicatorHeight;
    private boolean mIndicatorEqualTabText = false;
    private int mIndicatorBottomMargin;
    private int mIndicatorColor = Color.RED;
    private Paint mIndicatorPaint;

    private IHandleTab mHandleTab;
    private GradientDrawable mIndicatorDrawable;
    private int mResId;
    private Rect mIndicatorRect;
    private Paint mTextPaint;

    private float mPositionOffset;

    public void setTabLayout(int mTabLayout) {
        this.mTabLayout = mTabLayout;
    }

    public void setHandleTab(@NonNull IHandleTab mHandleTab) {
        this.mHandleTab = mHandleTab;
    }

    public void setCurrentTab(int mCurrentTab) {
        this.mCurrentTab = mCurrentTab;
    }

    public void setTabFillContainer(boolean fill) {
        this.mTabFillContainer = fill;
    }

    public void setIndicatorColor(int mIndicatorColor) {
        this.mIndicatorColor = getColorResource(mIndicatorColor);
    }

    public void setSelectedTabTextColor(int mSelectedTabTextColor) {
        this.mSelectedTabTextColor = mSelectedTabTextColor;
    }

    public void setDefaultTabTextColor(int mDefaultTabTextColor) {
        this.mDefaultTabTextColor = mDefaultTabTextColor;
    }

    public void setIndicatorBottomMargin(int margin) {
        this.mIndicatorBottomMargin = dp2px(margin);
    }

    public void setIndicatorEqualTabText(boolean mIndicatorEqualTabText) {
        this.mIndicatorEqualTabText = mIndicatorEqualTabText;
    }

    private void initView(Context context, AttributeSet attrs) {
        initScrollView();
        mContext = context;
        mTabContainer = new LinearLayout(context);
        addView(mTabContainer);
        mIndicatorDrawable = new GradientDrawable();
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(Color.RED);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorRect = new Rect();
        mIndicatorHeight = dp2px(2);
        mHaveInit = true;
    }

    private void initScrollView() {
        //设置滚动视图是否可以伸缩其内容以填充视口
        setFillViewport(true);
        //重写onDraw方法,需要调用这个方法来清除flag
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void attachToViewPager(ViewPager viewPager, String[] titles) {
        if (!haveInit()) {
            return;
        }
        if (null == viewPager || null == viewPager.getAdapter()) {
            return;
        }
        if (null == titles || titles.length <= 0) {
            return;
        }
        this.mViewPager = viewPager;
        mTitles = new ArrayList<>();
        Collections.addAll(mTitles, titles);
        viewPager.removeOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        mTabContainer.removeAllViews();
        mTabCount = mTitles.size();
        createTab();
    }

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

    private void selectedTab(int selectedIndex) {
        if (null == mTabContainer || mTabContainer.getChildCount() == 0) {
            return;
        }
        int childCount = mTabContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mTabContainer.getChildAt(i);
            TextView selected;
            if (childAt instanceof TextView) {
                selected = (TextView) childAt;
            } else {
                selected = findTabTextView(childAt);
            }
            if (null != selected) {
                selected.setTextColor(i == selectedIndex ? mSelectedTabTextColor : mDefaultTabTextColor);
            }

        }
    }

    private int mLastScrollX = 0;

    private int getParentWidth() {
        int width = getWidth() + getPaddingLeft() + getPaddingRight();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof MarginLayoutParams) {
            MarginLayoutParams mp = (MarginLayoutParams) layoutParams;
            width += mp.leftMargin + mp.rightMargin;
        }
        return width / 2;
    }

    /**
     * HorizontalScrollView滚到当前tab,并且居中显示
     */
    private void scrollToCurrentTab() {
        if (mTabCount <= 0) {
            return;
        }
        View childAt = mTabContainer.getChildAt(mCurrentTab);
        if (null == childAt) {
            return;
        }
        int centerDistance = (getWidth() - childAt.getWidth()) / 2;
        int left = childAt.getLeft();
        Log.e("cc.wang", "WeTabLayout.scrollToCurrentTab.centerDistance  " + centerDistance + "    left   " + left);
        int newScrollX = 0;
        if ( mCurrentTab > 0) {
            newScrollX += left + (int) (childAt.getWidth() * mPositionOffset);
            newScrollX -= centerDistance;
        }
        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    private TextView findTabTextView(View parent) {
        return parent.findViewWithTag(TAB_TAG);
    }

    private void setStyle(TextView childView, View tabView, int index) {
        if (null == childView || null == tabView || null == mTabContainer) {
            return;
        }
        //如果布局有背景的时候，要清掉，不清掉的时候会遮挡住绘制的指示器。
        clearBackground(tabView);
        clearBackground(childView);

        childView.setText(mTitles.get(index));
        childView.setTextSize(mTabTextSize);
        childView.setTextColor(index == mCurrentTab ? mSelectedTabTextColor : mDefaultTabTextColor);
        childView.setGravity(Gravity.CENTER);
        childView.getPaint().setFakeBoldText(mTabTextStyleBold);

        LinearLayout.LayoutParams tabLayoutParams = getTabLayoutParams();
        mTabContainer.setGravity(mTabContainerGravity);
        mTabContainer.addView(tabView, index, tabLayoutParams);
    }

    private void clearBackground(View tabView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tabView.setBackground(null);
        }
    }

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
        mTabContainer.removeAllViews();
    }

    /**
     * 自定义布局的时候，
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

        mIndicatorDrawable.setColor(mIndicatorColor);
        mIndicatorDrawable.setBounds(mIndicatorRect);
        mIndicatorDrawable.draw(canvas);

    }

    private void computeIndicatorRect() {
        View childAt = mTabContainer.getChildAt(mCurrentTab);
        int childCount = mTabContainer.getChildCount();
        if (null == childAt) {
            return;
        }
        int left = childAt.getLeft();
        int right = childAt.getRight();
        int bottom = childAt.getBottom();
        //当前的Tab。
        int margin = measureText(childAt, left, right);
        //滑动中的。
        if (mCurrentTab < childCount - 1) {
            View nextChild = mTabContainer.getChildAt(mCurrentTab + 1);
            if (null != nextChild) {
                float distance = (nextChild.getLeft() - left) * mPositionOffset;
                int nextMargin = measureText(nextChild, nextChild.getLeft(), nextChild.getRight());
                margin = (int) ((nextMargin - margin) * mPositionOffset + margin);
                left += distance;
                right += distance;
            }
        }

        mIndicatorRect.left = left + margin;
        mIndicatorRect.top = bottom - mIndicatorHeight - mIndicatorBottomMargin;
        mIndicatorRect.right = right - margin;
        mIndicatorRect.bottom = bottom - mIndicatorBottomMargin;
    }

    private int measureText(View childAt, int parentLeft, int parentRight) {
        //指示器要跟文本的宽度相等。
        if (mIndicatorEqualTabText) {
            TextView tabView;
            if (childAt instanceof TextView) {
                tabView = (TextView) childAt;
            } else {
                tabView = childAt.findViewWithTag(TAB_TAG);
            }
            if (null != tabView) {
                mTextPaint.setTextSize(tabView.getTextSize());
                float tabTextWidth = mTextPaint.measureText(tabView.getText().toString().trim());
                int tabWidth = parentRight - parentLeft;
                return (int) ((tabWidth - tabTextWidth) / 2);
            }
        }
        return 0;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentTab = position;
        this.mPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        selectedTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private boolean haveInit() {
        return mHaveInit && null != mTabContainer;
    }

    private int dp2px(int px) {
        return (int) (getContext().getResources().getDisplayMetrics().density * px + 0.5f);
    }

    private int getColorResource(int color) {
        return getContext().getResources().getColor(color);
    }
}
