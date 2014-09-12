/**
 *
 *	created by Mr.Simple, Sep 10, 20146:16:09 PM.
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

package com.uit.pullrefresh.base;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.uit.pullrefresh.R;
import com.uit.pullrefresh.listener.OnLoadMoreListener;
import com.uit.pullrefresh.listener.OnPullRefreshListener;

/**
 * @author mrsimple
 * @param <T>
 */
public abstract class PullRefreshBase<T extends View> extends LinearLayout {

    /**
     * 
     */
    protected T mContentView;

    /**
     * 
     */
    protected View mHeaderView;

    /**
     * 
     */
    protected View mFooterView;
    /**
     * 
     */
    protected OnPullRefreshListener mPullRefreshListener;
    /**
     * 
     */
    protected OnLoadMoreListener mLoadMoreListener;

    /**
     * 
     */
    protected LayoutInflater mInflater;

    /**
     * 
     */
    protected int mHeaderViewHeight = 0;

    /**
     * 空闲状态
     */
    public static final int STATUS_IDLE = 1;

    /**
     * 下拉或者上拉状态
     */
    public static final int STATUS_PULLING = 2;

    /**
     * 下拉或者上拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 3;

    /**
     * 下拉或者上拉状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 4;
    /**
     * 刷新中
     */
    public static final int STATUS_REFRESHING = 5;
    /**
     * 当前状态
     */
    protected int mCurrentStatus = STATUS_IDLE;
    /**
     * 
     */
    protected MarginLayoutParams mHeaderLayoutParams;
    /**
     * 触摸事件按下的y坐标
     */
    protected int mYDown = 0;

    /**
     * @param context
     */
    public PullRefreshBase(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public PullRefreshBase(Context context, AttributeSet attrs) {

        super(context, attrs);

        mInflater = LayoutInflater.from(context);
        setOrientation(LinearLayout.VERTICAL);
        initLayout(context);
    }

    /**
     * 
     */
    protected void initLayout(Context context) {
        //
        mHeaderView = mInflater.inflate(R.layout.umeng_comm_pull_to_refresh_header, null);
        // measureView(mHeaderView);

        // add header view to parent
        this.addView(mHeaderView, 0);

        initContentView();
        setContentView(mContentView);

        //
        ProgressBar footer = new ProgressBar(context);
        footer.setIndeterminate(true);
        mFooterView = footer;
        // mFooterView.setVisibility(View.GONE);
        this.addView(mFooterView, 2);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            mHeaderViewHeight = mHeaderView.getHeight();
            // hide header view
            mHeaderLayoutParams = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderLayoutParams.topMargin = -mHeaderViewHeight;
        }
    }

    /**
     * 
     */
    protected void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    protected abstract void initContentView();

    /**
     * @param view
     */
    public void setContentView(T view) {
        mContentView = view;
        this.addView(mContentView, 1);
    }

    /**
     * @return
     */
    public T getContentView() {
        return mContentView;
    }

    /**
     * @param view
     */
    public void setHeaderView(View view) {
        mHeaderView = view;
    }

    /**
     * @param view
     */
    public void setHeaderView(int layout) {
        mHeaderView = mInflater.inflate(layout, null);
    }

    /**
     * @param view
     */
    public void setFooterView(View view) {
        mFooterView = view;
    }

    /**
     * @param view
     */
    public void setFooterView(int layout) {
        mFooterView = mInflater.inflate(layout, null);
    }

    protected int mYDistance = 0;
    protected int mTouchSlop = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mYDown = (int) ev.getRawY();
        }
        // 是否到了最上面的一个view, 即判断是否可以下拉刷新, 拦截事件
        if (isTop()) {
            Log.d(VIEW_LOG_TAG, "### 拦截视图 ");
            return true;
        }

        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH || mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            return true;
        }

        // return super.onInterceptTouchEvent(ev);
        return true;
    }

    /*
     * 在这里处理触摸事件以达到下拉刷新或者上拉自动加载的问题
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d(VIEW_LOG_TAG, "#### onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                Log.d(VIEW_LOG_TAG, "#### ACTION_DOWN");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(VIEW_LOG_TAG, "#### ACTION_MOVE");
                int currentY = (int) event.getRawY();
                mYDistance = currentY - mYDown;

                Log.d(VIEW_LOG_TAG, "### touch slop = " + mTouchSlop + ", distance = " + mYDistance
                        + ", status = " + mCurrentStatus);

                if (mYDistance <= 0 || mYDistance < mTouchSlop) {
                    return false;
                }

                if (mYDistance >= mTouchSlop && mCurrentStatus != STATUS_REFRESHING) {
                    if (mHeaderLayoutParams.topMargin > 0) {
                        mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
                    } else {
                        mCurrentStatus = STATUS_PULL_TO_REFRESH;
                    }
                    adjustViewPadding(mHeaderView, mYDistance / 2 - mHeaderViewHeight);
                }
                break;

            case MotionEvent.ACTION_UP:
                // 下拉刷新的具体操作
                doRefresh();
                break;
            default:
                break;

        }

        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH
                || mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 
     */
    private final void doRefresh() {
        if (mHeaderLayoutParams.topMargin > mHeaderViewHeight / 2
                && mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            mCurrentStatus = STATUS_REFRESHING;
            mPullRefreshListener.onRefresh();
        } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            // 隐藏header view

        }
    }

    /**
     * @param listener
     */
    public void setOnRefreshListener(OnPullRefreshListener listener) {
        mPullRefreshListener = listener;
    }

    /**
     * 
     */
    public void refreshComplete() {
        mCurrentStatus = STATUS_IDLE;
        resetHeaderView();
    }

    /**
     * 
     */
    protected void resetHeaderView() {
        int curHeight = mHeaderLayoutParams.topMargin;
        mHeaderLayoutParams.bottomMargin = -curHeight - mHeaderViewHeight;
        mHeaderView.setLayoutParams(mHeaderLayoutParams);
    }

    /**
     * 
     */
    protected void rotateHeaderArrow() {

    }

    /**
     * 
     */
    protected void hideFooterView() {

    }

    /**
     * 
     */
    public void loadMoreComplete() {

    }

    /**
     * 调整Padding以实现下拉或者上拉的效果
     */
    protected void adjustViewPadding(View view, int distance) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.bottomMargin = distance;
        view.setLayoutParams(marginLayoutParams);
    }

    /**
     * 是否可以下拉刷新了
     * 
     * @return
     */
    protected abstract boolean isTop();

    /**
     * 下拉到底部时加载更多
     * 
     * @return
     */
    protected boolean isBottom() {
        return false;
    }

    /**
     * @author mrsimple
     */
    class HeaderViewHideTask extends AsyncTask<Integer, Void, Void> {

        MarginLayoutParams layoutParams = (MarginLayoutParams) mHeaderView.getLayoutParams();

        @Override
        protected Void doInBackground(Integer... params) {
            int speed = params[0];

            try {
                do {
                    if (layoutParams.topMargin <= 0) {
                        break;
                    }

                    layoutParams.topMargin += speed;
                    publishProgress();
                    Thread.sleep(20);
                } while (true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mHeaderView.setLayoutParams(layoutParams);
        }

    }

}
