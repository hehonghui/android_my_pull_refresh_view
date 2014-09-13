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
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uit.pullrefresh.R;
import com.uit.pullrefresh.listener.OnLoadMoreListener;
import com.uit.pullrefresh.listener.OnPullRefreshListener;

/**
 * @author mrsimple
 * @param <T>
 */
public abstract class PullRefreshBase<T extends View> extends LinearLayout implements
        OnScrollListener {

    /**
     * 
     */
    protected T mContentView;

    /**
     * 
     */
    protected ViewGroup mHeaderView;

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
     * 
     */
    public static final int STATUS_LOADING = 4;
    /**
     * 当前状态
     */
    protected int mCurrentStatus = STATUS_IDLE;
    /**
     * 
     */
    protected MarginLayoutParams mHeaderLayoutParams;

    /**
     * 
     */
    protected int mYDistance = 0;
    /**
     * 
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

    protected ImageView mArrowImageView;

    protected TextView mTipsTextView;

    protected TextView mTimeTextView;

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

    protected int mOriginHeaderPaddingTop = 0;

    /**
     * 
     */
    protected final void initLayout(Context context) {

        //
        initHeaderView();

        //
        initContentView();
        setContentView(mContentView);

        //
        initFooterView();

        //
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //
        mScrHeight = context.getResources().getDisplayMetrics().heightPixels;

    }

    /**
     * 
     */
    protected void initHeaderView() {
        //
        mHeaderView = (ViewGroup) mInflater.inflate(R.layout.umeng_comm_pull_to_refresh_header,
                null);
        mHeaderView.setBackgroundColor(Color.RED);

        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
        mArrowImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_refresh_image);
        mTipsTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mTimeTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        // add header view to parent
        this.addView(mHeaderView, 0);

    }

    /**
     * 
     */
    protected void initFooterView() {
        // //
        ProgressBar footer = new ProgressBar(getContext());
        footer.setIndeterminate(true);
        mFooterView = footer;
        this.addView(mFooterView, 2);
    }

    int footerHeight;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            mHeaderViewHeight = mHeaderView.getHeight();
            // hide header view
            mHeaderLayoutParams = (MarginLayoutParams)
                    mHeaderView.getLayoutParams();
            mHeaderLayoutParams.topMargin = -mHeaderViewHeight;
            // padding
            adjustHeaderPadding(-mHeaderViewHeight);

            footerHeight = mFooterView.getHeight();
            mFooterView.setPadding(mFooterView.getPaddingLeft(),
                    mFooterView.getPaddingTop(), mFooterView.getPaddingRight(),
                    -footerHeight);

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
                int yDistance = (int) ev.getRawY() - mYDown;
                showStatus(mCurrentStatus);
                Log.d(VIEW_LOG_TAG, "%%% isBottom : " + isBottom() + ", isTop : " + isTop()
                        + ", mYDistance : " + mYDistance);
                // 如果拉到了顶部, 并且是下拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if ((isTop() && yDistance > 0)
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
                        if (mHeaderView.getPaddingTop() > mHeaderViewHeight) {
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
     * 
     */
    private final void doRefresh() {
        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            //
            mCurrentStatus = STATUS_REFRESHING;
            //
            mArrowImageView.setVisibility(View.GONE);
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            //
            mPullRefreshListener.onRefresh();
            // 使headview 正常显示, 直到调用了refreshComplete后再隐藏
            adjustHeaderPadding(mHeaderViewHeight + 20);
            //
            mTipsTextView.setText(R.string.pull_to_refresh_refreshing_label);
        } else {
            // 隐藏header view
            adjustHeaderPadding(-mHeaderViewHeight);
        }
    }

    /**
     * 
     */
    private void loadmore() {
        if (isBottom() && mLoadMoreListener != null && mCurrentStatus != STATUS_REFRESHING) {
            mCurrentStatus = STATUS_LOADING;
            adjustFooterPadding(footerHeight);
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
     * 
     */
    public void refreshComplete() {
        mCurrentStatus = STATUS_IDLE;
        mHeaderProgressBar.setVisibility(View.GONE);
        mArrowImageView.setVisibility(View.VISIBLE);
        hideHeaderView();
    }

    /**
     * 
     */
    protected void hideHeaderView() {
        adjustHeaderPadding(-mHeaderViewHeight);
    }

    protected boolean isArrowUp = false;

    /**
     * 
     */
    protected void rotateHeaderArrow() {

        if (mCurrentStatus == STATUS_REFRESHING) {
            return;
        } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH && !isArrowUp) {
            return;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH && isArrowUp) {
            return;
        }

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
    protected void hideFooterView() {

    }

    /**
     * 
     */
    public void loadMoreComplete() {
        mCurrentStatus = STATUS_IDLE;
        adjustFooterPadding(-footerHeight);
    }

    private void adjustFooterPadding(int bottomPadding) {
        mFooterView.setPadding(mFooterView.getPaddingLeft(), mFooterView.getPaddingTop(),
                mFooterView.getPaddingRight(), bottomPadding);
    }

    private void adjustHeaderPadding(int topPadding) {
        mHeaderView.setPadding(mHeaderView.getPaddingLeft(), topPadding,
                mHeaderView.getPaddingRight(), mHeaderView.getPaddingBottom());
    }

    // /**
    // * 调整Padding以实现下拉或者上拉的效果
    // */
    // protected void adjustViewPadding(View view, int distance) {
    // // MarginLayoutParams marginLayoutParams = (MarginLayoutParams)
    // // view.getLayoutParams();
    // // marginLayoutParams.topMargin = distance;
    // // view.setLayoutParams(marginLayoutParams);
    //
    // adjustPadding(distance);
    //
    // Log.d(VIEW_LOG_TAG, "### adjustViewPadding : view : " + view);
    // }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        loadmore();
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
