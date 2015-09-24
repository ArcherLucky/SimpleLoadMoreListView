package com.liuzhe.simpleloadmorelistview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int num;
    List<String> list;
    ArrayAdapter adapter;
    AutoLoadListView listView;
    AutoLoadListView.OnLoadMoreListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        listView = (AutoLoadListView) findViewById(android.R.id.list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                list);
        listView.setAdapter(adapter);
        listener = new AutoLoadListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                addData();
            }
        };
        listView.setLoadMoreListener(listener);
    }

    private void addData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 20; i++) {
                    list.add("item " + num);
                    num++;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        listView.loadComplete();
                        if (num >= 99) {
                            listView.setLoadMoreEnabled(false);
                            listView.setTipText("加载完啦");
                        }
                    }
                });

            }
        }).start();

    }

}
