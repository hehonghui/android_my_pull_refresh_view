/**
 *
 *	created by Mr.Simple, Sep 13, 20141:34:49 PM.
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
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.uit.pullrefresh.base.PullRefreshBase;

/**
 * @author mrsimple
 */
public class PullRefreshTextView extends PullRefreshBase<TextView> {

    public PullRefreshTextView(Context context) {
        super(context);
    }

    public PullRefreshTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initContentView() {
        // 初始化mContentView
        mContentView = new TextView(getContext());
        mContentView.setTextSize(20f);
        mContentView.setGravity(Gravity.CENTER);
        mContentView.setBackgroundColor(Color.CYAN);
    }

    /*
     * 是否滑动到了顶端，如果返回true, 则表示到了顶端，用户继续下拉则触发下拉刷新.由于TextView默认没有滑动，因此直接返回true.
     * @see com.uit.pullrefresh.base.PullRefreshBase#isTop()
     */
    @Override
    protected boolean isTop() {
        return true;
    }
}
