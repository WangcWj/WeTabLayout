package cn.we.tablayout;

import android.graphics.drawable.Drawable;
import android.view.Gravity;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/5/11
 */
public class WeTabDrawable {

    private Drawable drawable;
    private int gravity = Gravity.RIGHT;
    private int drawablePadding = 0;
    private String tabName;

    public WeTabDrawable(Drawable drawable, String tabName) {
        this.drawable = drawable;
        this.tabName = tabName;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    /**
     * 该方法不要修改。
     *
     * @param gravity
     * @return
     */
    public Drawable getDrawableByGravity(int gravity) {
        if (getGravity() == gravity) {
            return drawable;
        }
        return null;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public int getDrawablePadding() {
        return drawablePadding;
    }

    public void setDrawablePadding(int drawablePadding) {
        this.drawablePadding = drawablePadding;
    }
}
