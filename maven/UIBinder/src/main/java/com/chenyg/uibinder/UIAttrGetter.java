package com.chenyg.uibinder;

import com.chenyg.wporter.base.AppValues;

/**
 * Created by 宇宙之灵 on 2016/4/28.
 */
public interface UIAttrGetter
{

    public interface Listener
    {
        void onGet(AppValues appValues);
    }

    /**
     * 是否支持同步方式获取属性值.
     *
     * @return
     */
    boolean supportSync();

    /**
     * 用于获取属性值
     *
     * @param listener
     * @param binders  某个Binder可能为null。
     * @param types    若大小为1，则表示后边全部相同
     */
    void asynGetAttrs(Listener listener, Binder[] binders, AttrEnum... types);
}
