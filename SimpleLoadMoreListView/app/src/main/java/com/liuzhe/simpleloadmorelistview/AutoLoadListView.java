package com.liuzhe.simpleloadmorelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 自动加载更多ListView
 * Created by LiuZhe on 2015/9/23.
 */
public class AutoLoadListView extends ListView {

    private static String TAG = "AutoLoadListView";
    int length;

    enum REFRESHING_STATE {
        REFRESHING, DONE, PRE_REFRESH, CANCEL_REFRESH;
    }

    float startY;

    int headerContentHeight;

    View headerView; // 顶部HeaderView
    View footerView; // 底部FooterView
    TextView tv;
    ProgressBar progressBar;
    private boolean loadMoreEnabled = true; // 加载更多是否可用
    private int status; // 当前加载状态
    private static final int LOADING = 1; // 正在加载更多
    private static final int LOADED = 2; // 加载完成
    private int mLastItemIndex; // 最后一项
    private int firstItem;
    private OnListViewChangeListener mLoadMoreListener; // 加载更多回调接口

    REFRESHING_STATE refreshing_state = REFRESHING_STATE.DONE;

    public AutoLoadListView(Context context) {
        super(context);
        init(context);
    }

    public AutoLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoLoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 加载更多回调接口
     * Created by LiuZhe on 2015/9/24.
     */
    public interface OnListViewChangeListener {
        void onLoadMore();

        void onRefresh();
    }

    /**
     * 设置加载更多回调接口
     *
     * @param loadMoreListener 加载更多回调接口
     *                         Created by LiuZhe on 2015/9/24.
     */
    public void setLoadMoreListener(OnListViewChangeListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    /**
     * 当加载成功后，可调用此方法
     * Created by LiuZhe on 2015/9/24.
     */
    public void loadComplete() {
        status = LOADED;
    }


    /**
     * 设置加载更多是否可用
     *
     * @param loadMoreEnabled 加载更多是否可用
     *                        Created by LiuZhe on 2015/9/24.
     */
    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        this.loadMoreEnabled = loadMoreEnabled;
    }

    private void setTipText(String text) {
        tv.setText(text);
        tv.setVisibility(VISIBLE);
    }

    public void onRefreshComplete() {
        refreshing_state = REFRESHING_STATE.DONE;
        headerView.setPadding(0, -headerContentHeight - 20, 0, -20);
        progressBar.setVisibility(GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                length += (int) (moveY - startY);

                if (length > 250) {
                    setTipText("松开刷新");
                } else {
                    setTipText("下拉刷新");
                }
                if (length > 500) {
                    length = 500;
                }
                startY = moveY;
                headerView.setPadding(0, length/2, 0, length/2);

                break;
            case MotionEvent.ACTION_UP:
                if (length > 250 && firstItem == 0) {
                    refreshing_state = REFRESHING_STATE.PRE_REFRESH;
                    headerView.setPadding(0, 20, 0, 20);
                } else {
                    refreshing_state = REFRESHING_STATE.CANCEL_REFRESH;
                    headerView.setPadding(0, - (headerContentHeight / 2), 0, - (headerContentHeight / 2));
                }

                length = 0;
                break;
        }
        return super.onTouchEvent(ev);
    }

    /*
        初始化
         */
    private void init(final Context context) {

        // 初始化FooterView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = inflater.inflate(R.layout.header_view, null);
        footerView = inflater.inflate(R.layout.footer_view, null);
        footerView.setVisibility(GONE);
        tv = (TextView) headerView.findViewById(android.R.id.text1);
        progressBar = (ProgressBar) headerView.findViewById(android.R.id.progress);
        progressBar.setVisibility(GONE);
        tv.setVisibility(GONE);
        headerContentHeight = headerView.getMeasuredHeight();
        addHeaderView(headerView);
        addFooterView(footerView);
        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (refreshing_state == REFRESHING_STATE.PRE_REFRESH && firstItem == 0) {
                    refreshing_state = REFRESHING_STATE.REFRESHING;
                    progressBar.setVisibility(VISIBLE);
                    mLoadMoreListener.onRefresh();
                    setTipText("刷新中");
                    return;
                }
                if(firstVisibleItem == 0) {
                    headerView.setVisibility(VISIBLE);
                }
                if (refreshing_state == REFRESHING_STATE.CANCEL_REFRESH) {
                    refreshing_state = REFRESHING_STATE.DONE;
                    progressBar.setVisibility(GONE);
                    return;
                }

                mLastItemIndex = firstVisibleItem + visibleItemCount; // 第一个可见的编号 + 总共有多少个可见的Item
                firstItem = firstVisibleItem;
                if (mLastItemIndex == totalItemCount && loadMoreEnabled) {
                    if (null != mLoadMoreListener && status != LOADING) {
                        mLoadMoreListener.onLoadMore();
                        status = LOADING;
                        footerView.setVisibility(VISIBLE);
                    }
                }

            }
        });
    }
}
