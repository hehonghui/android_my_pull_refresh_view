/**
 *
 *	created by Mr.Simple, Sep 30, 201411:38:09 PM.
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
import android.widget.TextView;

import com.uit.pullrefresh.scroller.RefreshLayoutBase;

public class RefreshTextView extends RefreshLayoutBase<TextView> {

    public RefreshTextView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setupContentView(Context context) {
        mContentView = new TextView(context);
        // 开启可点击,使得onTouchEvent在处理触摸事件时返回true
        mContentView.setClickable(true);
    }

    @Override
    protected boolean isTop() {
        return true;
    }

    @Override
    protected boolean isBottom() {
        return false;
    }

}
