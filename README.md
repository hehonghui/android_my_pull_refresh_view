android_my_pull_refresh_view
============================
# 概述
> 这是一个通用的下拉刷新、上拉自动加载的组件，该组件继承自LinearLayout,方向为竖直布局，由三部分组成，分别是Header、ContentView、Foooter,其中ContentView的宽高都为match_parent,footer和header的宽、高分别为match_parent、wrap_content，在Header、Foooter初始时都会通过设置padding隐藏掉，只有
ContentView区域显示出来。当用户下拉到顶端，并且继续下拉时触发下拉刷新操作；当用户上拉到底部，
>  并且继续上拉时触发加载更多的操作。     
	更多内容请参考我的博客, <a href="http://blog.csdn.net/bboyfeiyu/article/details/39718861" target="_blank">CSDN博客</a>
   
   **该项目中的库都是用于阐述基本原理，不建议使用到项目中。**     
   
##一、布局示意图
**原始布局**     
![Alt text](http://img.blog.csdn.net/20140913165858954?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYmJveWZlaXl1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)      

**设置padding后headerh和footer偏移出屏幕**      
![Alt text](http://img.blog.csdn.net/20140913165828046?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYmJveWZlaXl1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)     


## 二、已有组件的使用示例
   下面的例子都是在一个Activity中演示。    
### 2.1 使用PullRefreshListView
```java
public class MainActivity extends Activity {

    ListView mListView;

    PullRefreshListView mPullRefreshListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 下拉刷新的listview
        mPullRefreshListView = new PullRefreshListView(this);
        // 获取listview 对象, 即PullRefreshListView中的mContentView
        mListView = mPullRefreshListView.getContentView();

        List<String> datas = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            datas.add(" Item - " + i);
        }

        // 设置adapter
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                datas));

        // 下拉刷新
        mPullRefreshListView.setOnRefreshListener(new OnPullRefreshListener() {

            @Override
            public void onRefresh() {

                Toast.makeText(getApplicationContext(), "refresh", Toast.LENGTH_SHORT).show();
                mPullRefreshListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mPullRefreshListView.refreshComplete();
                    }
                }, 2000);
            }
        });
        // 上拉自动加载
        mPullRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                Toast.makeText(getApplicationContext(), "load more", Toast.LENGTH_SHORT).show();
                mPullRefreshListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mPullRefreshListView.loadMoreComplete();
                    }
                }, 1500);
            }
        });


        setContentView(mPullRefreshListView);

    }
}
```       
  **下拉刷新截图**   
  
  ![Alt text](http://img.blog.csdn.net/20140913171130673?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYmJveWZlaXl1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)      
  
  **上拉到底部的自动加载**     
  ![Alt text](http://img.blog.csdn.net/20140913171130673?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYmJveWZlaXl1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)       

### 2.2 使用可下拉刷新的TextView
```java

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

 // PullRefreshTextView
        final PullRefreshTextView pullRefreshTextView = new PullRefreshTextView(this);
        pullRefreshTextView.getContentView().setText("下拉刷新TextView");
        // 下拉刷新
        pullRefreshTextView.setOnRefreshListener(new OnPullRefreshListener() {

            @Override
            public void onRefresh() {
                pullRefreshTextView.getContentView().setText(new Date().toGMTString());
                pullRefreshTextView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pullRefreshTextView.refreshComplete();
                    }
                }, 1000);
            }
        });

        // 上拉自动加载, TextView不能设置scroll listener ，所以无效
        pullRefreshTextView.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                Toast.makeText(getApplicationContext(), "textview load", Toast.LENGTH_SHORT).show();
                pullRefreshTextView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pullRefreshTextView.loadMoreComplete();
                    }
                }, 1000);
            }
        });

         setContentView(pullRefreshTextView);


    }
}
```    
**截图**     

![Alt text](http://img.blog.csdn.net/20140913171316628?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYmJveWZlaXl1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
        
## 三、扩展该组件
### 3.1、继承自PullRefreshBase<T>
	T为你要实现下拉刷新的View的类型，如ListView.       
	
### 3.2、初始化ContentView
  覆写initContentView方法，并且在该函数中初始化mContentView对象。我们以ListView为例，例如 :       
```java
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
 ```      
     
### 3.3、覆写判断是否滑动到顶端和底部的方法
   我们以ListView为例，例如 :    
```java
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
```    
### 3.4使用即可

    
