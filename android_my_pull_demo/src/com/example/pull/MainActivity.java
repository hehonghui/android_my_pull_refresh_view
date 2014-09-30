/**
 *
 *	created by Mr.Simple, Sep 12, 201411:18:06 AM.
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

package com.example.pull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.uit.pullrefresh.base.impl.PullRefreshListView;
import com.uit.pullrefresh.base.impl.PullRefreshTextView;
import com.uit.pullrefresh.listener.OnLoadListener;
import com.uit.pullrefresh.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mrsimple
 */
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
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {

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
        mPullRefreshListView.setOnLoadMoreListener(new OnLoadListener() {

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

        // setContentView(mPullRefreshListView);

        // setContentView(R.layout.umeng_comm_pull_to_refresh_header);

        // PullRefreshTextView
        final PullRefreshTextView pullRefreshTextView = new PullRefreshTextView(this);
        pullRefreshTextView.getContentView().setText("下拉刷新TextView");
        // 下拉刷新
        pullRefreshTextView.setOnRefreshListener(new OnRefreshListener() {

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
        pullRefreshTextView.setOnLoadMoreListener(new OnLoadListener() {

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

        // setContentView(pullRefreshTextView);

        //
        setContentView(R.layout.pull_refresh_main);

        findViewById(R.id.refresh_listview).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.REFRESH_LV);
                startActivity(intent);
            }
        });

        findViewById(R.id.refresh_gridview).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.REFRESH_GV);
                startActivity(intent);
            }
        });

        findViewById(R.id.refresh_textview).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.REFRESH_TV);
                startActivity(intent);
            }
        });

    }
}
