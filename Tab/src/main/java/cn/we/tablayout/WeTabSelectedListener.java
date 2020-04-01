package cn.we.tablayout;

import android.view.View;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/24
 */
public interface WeTabSelectedListener {

    void onTabSelected(View currentTab, int position);

    void onPreTabSelected(View preTab, int prePosition);
}
