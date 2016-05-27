package com.chenyg.uibinder;

import java.util.List;

/**
 * 异步设置
 * Created by ZhuiFeng on 2015/6/12.
 */
public interface AsynSetListener
{
    /**
     * @param prefix 接口前缀
     * @param list
     */
    void toSet(String prefix, List<BinderSet> list);

    /**
     * 用于接收异步设置监听器。
     */
    public interface Receiver
    {
        void receive(AsynSetListener asynSetListener);
    }
}
