/**
 *
 *	created by Mr.Simple, Sep 30, 201411:13:20 PM.
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

package com.uit.pullrefresh.scroller.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.uit.pullrefresh.scroller.RefreshLayoutBase;

/**
 * @author mrsimple
 */
public class RefreshListView extends RefreshLayoutBase<ListView> {

    /**
     * @param context
     */
    public RefreshListView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setupContentView(Context context) {
        mContentView = new ListView(context);
        // 设置滚动监听器
        mContentView.setOnScrollListener(this);

    }

    @Override
    protected boolean isTop() {

        // Log.d(VIEW_LOG_TAG,
        // "### first pos = " + mContentView.getFirstVisiblePosition()
        // + ", getScrollY= " + getScrollY());
        return mContentView.getFirstVisiblePosition() == 0
                && getScrollY() <= mHeaderView.getMeasuredHeight();
    }

    @Override
    protected boolean isBottom() {
        // Log.d(VIEW_LOG_TAG, "### last position = " +
        // contentView.getLastVisiblePosition()
        // + ", count = " + contentView.getAdapter().getCount());
        return mContentView != null && mContentView.getAdapter() != null
                && mContentView.getLastVisiblePosition() ==
                mContentView.getAdapter().getCount() - 1;
    }
}
