android_my_pull_refresh_view
============================
# 概述
    这是一个通用的下拉刷新、上拉自动加载的组件，该组件继承自LinearLayout,方向为竖直布局，由三部分组成，分别是Header、ContentView、Foooter,其中的宽高都为match_parent,另外两个都为宽、高分别为match_parent、wrap_content，且Header、Foooter在初始时都会通过设置padding隐藏掉，只有ContentView区域显示出来。当用户下拉到顶端，并且继续下拉时触发下拉刷新操作；当用户上拉到底部，并且继续上拉时触发加载更多的操作。
## 布局示意图
**原始布局**
![Alt text] (http://img.blog.csdn.net/20140913165858954?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYmJveWZlaXl1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

**设置padding后headerh和footer偏移出屏幕**
![Alt text] (http://img.blog.csdn.net/20140913165828046?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYmJveWZlaXl1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)




    
