package com.chenyg.uibinder.android.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected Context context;
    private List<T> list;
    private boolean loadFinished;
    private boolean isFirstInTop = true;

    /**
     * @param context
     * @param list
     * @param offset
     * @param count
     * @param isFirstInTop 第一项是否从上面开始，true，从上面开始，false，从底部开始。
     */
    public MyBaseAdapter(Context context, List<T> list, int offset, int count, boolean isFirstInTop) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.list = new ArrayList<T>();
        this.isFirstInTop = isFirstInTop;
        this.add(list, offset, count);
    }

    /**
     *
     * @param context
     * @param isFirstInTop
     */
    public MyBaseAdapter(Context context, boolean isFirstInTop) {
        this(context, null, 0, 0, isFirstInTop);
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size() + (isLoadFinished() ? 0 : 1);
    }

    public int size() {
        return this.list.size();
    }

    /**
     * 得到指定位置的T
     */
    @Override
    public T getItem(int position) {
        // TODO Auto-generated method stub
        if (isFirstInTop) {
            if (position >= size()) {
                return null;
            }
            return list.get(position);
        } else {
            if (position <= 0) {
                if (loadFinished) {
                    return list.get(size() - 1);
                } else {
                    return null;
                }

            } else {
                int index = getCount() - 1 - position;
                return list.get(index);
            }
        }

    }

    /**
     * 得到会话id.默认返回0.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (isFirstInTop) {
            if (position >= size() && !isLoadFinished()) {
                ProgressBar progressBar = new ProgressBar(context);
                return progressBar;
            } else {
                return null;
            }
        } else {
            if (position == 0 && !isLoadFinished()) {
                ProgressBar progressBar = new ProgressBar(context);
                return progressBar;
            } else {
                return null;
            }
        }

    }

    public void add(T t) {
        this.list.add(t);
    }

    public void add(T t, int position) {
        if (position >= 0 && position <= this.list.size())
            this.list.add(position, t);
    }


    /**
     * 构造一个适配器
     *
     * @param ts     为null，则什么都不添加
     * @param offset
     * @param count
     */
    public void add(T[] ts, int offset, int count) {
        if (ts != null) {
            if (count > ts.length) {
                count = ts.length;
            }
            for (int i = 0; i < count; i++) {
                this.list.add(ts[offset + i]);
            }
        }

    }

    /**
     * 移除指定位置元素
     *
     * @param position
     */
    public void remove(int position) {
        this.list.remove(position);
    }

    /**
     * 添加
     *
     * @param set
     */
    public void add(Set<T> set) {
        List<T> list = new ArrayList<T>();
        Iterator<T> iterator = set.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        add(list, 0, list.size());
    }

    /**
     * 构造一个适配器
     *
     * @param list   为null，则什么都不添加
     * @param offset
     * @param count
     */
    public void add(List<T> list, int offset, int count) {
        if (list != null) {
            if (count > list.size()) {
                count = list.size();
            }
            for (int i = 0; i < count; i++) {
                this.list.add(list.get(offset + i));
            }
        }

    }

    /**
     * 清楚所有数据
     */
    public void clear() {
        this.list.clear();
    }

    public void setLoadFinished(boolean loadFinished) {
        this.loadFinished = loadFinished;
    }

    public boolean isLoadFinished() {
        return loadFinished;
    }

}
