package com.liuzhe.simpleloadmorelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 自动加载更多ListView
 * Created by LiuZhe on 2015/9/23.
 */
public class AutoLoadListView extends ListView {

    View footerView; // 底部FooterView
    private boolean loadMoreEnabled = true; // 加载更多是否可用
    private int status; // 当前加载状态
    private static final int LOADING = 1; // 正在加载更多
    private static final int LOADED = 2; // 加载完成
    private int mLastItemIndex; // 最后一项
    private OnLoadMoreListener mLoadMoreListener; // 加载更多回调接口

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
     *加载更多回调接口
     * Created by LiuZhe on 2015/9/24.
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /**
     * 设置加载更多回调接口
     * @param loadMoreListener 加载更多回调接口
     * Created by LiuZhe on 2015/9/24.
     */
    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
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
     * @param loadMoreEnabled 加载更多是否可用
     * Created by LiuZhe on 2015/9/24.
     */
    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        this.loadMoreEnabled = loadMoreEnabled;

//        removeFooterView(footerView); // 可以选择删除footerView
    }

    public void setTipText(String text) {
        TextView tv = (TextView) footerView.findViewById(android.R.id.text1);
        tv.setText(text);
        footerView.findViewById(android.R.id.progress).setVisibility(GONE);
    }

    /*
    初始化
     */
    private void init(final Context context) {

        // 初始化FooterView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.footer_view, null);

        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mLastItemIndex = firstVisibleItem + visibleItemCount; // 第一个可见的编号 + 总共有多少个可见的Item
                if (mLastItemIndex == totalItemCount && loadMoreEnabled) {
                    if (null != mLoadMoreListener && status != LOADING) {
                        mLoadMoreListener.onLoadMore();
                        status = LOADING;
                        // 不是加载第一页且没有FooterView的时候才addFooterView
                        if(totalItemCount > getFooterViewsCount() && getFooterViewsCount() == 0) {
                            addFooterView(footerView);
                        }
                    }
                }

            }
        });
    }
}
