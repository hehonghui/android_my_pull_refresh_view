/**
 *
 *	created by Mr.Simple, Oct 9, 20142:54:51 PM.
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

package com.uit.pullrefresh.swipe;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;

import com.uit.pullrefresh.R;

/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能. 注意 :
 * 在下拉刷新完成时需要调用RefreshLayout的setRefreshing(false)方法来停止刷新过程；
 * 在上拉加载更多完成时需要调用setLoading(false)来标识加载完成。
 * 
 * @author mrsimple
 */
public abstract class RefreshLayout<T extends AbsListView> extends SwipeRefreshLayout implements
        OnScrollListener {

    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * listview实例
     */
    // private ListView mListView;

    /**
     * 
     */
    protected T mAbsListView;

    /**
     * ListView滚动监听器,用于外部
     */
    private OnScrollListener mListViewOnScrollListener;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * 按下时的y坐标
     */
    protected int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    protected int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    protected boolean isLoading = false;

    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化ListView对象
        if (mAbsListView == null) {
            getRefreshView();
            // 设置颜色
            this.setColorScheme(R.color.umeng_comm_lv_header_color1,
                    R.color.umeng_comm_lv_header_color2, R.color.umeng_comm_lv_header_color3,
                    R.color.umeng_comm_lv_header_color4);
        }
    }

    /**
     * 获取ListView对象
     */
    @SuppressWarnings("unchecked")
    protected void getRefreshView() {

        Log.d(VIEW_LOG_TAG, "### 构造调用");
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            // if (childView instanceof ListView) {
            // mListView = (ListView) childView;
            // // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
            // mListView.setOnScrollListener(this);
            // Log.d(VIEW_LOG_TAG, "### 找到listview");
            // }

            if (childView instanceof AbsListView) {
                mAbsListView = (T) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mAbsListView.setOnScrollListener(this);
                Log.d(VIEW_LOG_TAG, "### 找到listview = " + mAbsListView);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    public void setAdapter(ListAdapter adapter) {
        mAbsListView.setAdapter(adapter);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     * 
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (mAbsListView != null && mAbsListView.getAdapter() != null) {
            return mAbsListView.getLastVisiblePosition() == (mAbsListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 是否是上拉操作
     * 
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            //
            mOnLoadListener.onLoad();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    /**
     * 使外部可以监听到listview的滚动
     * 
     * @param listener
     */
    public void addOnScrollListener(OnScrollListener listener) {
        mListViewOnScrollListener = listener;
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d(VIEW_LOG_TAG, "@@@@ state = " + scrollState);

        // 回调给外部的监听器
        if (mListViewOnScrollListener != null) {
            mListViewOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

        // 回调给外部的监听器
        if (mListViewOnScrollListener != null) {
            mListViewOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }

        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    /**
     * 加载更多的监听器
     * 
     * @author mrsimple
     */
    public static interface OnLoadListener {
        public void onLoad();
    }
}
