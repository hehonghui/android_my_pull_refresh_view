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
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uit.pullrefresh.R;
import com.uit.pullrefresh.listener.OnLoadMoreListener;
import com.uit.pullrefresh.listener.OnPullRefreshListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mrsimple
 * @param <T>
 */
public abstract class PullRefreshBase<T extends View> extends LinearLayout implements
        OnScrollListener {

    /**
     * 内容视图, 比如ListView, GridView等
     */
    protected T mContentView;

    /**
     * 下拉头视图,因为要修改其padding,所以类型为ViewGroup类型
     */
    protected ViewGroup mHeaderView;

    /**
     * footer视图,因为要修改其padding,所以类型为ViewGroup类型
     */
    protected ViewGroup mFooterView;
    /**
     * 下拉刷新监听器
     */
    protected OnPullRefreshListener mPullRefreshListener;
    /**
     * 滑动到底部则自动加载的监听器
     */
    protected OnLoadMoreListener mLoadMoreListener;

    /**
     * LayoutInflater
     */
    protected LayoutInflater mInflater;

    /**
     * Header 的高度
     */
    protected int mHeaderViewHeight;

    /**
     * 空闲状态
     */
    public static final int STATUS_IDLE = 0;

    /**
     * 下拉或者上拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 1;

    /**
     * 下拉或者上拉状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 2;
    /**
     * 刷新中
     */
    public static final int STATUS_REFRESHING = 3;

    /**
     * LOADING中
     */
    public static final int STATUS_LOADING = 4;

    /**
     * 当前状态
     */
    protected int mCurrentStatus = STATUS_IDLE;

    /**
     * Y轴上滑动的距离
     */
    protected int mYDistance = 0;
    /**
     * 滑动的距离阀值,超过这个阀值则认为是有效滑动
     */
    protected int mTouchSlop = 0;
    /**
     * 触摸事件按下的y坐标
     */
    protected int mYDown = 0;

    /**
     * header view里面的进度条
     */
    protected ProgressBar mHeaderProgressBar;
    /**
     * 下拉头的箭头
     */
    protected ImageView mArrowImageView;
    /**
     * 箭头图标是否是向上的状态
     */
    protected boolean isArrowUp = false;
    /**
     * 下拉刷新的文字TextView
     */
    protected TextView mTipsTextView;
    /**
     * 更新时间的文字TextView
     */
    protected TextView mTimeTextView;
    /**
     * footer view's progress bar
     */
    protected ProgressBar mFooterProgressBar;
    /**
     * footer view's text
     */
    protected TextView mFooterTextView;
    /**
     * footer view's height
     */
    protected int mFooterHeight;
    /**
     * 屏幕高度
     */
    protected int mScrHeight = 0;

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
     * 初始化整体布局, header view放在第一个，然后是content view 和 footer view .其中content view的
     * 宽度和高度都为match parent .
     */
    protected final void initLayout(Context context) {

        // 初始化header view
        initHeaderView();

        // 初始化 content view
        initContentView();
        setContentView(mContentView);

        // 初始化 footer
        initFooterView();

        //
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //
        mScrHeight = context.getResources().getDisplayMetrics().heightPixels;

    }

    /**
     * 初始化header view
     */
    protected void initHeaderView() {
        //
        mHeaderView = (ViewGroup) mInflater.inflate(R.layout.pull_to_refresh_header,
                null);
        mHeaderView.setBackgroundColor(Color.RED);

        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
        mArrowImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_arrow_image);
        mTipsTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mTimeTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        // add header view to parent
        this.addView(mHeaderView, 0);

    }

    /**
     * 初始化footer view
     */
    protected void initFooterView() {

        mFooterView = (ViewGroup) mInflater.inflate(R.layout.pull_to_refresh_footer, null);

        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pull_to_loading_progress);
        mFooterTextView = (TextView) mFooterView.findViewById(R.id.pull_to_loading_text);
        this.addView(mFooterView, 2);
    }

    /*
     * 获取header view, footer view的高度
     * @see android.widget.LinearLayout#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            mHeaderViewHeight = mHeaderView.getHeight();
            // padding
            adjustHeaderPadding(-mHeaderViewHeight);

            mFooterHeight = mFooterView.getHeight();
            adjustFooterPadding(-mFooterHeight);
        }
    }

    /**
     * 子类必须实现这个方法，并且在该方法中初始化mContentView字段，即你要显示的主视图.
     * 例如PullRefreshListView的mContentView就是ListView
     */
    protected abstract void initContentView();

    /**
     * @param view
     */
    public void setContentView(T view) {
        mContentView = view;
        LinearLayout.LayoutParams lvLayoutParams = (LinearLayout.LayoutParams) mContentView
                .getLayoutParams();
        if (lvLayoutParams == null) {
            lvLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        lvLayoutParams.bottomMargin = 0;
        lvLayoutParams.weight = 1.0f;
        mContentView.setLayoutParams(lvLayoutParams);
        this.addView(mContentView, 1);
    }

    /**
     * @return
     */
    public T getContentView() {
        return mContentView;
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

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                mYDown = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // int yDistance = (int) ev.getRawY() - mYDown;
                mYDistance = (int) ev.getRawY() - mYDown;
                showStatus(mCurrentStatus);
                Log.d(VIEW_LOG_TAG, "%%% isBottom : " + isBottom() + ", isTop : " + isTop()
                        + ", mYDistance : " + mYDistance);
                // 如果拉到了顶部, 并且是下拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if ((isTop() && mYDistance > 0)
                        || (mYDistance > 0 && mCurrentStatus == STATUS_REFRESHING)) {
                    Log.d(VIEW_LOG_TAG, "--------- mYDistance : " + mYDistance);
                    return true;
                }
                break;

        }

        // Do not intercept touch event, let the child handle it
        return false;
    }

    /**
     * @param status
     */
    private void showStatus(int status) {
        String statusString = "";
        if (status == STATUS_IDLE) {
            statusString = "idle";
        } else if (status == STATUS_PULL_TO_REFRESH) {
            statusString = "pull to refresh";
        } else if (status == STATUS_RELEASE_TO_REFRESH) {
            statusString = "release to refresh";
        }
        else if (status == STATUS_REFRESHING) {
            statusString = "refreshing";
        }
        Log.d(VIEW_LOG_TAG, "### status = " + statusString);
    }

    /*
     * 在这里处理触摸事件以达到下拉刷新或者上拉自动加载的问题
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d(VIEW_LOG_TAG, "@@@ onTouchEvent : action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                Log.d(VIEW_LOG_TAG, "#### ACTION_DOWN");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(VIEW_LOG_TAG, "#### ACTION_MOVE");
                int currentY = (int) event.getRawY();
                mYDistance = currentY - mYDown;

                Log.d(VIEW_LOG_TAG, "### touch slop = " + mTouchSlop + ", distance = " + mYDistance);
                showStatus(mCurrentStatus);
                // 高度大于header view的高度才可以刷新
                if (mYDistance >= mTouchSlop) {
                    if (mCurrentStatus != STATUS_REFRESHING) {
                        //
                        if (mHeaderView.getPaddingTop() > mHeaderViewHeight * 0.7f) {
                            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
                            mTipsTextView.setText(R.string.pull_to_refresh_release_label);
                        } else {
                            mCurrentStatus = STATUS_PULL_TO_REFRESH;
                            mTipsTextView.setText(R.string.pull_to_refresh_pull_label);
                        }
                    }

                    rotateHeaderArrow();
                    int scaleHeight = (int) (mYDistance * 0.8f);
                    // 小于屏幕高度4分之一时才下拉
                    if (scaleHeight <= mScrHeight / 4) {
                        adjustHeaderPadding(scaleHeight);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                // 下拉刷新的具体操作
                doRefresh();
                break;
            default:
                break;

        }

        Log.d(VIEW_LOG_TAG, "### before : super.onTouchEvent ");
        return true;
    }

    /**
     * 执行刷新操作
     */
    private final void doRefresh() {
        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            //
            mCurrentStatus = STATUS_REFRESHING;
            mArrowImageView.clearAnimation();
            //
            mArrowImageView.setVisibility(View.GONE);

            mHeaderProgressBar.setVisibility(View.VISIBLE);

            mTimeTextView.setText(R.string.pull_to_refresh_update_time_label);
            SimpleDateFormat sdf = new SimpleDateFormat();
            mTimeTextView.append(sdf.format(new Date()));
            //
            mTipsTextView.setText(R.string.pull_to_refresh_refreshing_label);

            // 执行回调
            if (mPullRefreshListener != null) {
                mPullRefreshListener.onRefresh();
            }
            // 使headview 正常显示, 直到调用了refreshComplete后再隐藏
            new HeaderViewHideTask().execute(0);

        } else {
            // 隐藏header view
            adjustHeaderPadding(-mHeaderViewHeight);
        }
    }

    /**
     * 下拉到底部时加载更多
     */
    private void loadmore() {
        if (isShowFooterView() && mLoadMoreListener != null) {
            mFooterTextView.setText(R.string.pull_to_refresh_refreshing_label);
            mFooterProgressBar.setVisibility(View.VISIBLE);
            adjustFooterPadding(0);
            mCurrentStatus = STATUS_LOADING;
            mLoadMoreListener.onLoadMore();
        }
    }

    /**
     * @param listener
     */
    public void setOnRefreshListener(OnPullRefreshListener listener) {
        mPullRefreshListener = listener;
    }

    /**
     * @param listener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    /**
     * 刷新结束，在调用完onRefresh后调用，否则header view会一直显示.
     */
    public void refreshComplete() {
        mCurrentStatus = STATUS_IDLE;
        mHeaderProgressBar.setVisibility(View.GONE);
        mArrowImageView.setVisibility(View.GONE);
        hideHeaderView();
    }

    /**
     * 隐藏header view
     */
    protected void hideHeaderView() {
        new HeaderViewHideTask().execute(-mHeaderViewHeight);
    }

    /**
     * 旋转箭头图标
     */
    protected void rotateHeaderArrow() {

        if (mCurrentStatus == STATUS_REFRESHING) {
            return;
        } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH && !isArrowUp) {
            return;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH && isArrowUp) {
            return;
        }

        mArrowImageView.setVisibility(View.VISIBLE);
        Log.d(VIEW_LOG_TAG, "------ rotateHeaderArrow");
        float pivotX = mArrowImageView.getWidth() / 2f;
        float pivotY = mArrowImageView.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }

        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        mArrowImageView.startAnimation(animation);

        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            isArrowUp = true;
        } else {
            isArrowUp = false;
        }
    }

    /**
     * 隐藏footer view
     */
    protected void hideFooterView() {
        adjustFooterPadding(-mFooterHeight);
    }

    /**
     * 上拉加载结束
     */
    public void loadMoreComplete() {
        mCurrentStatus = STATUS_IDLE;
        mFooterTextView.setText(R.string.pull_to_refresh_load_label);
        mFooterProgressBar.setVisibility(View.GONE);
        // adjustFooterPadding(-mFooterHeight);
        // hideFooterView();
        new FooterViewTask().execute(-mFooterHeight);
    }

    /**
     * 调整header view的bottom padding
     * 
     * @param bottomPadding
     */
    private void adjustFooterPadding(int bottomPadding) {
        mFooterView.setPadding(mFooterView.getPaddingLeft(), 0,
                mFooterView.getPaddingRight(), bottomPadding);
    }

    /**
     * 调整header view的top padding
     * 
     * @param topPadding
     */
    private void adjustHeaderPadding(int topPadding) {
        mHeaderView.setPadding(mHeaderView.getPaddingLeft(), topPadding,
                mHeaderView.getPaddingRight(), 0);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /*
     * 滚动事件，实现滑动到底部时上拉加载更多
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.
     * AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

        Log.d(VIEW_LOG_TAG, "&&& mYDistance = " + mYDistance);
        if (mFooterView == null || mYDistance >= 0 || mCurrentStatus == STATUS_LOADING
                || mCurrentStatus == STATUS_REFRESHING) {
            return;
        }

        loadmore();
    }

    /**
     * 是否可以下拉刷新了, 即是否拉到了头部
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
     * 是否到了显示footer view的时刻，该方法在onScroll中调用。在这个类中实现了mScroll方法，
     * 在设置mContentView时会将this设置给mContentView,以此监听mContentView的滑动事件.
     * 因此如果需要支持上拉加载更多则mContentView必须支持setOnScrollListener方法
     * ,并且在初始化mContentView时调用该方法进行注册.
     * 
     * @return
     */
    protected boolean isShowFooterView() {
        return false;
    }

    /**
     * 隐藏header view的异步任务, 实现平滑隐藏
     * 
     * @author mrsimple
     */
    class HeaderViewHideTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int totalHeight = mHeaderView.getPaddingTop();
            int targetPadding = params[0];
            int step = 1;
            Log.d(VIEW_LOG_TAG, "%%% new : totalHeight =" + totalHeight + ", targetPadding = "
                    + targetPadding);
            int mode = totalHeight % step;

            try {
                do {
                    if (mHeaderView.getPaddingTop() <= targetPadding) {
                        break;
                    }

                    if (totalHeight - mode == targetPadding) {
                        if (mode != 0) {
                            totalHeight -= mode;
                        } else {
                            totalHeight = targetPadding;
                        }
                    } else {
                        totalHeight -= step;
                    }
                    publishProgress(totalHeight);
                    Log.d(VIEW_LOG_TAG, "%%% totalHeight = " + totalHeight + ", mode = " + mode
                            + ", target = "
                            + targetPadding);
                    Thread.sleep(1);
                } while (true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            adjustHeaderPadding(values[0]);
        }

    } // end of HeaderViewHideTask

    /**
     * 隐藏header view的异步任务, 实现平滑隐藏
     * 
     * @author mrsimple
     */
    class FooterViewTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            // 0
            int totalHeight = mFooterView.getPaddingBottom();
            // -footer height
            int targetPadding = params[0];
            int step = 1;
            Log.d(VIEW_LOG_TAG, "%%% new : totalHeight =" + totalHeight + ", targetPadding = "
                    + targetPadding);
            try {
                do {

                    if (totalHeight == targetPadding || totalHeight > 0
                            || totalHeight < -mFooterHeight) {
                        break;
                    }

                    if (targetPadding == 0) {
                        totalHeight += step;
                    } else if (targetPadding < 0) {
                        totalHeight -= step;
                    }
                    publishProgress(totalHeight);

                    Log.d(VIEW_LOG_TAG, "%%% totalHeight = " + totalHeight
                            + ", target = "
                            + targetPadding);
                    Thread.sleep(1);
                } while (true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            adjustFooterPadding(values[0]);
        }

    } // end of HeaderViewHideTask

}
