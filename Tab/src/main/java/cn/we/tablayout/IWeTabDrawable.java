package cn.we.tablayout;

import android.graphics.drawable.Drawable;


/**
 * Created to :Tab设置上下左右设置小icon。
 *
 * @author cc.wang
 * @date 2020/5/11
 */
public interface IWeTabDrawable {

    /**
     * 通过gravity返回符合该位置处的Drawable对象。
     *
     * @param gravity
     * @return
     */
    Drawable getDrawableByGravity(int gravity) ;

    /**
     * 获取目标Tab的名字。
     *
     * @return
     */
    String getTargetTabName();

}
