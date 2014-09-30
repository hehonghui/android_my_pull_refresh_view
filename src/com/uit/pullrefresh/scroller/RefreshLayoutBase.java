/**
 *
 *	created by Mr.Simple, Sep 30, 20142:48:17 PM.
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

package com.uit.pullrefresh.scroller;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.uit.pullrefresh.R;
import com.uit.pullrefresh.listener.OnLoadListener;
import com.uit.pullrefresh.listener.OnPullRefreshListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mrsimple
 */
public class RefreshLayoutBase extends ViewGroup implements OnScrollListener {

    /**
     * 
     */
    Scroller mScroller;

    /**
     * 
     */
    View mHeaderView;

    /**
     * 
     */
    TextView mFooterView;

    /**
     * 
     */
    private int mYOffset;

    /**
     * 
     */
    ListView mContentView;

    /**
     * 
     */
    int mInitScrollY = 0;
    /**
     * 
     */
    int mLastY = 0;

    /**
     * 空闲状态
     */
    public static final int STATUS_IDLE = 0;

    /**
     * 下拉或者上拉状态, 还没有到达可刷新的状态
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
     * 
     */
    OnPullRefreshListener mOnPullRefreshListener;

    /**
     * 
     */
    private ImageView mArrowImageView;

    private boolean isArrowUp;

    private TextView mTipsTextView;

    private TextView mTimeTextView;

    private ProgressBar mProgressBar;
    /**
     * 
     */
    private int mScreenHeight;
    /**
     * 
     */
    private int mHeaderHeight;
    /**
     * 
     */
    private OnLoadListener mLoadListener;

    /**
     * @param context
     */
    public RefreshLayoutBase(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshLayoutBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public RefreshLayoutBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        mScroller = new Scroller(context);

        //
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;

        mHeaderHeight = mScreenHeight / 4;

        //
        initLayout(context);
    }

