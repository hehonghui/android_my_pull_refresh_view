/**
 *
 *	created by Mr.Simple, Oct 2, 201410:21:12 AM.
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
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

public class RefreshSlideDeleteListView extends RefreshListView {

    /**
     * 
     */
    private int mXDown;
    /**
     * 
     */
    private int mYDown;
    /**
     * 
     */
    private View mItemView;
    /**
     * 删除区域的宽度, 这里假设宽度为200px吧
     */
    private int mDeleteViewWidth = 200;

    /**
     * 是否在滑动中
     */
    private boolean isSliding = false;

    /**
     * 是否已经显示
     */
    private boolean isDeleteViewShowing = false;
    /**
     * 
     */
    int mItemPosition = 0;

    /**
     * @param context
     */
    public RefreshSlideDeleteListView(Context context) {
        this(context, null, 0);
    }

    public RefreshSlideDeleteListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshSlideDeleteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * 在适当的时候拦截触摸事件，这里指的适当的时候是当mContentView滑动到顶部，并且是下拉时拦截触摸事件，否则不拦截，交给其child
     * view 来处理。
     * @see
     * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        final int action = MotionEventCompat.getActionMasked(ev);
        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Do not intercept touch event, let the child handle it
            return false;
        }

        Log.d(VIEW_LOG_TAG, "### is delete showing = " + isDeleteViewShowing);

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getRawY();
                mXDown = (int) ev.getRawX();
                mYDown = mLastY;
                // 判断是否有删除按钮显示,如果有则隐藏删除按钮
                if (isDeleteViewShowing && mItemPosition != AbsListView.INVALID_POSITION
                        && mItemPosition != getItemPosition(mXDown, mYDown)) {
                    slideItemView(0);
                    clearSlideState();
                    return false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // int yDistance = (int) ev.getRawY() - mYDown;
                mYOffset = (int) ev.getRawY() - mLastY;
                mDistanceX = (int) ev.getRawX() - mXDown;
                // 如果拉到了顶部, 并且是下拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if (isTop() && mYOffset > 0) {
                    return true;
                }
                // 滑动删除操作
                if (Math.abs(mDistanceX) > 10) {
                    return true;
                }
                break;

        }

        // Do not intercept touch event, let the child handle it
        return false;
    }

    /*
     * 在这里处理触摸事件以达到下拉刷新或者上拉自动加载的问题
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                int currentY = (int) event.getRawY();
                int currentX = (int) event.getRawX();
                mDistanceX = currentX - mXDown;
                int distanceY = (int) event.getRawY() - mYDown;
                Log.d(VIEW_LOG_TAG, "### distanceX = " + mDistanceX + ", dis y = " + distanceY
                        + ", is delete showing = " + isDeleteViewShowing);
                if (Math.abs(mDistanceX) > Math.abs(distanceY) * 2) {
                    if (mItemView == null) {
                        mItemView = getCurrentItemView(currentX, currentY);
                    }
                    slideItemView(mDistanceX);
                } else if (!isDeleteViewShowing && isTop() && mYOffset > 0 && !isSliding) {
                    //
                    mYOffset = currentY - mLastY;
                    if (mCurrentStatus != STATUS_LOADING) {
                        // 修改ScrollY达到拉伸header的效果
                        changeScrollY(mYOffset);
                    }

                    rotateHeaderArrow();
                    changeTips();
                    mLastY = currentY;
                }
                break;

            case MotionEvent.ACTION_UP:
                isSlideValid();
                // 下拉刷新的具体操作
                doRefresh();
                break;
            default:
                break;

        }

        return true;
    }

    int mDistanceX = 0;

    /**
     * 
     */
    private void slideItemView(int distanceX) {
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH
                || mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            return;
        }

        // 在没有delete view显示，且是用户右滑则忽略
        if (distanceX > 0 && !isDeleteViewShowing) {
            return;
        }

        Log.d(VIEW_LOG_TAG, "### slide item view , origin distance x = " + distanceX);
        if (distanceX <= 0) {
            // 小于0，代表用户左滑，这里将其转为正数，使得整个item view的视图向右滑动，即在x轴上要向右移动。
            distanceX = Math.abs(distanceX);
            distanceX = Math.min(distanceX, mDeleteViewWidth);
        } else {
            distanceX = Math.max(0, mDeleteViewWidth - distanceX);
        }

        Log.d(VIEW_LOG_TAG, "### slide item view , distance x = " + distanceX);
        Log.d(VIEW_LOG_TAG, "item view = " + mItemView);
        if (mItemView != null) {
            mItemView.scrollTo(distanceX, 0);
            mItemView.invalidate();
            isSliding = true;
        }

    }

    /**
     * 
     */
    private void clearSlideState() {
        mItemView = null;
        isDeleteViewShowing = false;
        isSliding = false;
        mDistanceX = 0;
        mItemPosition = AbsListView.INVALID_POSITION;
    }

    /**
     * 
     */
    private void isSlideValid() {
        if (mItemView != null) {
            if (mItemView.getScrollX() > mDeleteViewWidth / 2) {
                slideItemView(-mDeleteViewWidth);
                isDeleteViewShowing = true;
            } else {
                slideItemView(0);
                clearSlideState();
            }
        } else {
            clearSlideState();
        }

    }

    /**
     * @param position
     */
    public void onRemoveItem(int position) {
        slideItemView(0);
        clearSlideState();
    }

    /**
     * @param x
     * @param y
     * @return
     */
    private int getItemPosition(int x, int y) {
        int position = mContentView.pointToPosition(x, y);
        if (position != AbsListView.INVALID_POSITION) {

            Log.d(VIEW_LOG_TAG,
                    "### first = " + mContentView.getFirstVisiblePosition() + ", touch postion = "
                            + position + ", header count = " + mContentView.getHeaderViewsCount());

            mItemPosition = position - mContentView.getFirstVisiblePosition() - 1;
            mItemPosition = Math.max(0, mItemPosition);
        }

        return mItemPosition;
    }

    /**
     * @param event
     * @return
     */
    private View getCurrentItemView(int x, int y) {
        //
        getItemPosition(x, y);

        Log.d(VIEW_LOG_TAG, "### child count = " + mContentView.getChildCount());
        //
        if (mItemPosition != AbsListView.INVALID_POSITION) {
            // get child view
            return mContentView.getChildAt(mItemPosition);
        }

        return null;
    }

}
