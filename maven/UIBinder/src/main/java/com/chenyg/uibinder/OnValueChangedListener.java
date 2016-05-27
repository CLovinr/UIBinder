package com.chenyg.uibinder;

/**
 * Created by ZhuiFeng on 2015/7/11.
 */
public interface OnValueChangedListener {
    /**
     *
     * @param prefix 接口前缀
     * @param funTiedName 接口方法绑定名
     * @param varName 该控件绑定的变量名称
     * @param value 控件当前的值
     */
     void onChanged(String prefix, String funTiedName, String varName, Object value);
}