    /**
     * @param context
     */
    private final void initLayout(Context context) {

        mHeaderView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this,
                false);
        mHeaderView
                .setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                        mHeaderHeight));
        mHeaderView.setBackgroundColor(Color.RED);
        mHeaderView.setPadding(0, mHeaderHeight - 100, 0, 0);
        addView(mHeaderView);

        // HEADER VIEWS
        mArrowImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_arrow_image);
        mTipsTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mTimeTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);

        //
        mContentView = new ListView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        String[] dataStrings = new String[20];
        for (int i = 0; i < dataStrings.length; i++) {
            dataStrings[i] = "item - " + i;
        }
        //
        mContentView.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, dataStrings));
        mContentView.setOnScrollListener(this);
        //
        addView(mContentView, params);

        //
        mFooterView = new TextView(context);
        mFooterView.setText("footer");
        mFooterView.setBackgroundColor(Color.CYAN);
        mFooterView.setTextSize(30f);
        mFooterView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, 80));
        mFooterView.setGravity(Gravity.CENTER);
        addView(mFooterView);

    }

    /**
     * 
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
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
                mLastY = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // int yDistance = (int) ev.getRawY() - mYDown;
                mYOffset = (int) ev.getRawY() - mLastY;
                // 如果拉到了顶部, 并且是下拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if (isTop() && mYOffset > 0) {
                    return true;
                }
                break;

        }

        // Do not intercept touch event, let the child handle it
        return false;
    }

    /**
     * @return
     */
    protected boolean isTop() {
        // Log.d(VIEW_LOG_TAG,
        // "### first pos = " + contentView.getFirstVisiblePosition()
        // + ", getScrollY= " + getScrollY());
        return mContentView.getFirstVisiblePosition() == 0
                && getScrollY() <= mHeaderView.getMeasuredHeight();
    }

    /**
     * @return
     */
    protected boolean isBottom() {
        // Log.d(VIEW_LOG_TAG, "### last position = " +
        // contentView.getLastVisiblePosition()
        // + ", count = " + contentView.getAdapter().getCount());
        return mContentView != null
                && mContentView.getLastVisiblePosition() == mContentView.getAdapter().getCount() - 1;
    }

    /**
     * 显示footer view
     */
    private void showFooter() {
        startScroll(mFooterView.getMeasuredHeight());
        mCurrentStatus = STATUS_LOADING;

        if (mLoadListener != null) {
            mLoadListener.onLoadMore();
        }
        postDelayed(new Runnable() {

            @Override
            public void run() {
                loadCompelte();
            }
        }, 1000);
    }

    /**
     * @param yOffset
     */
    private void startScroll(int yOffset) {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, yOffset);
        invalidate();
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
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                int currentY = (int) event.getRawY();
                mYOffset = currentY - mLastY;
                // Log.d(VIEW_LOG_TAG, "### distance = " + mYOffset);
                if (mCurrentStatus != STATUS_LOADING) {
                    //
                    changeScrollY(mYOffset);
                }

                rotateHeaderArrow();
                changeTips();
                mLastY = currentY;
                break;

            case MotionEvent.ACTION_UP:
                // 下拉刷新的具体操作
                doRefresh();
                break;
            default:
                break;

        }

        return true;
    }

    /**
     * @param distance
     * @return
     */
    private void changeScrollY(int distance) {
        // 最大值为 scrollY(header 隐藏), 最小值为0 ( header 完全显示).
        int curY = getScrollY();
        // 下拉
        if (distance > 0 && curY - distance > getPaddingTop()) {
            scrollBy(0, -distance);
        } else if (distance < 0 && curY - distance <= mInitScrollY) {
            // 上拉过程
            scrollBy(0, -distance);
        }

        curY = getScrollY();
        int slop = mInitScrollY / 2;
        //
        if (curY > 0 && curY < slop) {
            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
        } else if (curY > 0 && curY > slop) {
            mCurrentStatus = STATUS_PULL_TO_REFRESH;
        }
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

        mProgressBar.setVisibility(View.GONE);
        mArrowImageView.setVisibility(View.VISIBLE);
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
     * 
     */
    private void changeTips() {
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            mTipsTextView.setText(R.string.pull_to_refresh_pull_label);
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            mTipsTextView.setText(R.string.pull_to_refresh_release_label);
        }
    }

    /**
     * @param listener
     */
    public void setOnRefreshListener(OnPullRefreshListener listener) {
        mOnPullRefreshListener = listener;
    }

    /**
     * @param listener
     */
    public void setOnLoadListener(OnLoadListener listener) {
        mLoadListener = listener;
    }

    /**
     * 
     */
    public void refreshComplete() {
        mCurrentStatus = STATUS_IDLE;

        // 200毫秒后处理arrow和progressbar,免得太突兀
        this.postDelayed(new Runnable() {

            @Override
            public void run() {
                mArrowImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }, 200);

    }

    /**
     * 
     */
    public void loadCompelte() {
        // 隐藏footer
        startScroll(mInitScrollY - getScrollY());
        mCurrentStatus = STATUS_IDLE;
    }

    /**
     * 
     */
    private void refreshingHeader() {
        int curScrollY = getScrollY();
        // 超过1/2则认为是有效的下拉刷新, 否则还原
        if (curScrollY < mInitScrollY / 2) {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mHeaderView.getPaddingTop()
                    - curScrollY);
            mCurrentStatus = STATUS_REFRESHING;
            if (mOnPullRefreshListener != null) {
                mOnPullRefreshListener.onRefresh();
            }
            mTipsTextView.setText(R.string.pull_to_refresh_refreshing_label);
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mInitScrollY - curScrollY);
            mCurrentStatus = STATUS_IDLE;
        }

        invalidate();
    }

    /**
     * 
     */
    private void doRefresh() {
        refreshingHeader();
        // 恢复原始的视图
        postDelayed(new Runnable() {

            @Override
            public void run() {
                mScroller.startScroll(getScrollX(), getScrollY(), 0, mInitScrollY - getScrollY());
                invalidate();
                refreshComplete();
                updateTimeStamp();
            }
        }, 2000);
    }

    /**
     * 
     */
    private void updateTimeStamp() {
        // 设置更新时间
        mTimeTextView.setText(R.string.pull_to_refresh_update_time_label);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        mTimeTextView.append(sdf.format(new Date()));
    }

    /**
     * @return
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * @return
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * @return
     */
    public View getFooterView() {
        return mFooterView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);

        int childCount = getChildCount();

        int finalHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // measure
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //
            finalHeight += child.getMeasuredHeight();
        }

        setMeasuredDimension(width, finalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        int top = getPaddingTop();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(0, top, child.getMeasuredWidth(), child.getMeasuredHeight() + top);
            top += child.getMeasuredHeight();
        }

        //
        mInitScrollY = mHeaderView.getMeasuredHeight() + getPaddingTop();
        //
        scrollTo(0, mInitScrollY);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        Log.d(VIEW_LOG_TAG, "### onScrollChanged ");
        if (isBottom() && mScroller.getCurrY() <= mInitScrollY && mYOffset <= 0
                && mCurrentStatus == STATUS_IDLE) {
            showFooter();
        }
    }

}
