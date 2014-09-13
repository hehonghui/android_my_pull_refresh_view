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
import android.view.View;
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

    /*
     * 是否滑动到了顶端，如果返回true, 则表示到了顶端，用户继续下拉则触发下拉刷新
     * @see com.uit.pullrefresh.base.PullRefreshBase#isTop()
     */
    @Override
    protected boolean isTop() {
        View firstChild = mContentView.getChildAt(0);
        if (firstChild == null) {
            return true;
        }
        return mContentView.getFirstVisiblePosition() == 0
                && (firstChild.getTop() >= mContentView.getTop());
    }

    /*
     * 下拉到listview 最后一项时则返回true, 将出发自动加载
     * @see com.uit.pullrefresh.base.PullRefreshBase#isShowFooterView()
     */
    @Override
    protected boolean isShowFooterView() {
        if (mContentView == null || mContentView.getAdapter() == null) {
            return false;
        }

        return mContentView.getLastVisiblePosition() == mContentView.getAdapter().getCount() - 1;
    }

    /*
     * 初始化mContentView
     * @see com.uit.pullrefresh.base.PullRefreshBase#initContentView()
     */
    @Override
    protected void initContentView() {
        // 初始化mContentView
        mContentView = new ListView(getContext());
        // 设置OnScrollListener, 用以实现滑动到底部时的自动加载功能，如果不需要该功能可以不设置.
        mContentView.setOnScrollListener(this);
    }

}
