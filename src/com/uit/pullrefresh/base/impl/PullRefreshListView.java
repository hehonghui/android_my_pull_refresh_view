/**
 *
 *	created by Mr.Simple, Sep 10, 20146:29:46 PM.
 *	Copyright (c) 2014, hehonghui@umeng.com All Rights Reserved.
 *
 *                #####################################################
 *                #                                                   #
 *                #                       _oo0oo_                     #   
 *                #                      o8888888o                    #
 *                #                      88" . "88                    #
 *                #                      (| -_- |)                    #
 *                #                      0\  =  /0                    #   
 *                #                    ___/`---'\___                  #
 *                #                  .' \\|     |# '.                 #
 *                #                 / \\|||  :  |||# \                #
 *                #                / _||||| -:- |||||- \              #
 *                #               |   | \\\  -  #/ |   |              #
 *                #               | \_|  ''\---/''  |_/ |             #
 *                #               \  .-\__  '-'  ___/-. /             #
 *                #             ___'. .'  /--.--\  `. .'___           #
 *                #          ."" '<  `.___\_<|>_/___.' >' "".         #
 *                #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 *                #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 *                #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 *                #                       `=---='                     #
 *                #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 *                #                                                   #
 *                #               佛祖保佑         永无BUG              #
 *                #                                                   #
 *                #####################################################
 */

package com.uit.pullrefresh.base.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.uit.pullrefresh.base.PullRefreshBase;

/**
 * @author mrsimple
 */
public class PullRefreshListView extends PullRefreshBase<ListView> {

    /**
     * @param context
     */
    public PullRefreshListView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean isTop() {
        View firstChild = mContentView.getChildAt(0);
        if (firstChild == null) {
            return true;
        }
        Log.d(VIEW_LOG_TAG, "### isTop, mContentView.getTop() = " + mContentView.getTop()
                + ", first child : "
                + firstChild.getTop());
        return mContentView.getFirstVisiblePosition() == 0
                && (firstChild.getTop() >= mContentView.getTop());
    }

    @Override
    protected boolean isBottom() {

        if (mContentView == null || mContentView.getAdapter() == null) {
            return false;
        }
        // 总数
        int count = mContentView.getAdapter().getCount();
        // 显示的数量
        int activeCount = mContentView.getChildCount();
        //
        View lastChild = mContentView.getChildAt(activeCount - 1);
        if (lastChild == null) {
            return true;
        }

        Log.d(VIEW_LOG_TAG,
                "### isBottom, mContentView.getBottom() = " + mContentView.getBottom()
                        + ", last child getBottom: "
                        + lastChild.getBottom() + ", last postion : "
                        + mContentView.getLastVisiblePosition() + ", child count : " + count
                        + ", active count : " + activeCount);
        return mContentView.getLastVisiblePosition() == count - 1
                && (lastChild.getBottom() <= mContentView.getBottom());
    }

    @Override
    protected boolean isShowFooterView() {
        if (mContentView == null || mContentView.getAdapter() == null) {
            return false;
        }

        return mContentView.getLastVisiblePosition() == mContentView.getAdapter().getCount() - 1;
    }

    @Override
    protected void initContentView() {
        mContentView = new ListView(getContext());
        //
        mContentView.setOnScrollListener(this);
        //
        int count = mContentView.getHeaderViewsCount();
        for (int i = 0; i < count; i++) {
            mContentView.removeHeaderView(mContentView.getChildAt(i));
        }
        ViewGroup.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(layoutParams);
    }

}
