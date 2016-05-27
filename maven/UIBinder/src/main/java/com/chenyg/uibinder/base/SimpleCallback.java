package com.chenyg.uibinder.base;


/**
 * Created by ZhuiFeng on 2015/6/7.
 */
 public interface SimpleCallback{

    /**
     * 完成时的回调
     */
    public void onDone();

    /**
     * 中断时的回调
     * @param info 描述信息
     */
    public void onInterupt(String info);
}
